package cn.edu.xmu.oomall.goods.microservice;

import cn.edu.xmu.oomall.core.config.OpenFeignConfig;
import cn.edu.xmu.oomall.goods.microservice.vo.RetShareActivitySpecificInfoVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/23 14:39
 */
@FeignClient(value = "activity-service",configuration = OpenFeignConfig.class)
public interface ActivityService {
    @GetMapping("/internal/shops/{shopId}/shareactivities/{id}")
    InternalReturnObject<RetShareActivitySpecificInfoVo> getShareActivityByShopIdAndId(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id);

}
