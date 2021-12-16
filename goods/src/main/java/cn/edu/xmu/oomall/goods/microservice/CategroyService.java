package cn.edu.xmu.oomall.goods.microservice;

import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author wyg
 */
@FeignClient(name = "Category")
public interface CategroyService {
    /**
     * 需要内部接口，通过cateoryId获取SimpleCategory
     * @param categoryId
     * @return
     */
    @GetMapping("/internal/categories/{categoryId}")
    InternalReturnObject getCategoryById(@PathVariable Long categoryId);
}
