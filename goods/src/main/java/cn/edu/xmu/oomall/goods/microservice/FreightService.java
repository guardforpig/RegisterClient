package cn.edu.xmu.oomall.goods.microservice;


import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author wyg
 */
@FeignClient(name = "Freight")
public interface FreightService {
    @GetMapping("shops/{shopId}/freightmodels/default")
    InternalReturnObject getDefaultFreightModel(@PathVariable Long shopId);

    @GetMapping("shops/{shopId}/freightmodels/{id}")
    InternalReturnObject getFreightModel(@PathVariable Long shopId, @PathVariable Long id);
}
