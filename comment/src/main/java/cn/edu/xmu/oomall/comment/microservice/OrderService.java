package cn.edu.xmu.oomall.comment.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Order")
public interface OrderService {

    @GetMapping("/internal/orders/orderitem/{productId}")
    ReturnObject getOrderitemIdByProductId(@RequestParam("ProductId") Long productId);

    @GetMapping("/internal/shop/{productId}")
    ReturnObject getShopIdByProductId(@RequestParam("productId") Long productId);


}
