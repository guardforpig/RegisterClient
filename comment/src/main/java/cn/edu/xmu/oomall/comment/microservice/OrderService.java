package cn.edu.xmu.oomall.comment.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Order")
public interface OrderService {

    @GetMapping("/internal/{userId}/orders/{orderitemId}")
    ReturnObject isCustomerOwnOrderItem(@RequestParam("userId") Long userId, @RequestParam("orderitemId") Long orderitemId);

    @GetMapping("/internal/orders/orderitem/shop/{orderitemId}")
    ReturnObject getShopIdByOrderItemId(@RequestParam("orderitemId")Long orderItemId);
}
