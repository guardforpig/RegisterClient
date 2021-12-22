package cn.edu.xmu.oomall.activity.microservice;

import cn.edu.xmu.oomall.activity.microservice.vo.*;
import cn.edu.xmu.oomall.activity.model.vo.OnsaleModifyVo;
import cn.edu.xmu.oomall.activity.model.vo.OnsaleVo;
import cn.edu.xmu.oomall.activity.model.vo.PageVo;
import cn.edu.xmu.oomall.core.config.OpenFeignConfig;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @author Gao Yanfeng
 * @date 2021/11/13
 */
@FeignClient(name = "goods-service",configuration= OpenFeignConfig.class)
public interface GoodsService {
    @GetMapping("/internal/onsales")
    InternalReturnObject getOnSales(@RequestParam("shopId") Long shopId,
                                    @RequestParam("productId")Long productId,
                                    @RequestParam("beginTime") LocalDateTime beginTime,
                                    @RequestParam("endTime")LocalDateTime endTime,
                                    @RequestParam("page") Integer page,
                                    @RequestParam("pageSize") Integer pageSize);

    @PutMapping("/internal/shops/{did}/activities/{id}/onsales/online")
    InternalReturnObject onlineOnsale(@PathVariable(value="did") Long shopId, @PathVariable(value="id") Long activityId);

    @PutMapping("/internal/shops/{did}/activities/{id}/onsales/offline")
    InternalReturnObject offlineOnsale(@PathVariable(value="did") Long shopId, @PathVariable(value="id") Long activityId);

    @PutMapping("/internal/shops/{did}/onsales/{id}")
    InternalReturnObject modifyOnsale(@PathVariable(value="did") Long shopId, @PathVariable(value="id")Long onsaleId, @RequestBody OnsaleModifyVo vo);

    @DeleteMapping("/internal/shops/{did}/activities/{id}/onsales")
    InternalReturnObject deleteOnsale(@PathVariable(value="did") Long shopId, @PathVariable(value="id") Long activityId);

    /**
     * @author Jiawei Zheng
     */
//    @GetMapping("/internal/shops/{did}/activities/{id}/onsales")
//    InternalReturnObject getShopOnSaleInfo(@PathVariable("did")Long did,
//                                           @PathVariable("id")Long id,
//                                           @RequestParam("state")Byte state,
//                                           @RequestParam("beginTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")  LocalDateTime beginTime,
//                                           @RequestParam("endTime")@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime endTime,
//                                           @RequestParam("page") Integer page,
//                                           @RequestParam("pageSize") Integer pageSize);

    @GetMapping("/internal/onsales/{id}")
    InternalReturnObject<FullOnSaleVo> selectFullOnsale(@PathVariable("id")Long id);

    @PostMapping("internal/shops/{did}/products/{id}/onsales")
    InternalReturnObject<SimpleOnSaleInfoVo> createNewOnSale(@PathVariable Long id, @Validated @RequestBody OnSaleCreatedVo newOnSaleAllVo,
                                                             @PathVariable Long did)  ;

    @PutMapping("/shops/{shopId}/onsales/{id}")
    InternalReturnObject modifyOnSaleNorSec(@PathVariable Long shopId, @PathVariable Long id, @Validated @RequestBody ModifyOnSaleVo onSale);

//    @GetMapping("/internal/onsales/{id}")
//    InternalReturnObject selectFullOnsale(@PathVariable("id")Long id);

    @PostMapping("/internal/onSales/{id}/shareActivities/{sid}")
    ReturnObject updateAddOnSaleShareActId(@PathVariable("id") Long id,@PathVariable("sid") Long sid);

    @DeleteMapping("/internal/onSales/{id}/shareActivities/{sid}")
    ReturnObject updateDeleteOnSaleShareActId(@PathVariable("id") Long id,@PathVariable("sid") Long sid);

    /**
     * lxc
     * @param did
     * @param id
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/internal/shops/{did}/activities/{id}/onsales")
    InternalReturnObject<PageVo<SimpleOnSaleInfoVo>> getOnSale(@PathVariable("did")Long did,
                                                                       @PathVariable("id")Long id,
                                                                       @RequestParam("state")Byte state,
                                                                       @RequestParam("beginTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")  LocalDateTime beginTime,
                                                                       @RequestParam("endTime")@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime endTime,
                                                                       @RequestParam("page") Integer page,
                                                                       @RequestParam("pageSize") Integer pageSize);
}
