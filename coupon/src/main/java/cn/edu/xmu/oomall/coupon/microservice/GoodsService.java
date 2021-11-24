package cn.edu.xmu.oomall.coupon.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author qingguo Hu 22920192204208
 */
@FeignClient(value = "goods")
public interface GoodsService {

    @ApiOperation(value = "通过Id返回OnsaleVo")
    @GetMapping("/internal/onsales/{id}")
    ReturnObject getOnsaleById(@PathVariable("id") Long id);


    @ApiOperation(value = "通过productId查OnsaleVo（1对多）")
    @GetMapping("/internal/onsales")
    ReturnObject listOnsale(@RequestParam("productId") Long productId,
                            @RequestParam("page") Integer pageNumber,
                            @RequestParam("pageSize") Integer pageSize);

}

