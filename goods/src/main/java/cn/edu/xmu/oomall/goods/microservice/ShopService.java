package cn.edu.xmu.oomall.goods.microservice;

import cn.edu.xmu.oomall.goods.microservice.vo.CategoryVo;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleShopVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Zijun Min
 * @description
 * @createTime 2021/11/29 15:47
 **/
@FeignClient(name = "Shop")
public interface ShopService {
    @GetMapping("/shops/{id}")
    InternalReturnObject<SimpleShopVo> getShopInfo(@PathVariable("id")Long id);
    /**
     * @author 何赟
     * @date 2021-12-5
     */
    @GetMapping("/category/{id}")
    InternalReturnObject<CategoryVo> getCategoryById(@PathVariable("id")Integer id);

}
