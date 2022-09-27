package com.zyx.seckill.rabbitmq;

import com.zyx.seckill.pojo.SeckillMessage;
import com.zyx.seckill.pojo.SeckillOrder;
import com.zyx.seckill.pojo.User;
import com.zyx.seckill.service.IGoodsService;
import com.zyx.seckill.service.IOrderService;
import com.zyx.seckill.utils.JsonUtil;
import com.zyx.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
@Slf4j
public class MQReceiver {

//    //监听接收队列"queue"中的消息
//    @RabbitListener(queues="queue")
//    public void receive(Object msg) {
//        log.info("接收消息：" + msg);
//    }
//
//    //Fanout模式 消费者接收消息
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg) {
//        log.info("QUEUE01接收消息：" + msg);
//    }
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg) {
//        log.info("QUEUE02接收消息：" + msg);
//    }
//
//    //direct模式 消费者接收消息
//    @RabbitListener(queues = "queue_direct01")
//    private void receive03(Object msg) {
//        log.info("QUEUE01接收消息：" + msg);
//    }
//    @RabbitListener(queues = "queue_direct02")
//    private void receive04(Object msg) {
//        log.info("QUEUE02接收消息：" + msg);
//    }
//
//    //topic模式 消费者接收消息
//    @RabbitListener(queues = "queue_topic01")
//    private void receive05(Object msg) {
//        log.info("QUEUE01接收消息：" + msg);
//    }
//    @RabbitListener(queues = "queue_topic02")
//    private void receive06(Object msg) {
//        log.info("QUEUE02接收消息：" + msg);
//    }
//
//    //header模式 消费者接收消息
//    @RabbitListener(queues = "queue_hearder01")
//    public void receive07(Message message) {
//        log.info("QUEUE01接收Message对象：" + message);
//        log.info("QUEUE01接收消息：" + new String(message.getBody()));
//    }
//    @RabbitListener(queues = "queue_hearder02")
//    public void receive08(Message message) {
//        log.info("QUEUE02接收Message对象：" + message);
//        log.info("QUEUE02接收消息：" + new String(message.getBody()));
//    }

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

    /**
     * 监听接收队列"queue"中的消息，进行下单操作，也就是真正实现在数据库中减少库存
     * @param message
     */
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) {
        log.info("接收到订单消息:",message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();

        //根据商品id获取商品信息，判断库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() < 1) {
            return;
        }
        //再次判断是否重复抢购
        ValueOperations valueOperations = redisTemplate.opsForValue();
        SeckillOrder seckillOrder = (SeckillOrder)valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null) {
            return;
        }

        //下单操作
        try {
            orderService.secKill(user, goodsVo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return;
        }

    }
}