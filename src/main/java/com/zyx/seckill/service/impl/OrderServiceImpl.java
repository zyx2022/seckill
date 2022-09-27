package com.zyx.seckill.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyx.seckill.exception.GlobalException;
import com.zyx.seckill.mapper.OrderMapper;
import com.zyx.seckill.pojo.Order;
import com.zyx.seckill.pojo.SeckillGoods;
import com.zyx.seckill.pojo.SeckillOrder;
import com.zyx.seckill.pojo.User;
import com.zyx.seckill.service.IGoodsService;
import com.zyx.seckill.service.IOrderService;
import com.zyx.seckill.service.ISeckillGoodsService;
import com.zyx.seckill.service.ISeckillOrderService;
import com.zyx.seckill.utils.MD5Util;
import com.zyx.seckill.utils.UUIDUtil;
import com.zyx.seckill.vo.GoodsVo;
import com.zyx.seckill.vo.OrderDetailVo;
import com.zyx.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 * zyx
 * @author jobob
 * @since 2022-07-23
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    @Override
    public Order secKill(User user, GoodsVo goods) {
        //拿到秒杀商品
        LambdaQueryWrapper<SeckillGoods> seckillGoodsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        seckillGoodsLambdaQueryWrapper.eq(SeckillGoods::getGoodsId, goods.getId());
        SeckillGoods secKillGoods = seckillGoodsService.getOne(seckillGoodsLambdaQueryWrapper);
        //判断库存是否大于0
        UpdateWrapper<SeckillGoods> seckillGoodsUpdateWrapper = new UpdateWrapper<>();
        seckillGoodsUpdateWrapper.eq("id", secKillGoods.getId());
        seckillGoodsUpdateWrapper.gt("stock_count", 0);
        seckillGoodsUpdateWrapper.setSql("stock_count=" + "stock_count-1");
        boolean secKillres = seckillGoodsService.update(seckillGoodsUpdateWrapper);
//        if(!secKillres) {
//            return null;
//        }
        //通过Redis，判断秒杀商品是否还有库存
        if (secKillGoods.getStockCount() < 1){
            redisTemplate.opsForValue().set("isStockEmpty:" + goods.getId(), "0");
            return null;
        }
        //生成订单（后面秒杀订单需要用到关联订单的id）
        Order order = new Order(user.getId(), goods.getId(), 0L, goods.getGoodsName(), 1, secKillGoods.getSeckillPrice(), 1, 0, new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder(user.getId(), order.getId(), goods.getId());
        seckillOrderService.save(seckillOrder);
        //将秒杀订单信息存入Redis，方便判断是否重复抢购时进行查询
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), seckillOrder);
        //返回订单
        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null){
            throw  new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        Long goodsId = order.getGoodsId();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoodsVo(goodsVo);
        return orderDetailVo;
    }

    /**
     * 通过UUID，创建随机路径
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String uuid = UUIDUtil.uuid() + "123456";
        String path = MD5Util.md5(uuid);
        String key = "seckillPath:" + user.getId() + ":" + goodsId;
        redisTemplate.opsForValue().set(key, path, 60, TimeUnit.SECONDS);
        return path;
    }

    /**
     * 校验秒杀地址
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    @Override
    public Boolean checkPath(User user, Long goodsId, String path) {
        if (null == user || goodsId < 0 || StringUtils.isEmpty(path)){
            return false;
        }
        String key = "seckillPath:" + user.getId() + ":" + goodsId;
        String redisPath = (String) redisTemplate.opsForValue().get(key);
        if(redisPath == null || !redisPath.equals(path)) {
            return false;
        }
        return true;
    }

    /**
     * 校验验证码
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public Boolean checkCptcha(User user, Long goodsId, String captcha) {
        if(StringUtils.isEmpty(captcha) || user == null || goodsId < 0) {
            return false;
        }
        String key = "captcha:" + user.getId() + ":" + goodsId;
        String redisCaptcha = (String) redisTemplate.opsForValue().get(key);
        if(redisCaptcha == null || !redisCaptcha.equals(captcha)) {
            return false;
        }
        return true;
    }
}
