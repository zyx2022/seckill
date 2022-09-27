//package com.zyx.seckill.config;
//
//
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.FanoutExchange;
//import org.springframework.amqp.core.Queue;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * 准备队列
// */
//@Configuration
//public class RabbitMQConfigFanout {
//    private static final String QUEUE01 = "queue_fanout01";
//    private static final String QUEUE02 = "queue_fanout02";
//    private static final String EXCHANGE = "fanoutExchange";
//
//    //生成队列，并持久化
//    @Bean
//    public Queue queue() {
//        return new Queue("queue", true);
//    }
//
//    //准备两个队列。一个交换机，并将它们进行绑定
//    @Bean
//    public Queue queue01() {
//        return new Queue(QUEUE01);
//    }
//    @Bean
//    public Queue queue02() {
//        return new Queue(QUEUE02);
//    }
//    @Bean
//    public FanoutExchange fanoutExchange() {
//        return new FanoutExchange(EXCHANGE);
//    }
//    @Bean
//    public Binding binding01() {
//        return BindingBuilder.bind(queue01()).to(fanoutExchange());
//    }
//    @Bean
//    public Binding binding02() {
//        return BindingBuilder.bind(queue02()).to(fanoutExchange());
//    }
//}