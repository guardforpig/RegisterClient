package cn.edu.xmu.oomall.goods.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Order")
public interface OrderService {

    @GetMapping("/internal/{userId}/orders/{orderitemId}")
    ReturnObject isCustomerOwnOrderItem(Long userId,Long orderItemId);

    @GetMapping("/internal/orders/orderitem/{orderitemId}")
    ReturnObject getOrderItem(Long orderItemId);
}
