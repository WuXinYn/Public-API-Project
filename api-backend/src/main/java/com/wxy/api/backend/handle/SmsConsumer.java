package com.wxy.api.backend.handle;

import com.rabbitmq.client.Channel;
import com.wxy.api.backend.service.SMService;
import com.wxy.api.common.model.dto.sms.SmsQueuesMsgInfo;
import com.wxy.api.common.model.enums.Limit;
import com.wxy.api.common.model.enums.RouteKey;
import com.wxy.api.common.service.InnerRedisService;
import com.wxy.api.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import java.io.IOException;

import static com.wxy.api.common.model.enums.PayEnums.FAST_INSTANT_TRADE_PAY;
import static com.wxy.api.common.model.enums.PayEnums.FAST_INSTANT_TRADE_REFUND;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * 短信消息消费者
 */
//@Component
@Slf4j
public class SmsConsumer
{
    //    @Resource
    private SMService smService;

    //    @DubboReference
    private InnerRedisService innerRedisService;

    //    @RabbitHandler
//    @RabbitListener(queues = "yupi.direct.sms.queue") // 指定队列名称
    public void handleSmsMessage(Channel channel, Message message) throws IOException
    {

        if (message != null) {
            String messageType = message.getMessageProperties().getHeader("messageType");
            messageType = defaultIfEmpty(messageType, RouteKey.Default.getKey());

//            if (messageType.equals(FAST_INSTANT_TRADE_PAY.getMessage())) {
//                // 3. 如果是支付成功的回调消息
//            }
//            else if (messageType.equals(FAST_INSTANT_TRADE_REFUND.getMessage())) {
//                // 3. 如果是退款成功的回调消息
//            }
//            else {
//                // 3. 其他类型信息
//                log.warn("支付宝回调消息-异常消息-{}", messageType);
//                // 非特定消息处理
//                long deliveryTag = message.getMessageProperties().getDeliveryTag();
//                // 将无效消息发送到死信队列
//                channel.basicNack(deliveryTag, false, false); // 不重新入队，发送到DLQ
//            }

            if (RouteKey.Sms_Message.getKey().equals(messageType)) {
                // 处理特定消息
                String data = new String(message.getBody());
                SmsQueuesMsgInfo smsQueuesMsgInfo = JsonUtils.parseJsonToObject(data, SmsQueuesMsgInfo.class);

                String account = smsQueuesMsgInfo.getAccount();
                int authCode = smsQueuesMsgInfo.getAuthCode();

                smService.sendMessage(account, authCode);

                String accountKey = Limit.CAPTCHA_CODE_KEY.getAddress() + account;
                innerRedisService.setParams(accountKey, authCode, 5);

                //由于配置设置了手动应答，所以这里要进行一个手动应答。注意：如果设置了自动应答，这里又进行手动应答，会出现double ack，那么程序会报错。
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
            else {
                // 非特定消息处理
                long deliveryTag = message.getMessageProperties().getDeliveryTag();
//                channel.basicReject(deliveryTag, false);  // 丢弃消息
                // 将无效消息发送到死信队列
                channel.basicNack(deliveryTag, false, false); // 不重新入队，发送到DLQ
                // 记录日志
                log.warn("Received invalid message with messageType: {}", messageType);
            }
        }

    }
}
