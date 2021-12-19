package cn.edu.xmu.oomall.shop.microservice;

import cn.edu.xmu.oomall.core.config.OpenFeignConfig;
import cn.edu.xmu.privilegegateway.annotation.aop.Depart;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import feign.Headers;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @Author jxy
 * @create 2021/12/19 3:49 PM
 */

@FeignClient(value = "privilege-service",configuration = OpenFeignConfig.class)
public interface PrivilegeService {
    @PutMapping(value = "/internal/users/{id}/departs/{did}",headers = {"Authorization: {token}"})
//    @Headers({"Content-Type: application/json;charset=UTF-8", "Authorization: {token}"})
    InternalReturnObject addToDepart(@PathVariable Long id, @PathVariable Long did,@Param("token") String token);

}
