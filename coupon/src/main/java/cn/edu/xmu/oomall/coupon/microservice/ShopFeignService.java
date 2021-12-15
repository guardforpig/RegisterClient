package cn.edu.xmu.oomall.coupon.microservice;

import cn.edu.xmu.oomall.coupon.microservice.vo.ShopRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.oomall.coupon.model.bo.Shop;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author RenJieZheng 22920192204334
 */
@Component
@FeignClient(name = "shop-service")
public interface ShopFeignService {
    /**
     * 通过店铺id获得店铺
     * @param id
     * @return
     */
    @GetMapping(value = "/shops/{id}")
    InternalReturnObject<ShopRetVo> getSimpleShopById(@PathVariable Long id);
}
