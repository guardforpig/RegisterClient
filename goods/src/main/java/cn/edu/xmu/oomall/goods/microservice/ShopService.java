package cn.edu.xmu.oomall.goods.microservice;

import cn.edu.xmu.oomall.goods.microservice.vo.CategoryVo;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleCategoryVo;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleShopVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author Zijun Min
 * @description
 * @createTime 2021/11/29 15:47
 **/
@FeignClient(value = "shop-service")
public interface ShopService {
    @GetMapping("/shops/{id}")
    InternalReturnObject<SimpleShopVo> getShopInfo(@PathVariable("id")Long id);

    /**
     * @author 何赟
     * @date 2021-12-5
     */
    @GetMapping("/shops/{shopId}/orphoncategorie")
    InternalReturnObject<List<CategoryVo>> getSecondCategory(@PathVariable Long shopId);

    /**
     * 需要内部接口，通过cateoryId获取SimpleCategory
     * @return
     */
    @GetMapping("/internal/categories/{id}")
    InternalReturnObject<SimpleCategoryVo> getCategoryById(@PathVariable("id") Long id);

}
