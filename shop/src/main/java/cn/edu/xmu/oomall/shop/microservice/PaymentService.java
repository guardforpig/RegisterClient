package cn.edu.xmu.oomall.shop.microservice;

import cn.edu.xmu.oomall.core.util.InternalReturnObject;
import cn.edu.xmu.oomall.shop.microservice.vo.RefundDepositVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Payment")
public interface PaymentService {

    @PostMapping("/internal/transfer")
    InternalReturnObject refund(@RequestBody RefundDepositVo refundDepositVo);
}