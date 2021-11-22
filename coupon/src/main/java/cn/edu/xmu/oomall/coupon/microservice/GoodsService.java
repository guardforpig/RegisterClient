package cn.edu.xmu.oomall.coupon.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author qingguo Hu 22920192204208
 */
@FeignClient(value = "goods")
public interface GoodsService {

    @ApiOperation(value = "通过onsaleId返回productVo（1对1）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "onsaleId", required = true, dataType = "Integer", paramType = "path")
    })
    @GetMapping("/internal/onsales/{id}/products")
    ReturnObject getProductByOnsaleId(@PathVariable("id") Long onsaleId);


    @ApiOperation(value = "通过productId返回OnsaleVo（1对多）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "productId", required = true, dataType = "Integer", paramType = "path")
    })
    @GetMapping("/internal/products/{id}/onsales")
    ReturnObject listOnsalesByProductId(@PathVariable("id") Long productId);


    @ApiOperation(value = "通过Id返回OnsaleVo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Integer", paramType = "path")
    })
    @GetMapping("/internal/onsales/{id}")
    ReturnObject getOnsaleById(@PathVariable("id") Long id);

}

