package com.zyx.seckill.Controller;


import com.zyx.seckill.pojo.User;
import com.zyx.seckill.rabbitmq.MQSender;
import com.zyx.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 * zyx
 * @author jobob
 * @since 2022-07-14
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private MQSender mqSender;


//    /**
//     * 功能描述: 用户信息(测试)
//     *
//     * @param:
//     * @return:
//     */
//    @RequestMapping("/info")
//    @ResponseBody
//    public RespBean info(User user) {
//        return RespBean.success(user);
//    }
//
//
//    /**
//     * 功能描述: 测试发送RabbitMQ消息
//     *
//     * @param:
//     * @return:
//     */
//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq() {
//        mqSender.send01("hello");
//    }
//
//    //Fanout模式 交换机发送消息给消费者
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public void mq01() {
//        mqSender.send02("hello");
//    }
//
//    //direct模式 交换机发送消息给消费者
//    @RequestMapping("/mq/direct01")
//    @ResponseBody
//    public void mq03() {
//        mqSender.send03("Hello, Red");
//    }
//    @RequestMapping("/mq/direct02")
//    @ResponseBody
//    public void mq04() {
//        mqSender.send04("Hello, Green");
//    }
//
//    //topic模式  交换机发送消息给消费者
//    @RequestMapping("/mq/topic01")
//    @ResponseBody
//    public void mq05() {
//        mqSender.send05("Hello");
//    }
//    @RequestMapping("/mq/topic02")
//    @ResponseBody
//    public void mq06() {
//        mqSender.send06("Hello");
//    }
//
//    //topic模式  交换机发送消息给消费者
//    @RequestMapping("/mq/header01")
//    @ResponseBody
//    public void mq07() {
//        mqSender.send07("Hello, Header01");
//    }
//    @RequestMapping("/mq/header02")
//    @ResponseBody
//    public void mq08() {
//        mqSender.send08("Hello, Header02");
//    }
}
