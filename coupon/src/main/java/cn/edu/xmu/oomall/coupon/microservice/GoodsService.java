package cn.edu.xmu.oomall.coupon.microservice;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author qingguo Hu 22920192204208
 */
@FeignClient(value = "goods")
public interface GoodsService {

    @ApiOperation(value = "通过onsaleId返回productVo（1对1）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "onsaleId", required = true, dataType = "Integer", paramType = "path")
    })
    @RequestMapping(value = "/internal/onsale/{id}/product", method = RequestMethod.GET)
    ReturnObject<VoObject> getProductByOnsaleId(@PathVariable("id") Long onsaleId);


    @ApiOperation(value = "通过productId返回OnsaleVo（1对多）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "productId", required = true, dataType = "Integer", paramType = "path")
    })
    @RequestMapping(value = "/internal/product/{id}/onsales", method = RequestMethod.GET)
    ReturnObject<List<VoObject>> listOnsalesByProductId(@PathVariable("id") Long productId);
}

