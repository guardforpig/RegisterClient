package cn.edu.xmu.oomall.activity.microservice;

import cn.edu.xmu.oomall.activity.microservice.vo.SimpleSaleInfoVO;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author jiyuan lin
 * @date 2021/11/14
 */
@FeignClient(name = "OnSale")
public interface OnSaleService {

    @DeleteMapping("/internal/activities/{id}/onsales")
    ReturnObject deleteOnSale(@PathVariable long activityId);

    @PostMapping("/shops/{shopId}/products/{id}/onsales")
    ReturnObject addOnsale(@PathVariable("shopId") long shopId,@PathVariable("id") long id,
                           @RequestBody SimpleSaleInfoVO simpleSaleInfoVo);

    @PutMapping("/internal/groupon/{id}/update")
    ReturnObject updateGrouponOnsale(@PathVariable("id") long id, @RequestBody SimpleSaleInfoVO simpleOnsaleVo);

    @PutMapping("/internal/activities/{id}/onsales/offline")
    ReturnObject offlineOnsale(@PathVariable("id") long id);

    @PutMapping("/internal/activities/{id}/onsales/online")
    ReturnObject onlineOnsale(@PathVariable("id") long id);
}



