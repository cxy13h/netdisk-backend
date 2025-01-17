package com.example.netdisk.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 队列名称
    @Value("${mq.upload.queue}")
    private String uploadQueueName;

    // 交换机名称
    @Value("${mq.upload.exchange}")
    private String uploadExchangeName;

    // 路由键
    @Value("${mq.upload.routingKey}")
    private String uploadRoutingKey;

    /**
     * 定义上传队列
     */
    @Bean
    public Queue uploadQueue() {
        return new Queue(uploadQueueName, true); // true 表示队列持久化
    }

    /**
     * 定义上传交换机
     */
    @Bean
    public DirectExchange uploadExchange() {
        return new DirectExchange(uploadExchangeName, true, false); // 持久化，非自动删除
    }

    /**
     * 定义队列和交换机的绑定关系
     */
    @Bean
    public Binding uploadBinding(Queue uploadQueue, DirectExchange uploadExchange) {
        return BindingBuilder.bind(uploadQueue).to(uploadExchange).with(uploadRoutingKey);
    }

    /**
     * 配置 RabbitMQ 连接工厂
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost"); // RabbitMQ 服务地址
        factory.setPort(5672); // 默认端口
        factory.setUsername("guest"); // 默认用户名
        factory.setPassword("guest"); // 默认密码
        return factory;
    }

    /**
     * 配置 RabbitTemplate（用于消息发送）
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter()); // 使用 JSON 消息转换器
        return rabbitTemplate;
    }

    /**
     * 配置监听器容器工厂（用于消费者监听）
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter()); // 使用 JSON 消息转换器
        factory.setConcurrentConsumers(5); // 设置并发消费者数量
        factory.setMaxConcurrentConsumers(10); // 设置最大并发消费者数量
        return factory;
    }
}
