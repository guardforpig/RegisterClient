package cn.edu.xmu.oomall.wechatpay.mq;

import cn.edu.xmu.oomall.wechatpay.microservice.WeChatPayNotifyService;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayRefundNotifyRetVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@RocketMQMessageListener(topic = "wechat-refund-notify-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumerGroup = "wechat-refund-notify-group")
public class RefundNotifyService implements RocketMQListener<String> {
    @Resource
    WeChatPayNotifyService weChatPayNotifyService;

    @Override
    public void onMessage(String message) {
        WeChatPayRefundNotifyRetVo weChatPayRefundNotifyRetVo = JSONObject.parseObject(message, WeChatPayRefundNotifyRetVo.class);

        weChatPayNotifyService.refundNotify(weChatPayRefundNotifyRetVo);
    }
}