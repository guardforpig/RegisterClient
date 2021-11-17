package cn.edu.xmu.oomall.comment.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "Customer")
public interface CustomerService {
    @GetMapping("/internal/customer/{userId}")
    ReturnObject getCustomer(Long userId);
}
