package cn.edu.xmu.oomall.alipay.service.mq;

import cn.edu.xmu.oomall.alipay.microservice.PaymentFeightService;
import cn.edu.xmu.oomall.alipay.model.bo.NotifyBody;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
@RocketMQMessageListener(topic = "alipay-notify-topic", consumeMode = ConsumeMode.ORDERLY, consumerGroup = "alipay-notify-group")
public class NotifyService implements RocketMQListener<String> {
    @Resource
    PaymentFeightService paymentFeightService;

    @Override
    public void onMessage(String message) {
        NotifyBody notifyBody  = JacksonUtil.toObj(message, NotifyBody.class);
        paymentFeightService.notify(notifyBody);
    }

}
