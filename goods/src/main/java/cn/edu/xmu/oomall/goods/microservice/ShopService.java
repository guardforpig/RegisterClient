package cn.edu.xmu.oomall.goods.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Zijun Min
 * @description
 * @createTime 2021/11/14 10:47
 **/
@FeignClient(name = "Shop")
public interface ShopService {
    @GetMapping("/shops/{id}/exist")
    ReturnObject isShopExist(Long id);

    @GetMapping("/shops/{id}")
    ReturnObject getShopInfo(Long id);
}
