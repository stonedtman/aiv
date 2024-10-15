package com.yys.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQconfig {

    // 声明一个公共静态常量QUEUE_NAME，用于存储队列名称"json_queue"
    public static final String QUEUE_NAME = "json_queue";

    // 声明一个公共静态常量EXCHANGE_NAME，用于存储交换机名称"training_exchange"
    public static final String EXCHANGE_NAME = "training_exchange";

    // 声明一个公共静态常量ROUTING_KEY，用于存储路由键"training.routing.key"
    public static final String ROUTING_KEY = "training.routing.key";

    // 声明队列
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true); //队列持久化
    }

    // 声明交换器
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // 将队列与交换器绑定
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}



