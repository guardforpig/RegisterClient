package cn.edu.xmu.oomall.wechatpay.mq;

import cn.edu.xmu.oomall.wechatpay.microservice.WeChatPayNotifyService;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayPaymentNotifyRetVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@RocketMQMessageListener(topic = "wechat-payment-notify-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumerGroup = "wechat-payment-notify-group")
public class PaymentNotifyService implements RocketMQListener<String> {
    @Resource
    WeChatPayNotifyService weChatPayNotifyService;

    @Override
    public void onMessage(String message) {
        WeChatPayPaymentNotifyRetVo weChatPayPaymentNotifyRetVo = JSONObject.parseObject(message, WeChatPayPaymentNotifyRetVo.class);
        weChatPayNotifyService.paymentNotify(weChatPayPaymentNotifyRetVo);
    }
}

