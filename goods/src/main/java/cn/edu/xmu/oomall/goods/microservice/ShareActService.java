package cn.edu.xmu.oomall.goods.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Zijun Min
 * @description
 * @createTime 2021/11/14 10:51
 **/
@FeignClient(name = "ShareAct")
public interface ShareActService {
    @GetMapping("/shareactivity/{id}/exist")
    ReturnObject isShareActExist(Long id);

    @GetMapping("/shareactivity/{id}")
    ReturnObject getShareActInfo(Long id);
}
