package cn.edu.xmu.oomall.shop.microservice;

import cn.edu.xmu.oomall.core.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "Reconciliation")
public interface ReconciliationService
{
    @GetMapping("/internal/shops/{shopId}/reconsiliation/clear")
    InternalReturnObject isClean(Long shopId);
}