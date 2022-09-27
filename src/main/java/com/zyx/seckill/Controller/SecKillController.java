package com.zyx.seckill.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wf.captcha.ArithmeticCaptcha;
import com.zyx.seckill.config.AccessLimit;
import com.zyx.seckill.exception.GlobalException;
import com.zyx.seckill.pojo.*;
import com.zyx.seckill.rabbitmq.MQSender;
import com.zyx.seckill.service.IGoodsService;
import com.zyx.seckill.service.IOrderService;
import com.zyx.seckill.service.ISeckillOrderService;
import com.zyx.seckill.utils.JsonUtil;
import com.zyx.seckill.vo.GoodsVo;
import com.zyx.seckill.vo.RespBean;
import com.zyx.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀
 */
@Slf4j
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> script;
    //用于内存标记，参数：<秒杀商品id，库存是否为空>
    private Map<Long,Boolean> emptyStockMap = new HashMap<>();


    /**
     * 秒杀
     * @param
     * @param
     * @param
     * @return
     */
//    @RequestMapping("/doSecKill2")
//    public String doSecKill2(Model model, User user, Long goodsId){
//        if (null == user){
//            return "login";
//        }
//        model.addAttribute("user", user);
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        //该商品无库存
//        if (goods.getStockCount() < 1){
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
//            return "secKillFail";
//        }
//        LambdaQueryWrapper<SeckillOrder> seckillOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        seckillOrderLambdaQueryWrapper.eq(SeckillOrder::getUserId, user.getId()).eq(SeckillOrder::getGoodsId, goodsId);
//        SeckillOrder seckillOrder = seckillOrderService.getOne(seckillOrderLambdaQueryWrapper);
//        //该商品没人限购一件
//        if (null != seckillOrder){
//            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
//            return "secKillFail";
//        }
//        Order order = orderService.secKill(user, goods);
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goods);
//        return "orderDetail";
//    }
//

    /**
     * 系统初始化，将秒杀商品库存加载到Redis中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(goodsVos) || goodsVos.size() == 0){
            return;
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        for (GoodsVo goodsVo : goodsVos){
            valueOperations.set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            //内存标记，表示当前秒杀商品库存不为空
            emptyStockMap.put(goodsVo.getId(), false);
        }
    }

    /**
     * 秒杀
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping( "/{path}/doSeckill")
    @ResponseBody//异步获取 json数据，加上@ResponseBody后，会直接返回json数据。
    public RespBean doSecKill(@PathVariable String path, User user, Long goodsId){
        if (null == user){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        /*
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //该商品无库存
        if (goods.getStockCount() < 1){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断单用户是否重复抢购
//        LambdaQueryWrapper<SeckillOrder> seckillOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        seckillOrderLambdaQueryWrapper.eq(SeckillOrder::getUserId, user.getId()).eq(SeckillOrder::getGoodsId, goodsId);
//        SeckillOrder seckillOrder = seckillOrderService.getOne(seckillOrderLambdaQueryWrapper);

        //这里不再去数据库中查询秒杀订单了，而是去Redis缓存中查询，进一步优化
        String seckillOrderJson = (String) redisTemplate.opsForValue().get("order" + user.getId() + ":" + goods.getId());
        if (!StringUtils.isEmpty(seckillOrderJson)) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        Order order = orderService.secKill(user, goods);
        if (null != order) {
            return RespBean.success(order);
        }
        return RespBean.error(RespBeanEnum.ERROR);
         */



        //判断秒杀路径是否合法
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Boolean check = orderService.checkPath(user, goodsId, path);
        if (!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //判断同一用户是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (null != seckillOrder) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //根据内存标记判断当前秒杀商品库存是否为空，将访问Redis的次数部分转移到map中，实现减少对Redis的访问
        if (emptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //Redis预减库存, 原子操作
//        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);//递减之后的库存
        //使用lua脚本
        Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
        if (stock == 0){
            //内存标记，表示当前秒杀商品库存为空
            emptyStockMap.put(goodsId, true);
//            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //创建秒杀消息对象，并进入到RabbitMQ消息队列中缓冲
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);//由于使用RabbitMQ消息队列，异步生成订单，这一步会快速返回秒杀请求，达到流量削峰的目的
    }

    /**
     * 获取用户秒杀的结果
     * @param user
     * @param goodsId
     * @return order:成功；-1:失败； 0:排队中
     */
    @RequestMapping("/result")
    @ResponseBody
    public RespBean getResult(User user, String goodsId) {
        if(user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long seckillOrderId = seckillOrderService.getSeckillResult(user, goodsId);
        if (seckillOrderId != null){
            return RespBean.success(seckillOrderId);
        }
        return RespBean.success(RespBeanEnum.EMPTY_STOCK);
    }


    /**
     * 获取秒杀接口地址
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping( "/path")
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        //判断验证码是否正确
        Boolean check = orderService.checkCptcha(user, goodsId, captcha);
        if(!check) {
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }

        //使用UUID生成随机值并使用MD5加密一下然后放入redis
        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);
    }


    /**
     * 生成验证码
     * @param user
     * @param goodsId
     * @param response
     */
    @RequestMapping("/captcha")
    public void vertifyCode(User user, Long goodsId, HttpServletResponse response) {
        if(user == null || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.ERROR);
        }

        //设置请求头为输出图片数据
        response.setContentType("image/jpg");
        response.setHeader("Pargam","No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码，将结果放入redis中
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        String key = "captcha:" + user.getId() + ":" + goodsId;
        redisTemplate.opsForValue().set(key, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.info("验证码生成失败", e.getStackTrace());
        }

    }
}
