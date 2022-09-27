package com.zyx.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyx.seckill.mapper.SeckillOrderMapper;
import com.zyx.seckill.pojo.SeckillOrder;
import com.zyx.seckill.pojo.User;
import com.zyx.seckill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取用户秒杀的结果
     * @param user
     * @param goodsId
     * @return 成功返回orderId；秒杀失败返回-1，排队中返回0
     */
    @Override
    public Long getSeckillResult(User user, String goodsId) {
        LambdaQueryWrapper<SeckillOrder> seckillOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        seckillOrderLambdaQueryWrapper.eq(SeckillOrder::getUserId, user.getId());
        seckillOrderLambdaQueryWrapper.eq(SeckillOrder::getGoodsId, goodsId);
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(seckillOrderLambdaQueryWrapper);
        if (null != seckillOrder){//生成秒杀订单成功，秒杀成功
            return seckillOrder.getOrderId();
        }else if (redisTemplate.hasKey("isStockEmpty:" + goodsId)){
            //在OrderServiceImpl中使用Redis，判断秒杀商品是否还有库存，若有key，说明没有库存，秒杀失败
            return -1L;
        }else {
            //排队中
            return 0L;
        }
    }
}
