package com.wxy.api.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectRabbitConfig
{
    /**
     * 创建一个名为api.direct的Direct类型的正常交换机
     */
    @Bean
    public DirectExchange apiDirectExchange()
    {
        // durable:是否持久化,默认是false,持久化交换机。
        // autoDelete:是否自动删除，交换机先有队列或者其他交换机绑定的时候，然后当该交换机没有队列或其他交换机绑定的时候，会自动删除。
        // arguments：交换机设置的参数，比如设置交换机的备用交换机（Alternate Exchange），当消息不能被路由到该交换机绑定的队列上时，会自动路由到备用交换机
        return new DirectExchange("api.direct", true, false);
    }

    /**
     * 订单更新队列
     */
    @Bean
    public Queue apiOrderDirectQueue()
    {
        return QueueBuilder.durable("api.direct.order.queue")
                .withArgument("x-dead-letter-exchange", "api.dlx.exchange") // 设置死信交换机
                .withArgument("x-dead-letter-routing-key", "order_error") // 设置死信队列的路由键
                .withArgument("x-message-ttl", 300000) // 设置TTL为5min
                .build();
    }

    /**
     * 资源更新队列
     */
    @Bean
    public Queue apiResourceDirectQueue()
    {
        return QueueBuilder.durable("api.direct.resource.queue")
                .withArgument("x-dead-letter-exchange", "api.dlx.exchange") // 设置死信交换机
                .withArgument("x-dead-letter-routing-key", "resource_error") // 设置死信队列的路由键
                .withArgument("x-message-ttl", 300000) // 设置TTL为5min
                .build();
    }

    /**
     * 绑定交换机和队列
     */
    @Bean
    public Binding orderBindingDirect()
    {
        return BindingBuilder.bind(apiOrderDirectQueue()).to(apiDirectExchange()).with("order"); // bind队列to交换机中with路由key（routing key）
    }

    /**
     * 绑定交换机和队列
     */
    @Bean
    public Binding resourceBindingDirect()
    {
        return BindingBuilder.bind(apiResourceDirectQueue()).to(apiDirectExchange()).with("resource");
    }


    /**
     * 定义订单更新死信队列
     */
    @Bean
    public Queue apiDlxOrderQueue()
    {
        return QueueBuilder.durable("api.dlx.order.queue")
                .build();
    }

    /**
     * 定义资源更新死信队列
     */
    @Bean
    public Queue apiDlxResourceQueue()
    {
        return QueueBuilder.durable("api.dlx.resource.queue")
                .build();
    }

    /**
     * 定义死信交换机
     */
    @Bean
    public Exchange apiDlxExchange()
    {
        return ExchangeBuilder.directExchange("api.dlx.exchange")
                .durable(true)
                .build();
    }

    /**
     * 将订单死信队列绑定到死信交换机
     */
    @Bean
    public Binding dlxOrderBinding()
    {
        return BindingBuilder.bind(apiDlxOrderQueue()).to(apiDlxExchange()).with("order_error").noargs();
    }

    /**
     * 将资源死信队列绑定到死信交换机
     */
    @Bean
    public Binding dlxResourceBinding()
    {
        return BindingBuilder.bind(apiDlxResourceQueue()).to(apiDlxExchange()).with("resource_error").noargs();
    }

}
