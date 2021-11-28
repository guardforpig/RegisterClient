package cn.edu.xmu.oomall.activity.microservice;

import cn.edu.xmu.oomall.activity.microservice.vo.OnSaleCreatedVo;
import cn.edu.xmu.oomall.activity.microservice.vo.OnSaleInfoVo;
import cn.edu.xmu.oomall.activity.microservice.vo.OnSaleVo;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleOnSaleVo;
import cn.edu.xmu.oomall.activity.model.vo.PageInfoVo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.activity.model.vo.OnsaleModifyVo;
import cn.edu.xmu.oomall.activity.model.vo.OnsaleVo;
import cn.edu.xmu.oomall.activity.model.vo.PageVo;
import cn.edu.xmu.oomall.core.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
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
    ReturnObject getOnSalesByProductId(@RequestParam("shopId") Long shopId,
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

    /**
     * @author Jiawei Zheng
     */
    @GetMapping("/internal/onsales")
    InternalReturnObject getAllOnsale(@RequestParam Long shopId,
                              @RequestParam Long productId,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")  LocalDateTime beginTime,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")  LocalDateTime endTime,
                              @RequestParam Integer page,
                              @RequestParam Integer pageSize);

    @GetMapping("/internal/shops/{did}/activities/{id}/onsales")
    InternalReturnObject getShopOnsaleInfo(@PathVariable("shopId")Long did,
                                   @PathVariable("id")Long id,
                                   @RequestParam("state")Byte state,
                                   @RequestParam("beginTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")  LocalDateTime beginTime,
                                   @RequestParam("endTime")@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime endTime,
                                   @RequestParam("page") Integer page,
                                   @RequestParam("pageSize") Integer pageSize);

    @GetMapping("/internal/onsales/{id}")
    InternalReturnObject<OnSaleInfoVo> getOnSaleById(@PathVariable("id")Long id);

    @GetMapping("/internal/onsales/{id}")
    InternalReturnObject<OnSaleInfoVo> getOnSaleInfo(@PathVariable Long id);

    @PostMapping("/shops/{shopId}/products/{id}/onsales")
    InternalReturnObject addOnSale(@PathVariable("shopId") long shopId,
                           @PathVariable("id") long id,
                           @RequestBody OnSaleCreatedVo onSaleCreatedVo);
}
