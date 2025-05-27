package com.wxy.api.backend.handle;

import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class DeadLetterConsumer
{

    // 消费死信队列中的消息 todo
//    @RabbitListener(queues = "api.dlx.order.queue")
    public void onDeadLetterOrderMessage(String message)
    {
        // 在这里处理死信消息，比如记录错误日志、分析问题、重试机制等
        log.info("Received dead letter message: {}", message);
    }

    // 消费死信队列中的消息 todo
//    @RabbitListener(queues = "api.dlx.resource.queue")
    public void onDeadLetterResourceMessage(String message)
    {
        // 在这里处理死信消息，比如记录错误日志、分析问题、重试机制等
        log.info("Received dead letter message: {}", message);
    }
}

