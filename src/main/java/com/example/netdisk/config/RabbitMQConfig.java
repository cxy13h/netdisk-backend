package com.example.netdisk.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 从配置文件读取队列名称
    @Value("${mq.chunk.queue}")
    private String chunkUploadQueue;

    @Value("${mq.merge.queue}")
    private String fileMergeQueue;

    // 定义交换机名称
    public static final String DIRECT_EXCHANGE = "upload.direct.exchange";

    // 定义路由键
    public static final String CHUNK_ROUTING_KEY = "chunk.routing.key";
    public static final String MERGE_ROUTING_KEY = "merge.routing.key";

    /**
     * 定义分片上传队列
     */
    @Bean
    public Queue chunkUploadQueue() {
        return QueueBuilder.durable(chunkUploadQueue).build(); // 持久化队列
    }

    /**
     * 定义文件合并队列
     */
    @Bean
    public Queue fileMergeQueue() {
        return QueueBuilder.durable(fileMergeQueue).build(); // 持久化队列
    }

    /**
     * 定义直连交换机
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false); // 持久化，非自动删除
    }

    /**
     * 分片上传队列绑定到直连交换机
     */
    @Bean
    public Binding chunkQueueBinding(Queue chunkUploadQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(chunkUploadQueue).to(directExchange).with(CHUNK_ROUTING_KEY);
    }

    /**
     * 文件合并队列绑定到直连交换机
     */
    @Bean
    public Binding mergeQueueBinding(Queue fileMergeQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(fileMergeQueue).to(directExchange).with(MERGE_ROUTING_KEY);
    }

    /**
     * 消息转换器，使用 JSON 序列化
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 自定义 RabbitTemplate，设置消息转换器
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    /**
     * 配置消息监听容器工厂，支持自定义并发消费者数量和预取数量
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(5); // 默认并发消费者数量
        factory.setMaxConcurrentConsumers(10); // 最大并发消费者数量
        factory.setPrefetchCount(1); // 每次只预取 1 条消息
        return factory;
    }
}