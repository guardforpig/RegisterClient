package cn.edu.xmu.oomall.goods.microservice;

import cn.edu.xmu.oomall.goods.microservice.vo.CategoryDetailRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author wyg
 */
@FeignClient(name = "shop-service")
public interface CategroyService {
    /**
     * 需要内部接口，通过cateoryId获取SimpleCategory
     * @param categoryId
     * @return
     */
    @GetMapping("/internal/categories/{categoryId}")
    InternalReturnObject<CategoryDetailRetVo> getCategoryDetailById(@PathVariable Long categoryId);
}
