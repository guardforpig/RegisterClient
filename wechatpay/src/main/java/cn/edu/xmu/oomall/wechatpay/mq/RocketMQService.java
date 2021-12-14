package cn.edu.xmu.oomall.wechatpay.mq;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayPaymentNotifyRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayRefundNotifyRetVo;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;


@Service
public class RocketMQService {
    private static final Logger logger = LoggerFactory.getLogger(RocketMQService.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Value("${notify-creator.notify-topic.delay-level}")
    private int delayLevel;

    @Value("${notify-creator.notify-topic.timeout}")
    private long timeout;


    public void sendWeChatPayPaymentMessage(WeChatPayPaymentNotifyRetVo weChatPayPaymentNotifyRetVo){
        String json= JacksonUtil.toJson(weChatPayPaymentNotifyRetVo);
        logger.info("sendWeChatPayPaymentMessage: send message "+weChatPayPaymentNotifyRetVo+" delay ="+delayLevel+" time =" + LocalDateTime.now());
        rocketMQTemplate.asyncSend("wechatpaymentnotify-topic", MessageBuilder.withPayload(json).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.info("sendWeChatPayPaymentMessage: onSuccess result = "+ sendResult+" time ="+LocalDateTime.now());
            }
            @Override
            public void onException(Throwable throwable) {
                logger.info("sendWeChatPayPaymentMessage: onException e = "+ throwable.getMessage()+" time ="+LocalDateTime.now());
            }
        }, timeout * 1000, delayLevel);
    }

    public void sendWeChatPayRefundMessage(WeChatPayRefundNotifyRetVo weChatPayRefundNotifyRetVo){
        String json= JacksonUtil.toJson(weChatPayRefundNotifyRetVo);
        logger.info("sendWeChatPayRefundMessage: send message "+weChatPayRefundNotifyRetVo+" delay ="+delayLevel+" time =" + LocalDateTime.now());
        rocketMQTemplate.asyncSend("wechatrefundnotify-topic", MessageBuilder.withPayload(json).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.info("sendWeChatPayRefundMessage: onSuccess result = "+ sendResult+" time ="+LocalDateTime.now());
            }
            @Override
            public void onException(Throwable throwable) {
                logger.info("sendWeChatPayRefundMessage: onException e = "+ throwable.getMessage()+" time ="+LocalDateTime.now());
            }
        }, timeout * 1000, delayLevel);
    }

}
