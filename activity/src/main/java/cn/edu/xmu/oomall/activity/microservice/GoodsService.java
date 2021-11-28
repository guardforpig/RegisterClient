package cn.edu.xmu.oomall.activity.microservice;

import cn.edu.xmu.oomall.activity.microservice.vo.OnSaleVo;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleOnSaleVo;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleSaleInfoVo;
import cn.edu.xmu.oomall.activity.model.vo.*;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.activity.model.vo.OnsaleModifyVo;
import cn.edu.xmu.oomall.activity.model.vo.OnsaleVo;
import cn.edu.xmu.oomall.activity.model.vo.PageVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @author Gao Yanfeng
 * @date 2021/11/13
 */
@FeignClient(name = "Goods")
public interface GoodsService {
    @GetMapping("/internal/products/{id}/onsales")
    ReturnObject<PageInfoVo<SimpleOnSaleVo>> getOnsSalesOfProduct(@PathVariable Long id, @RequestParam Integer page, @RequestParam Integer pageSize);

    @GetMapping("/internal/onsales/{id}")
    ReturnObject<OnSaleVo> getOnSale(@PathVariable Long id);

    @GetMapping("/internal/onsales")
    InternalReturnObject getOnSalesByProductId(@RequestParam("shopId") Long shopId,
                                       @RequestParam("productId")Long productId,
                                       @RequestParam("beginTime") LocalDateTime beginTime,
                                       @RequestParam("endTime")LocalDateTime endTime,
                                       @RequestParam("page") Integer page,
                                       @RequestParam("pageSize") Integer pageSize);

    @PutMapping("/internal/shops/{did}/activities/{id}/onsales/online")
    InternalReturnObject onlineOnsale(@PathVariable(value="did") Long shopId, @PathVariable(value="id") Long activityId);

    @PutMapping("/internal/shops/{did}/activities/{id}/onsales/offline")
    InternalReturnObject offlineOnsale(@PathVariable(value="did") Long shopId, @PathVariable(value="id") Long activityId);


    @GetMapping("/internal/shops/{did}/activities/{id}/onsales")
    InternalReturnObject<PageVo<OnsaleVo>> getOnsale(@PathVariable(value="did") Long shopId,
                                                     @PathVariable(value="id")Long activityId,
                                                     @RequestParam(name="state",required = false)Integer state,
                                                     @RequestParam(name="page",required = false)Integer page,
                                                     @RequestParam(name="pageSize",required = false)Integer pageSize);

    @PutMapping("/internal/shops/{did}/onsales/{id}")
    InternalReturnObject modifyOnsale(@PathVariable(value="did") Long shopId, @PathVariable(value="id")Long onsaleId, @RequestBody OnsaleModifyVo vo);

    @DeleteMapping("/internal/shops/{did}/activities/{id}/onsales")
    InternalReturnObject deleteOnsale(@PathVariable(value="did") Long shopId, @PathVariable(value="id") Long activityId);


    @PostMapping("/shops/{shopId}/products/{id}/onsales")
    InternalReturnObject addOnsale(@PathVariable("shopId") long shopId, @PathVariable("id") long id,
                                 @RequestBody SimpleSaleInfoVo simpleSaleInfoVo);
}
