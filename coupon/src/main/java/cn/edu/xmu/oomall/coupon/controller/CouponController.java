package cn.edu.xmu.oomall.coupon.controller;


import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.model.bo.CouponActivity;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityVo;
import cn.edu.xmu.oomall.coupon.service.CouponService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author qingguo Hu 22920192204208
 */

@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    @ApiOperation(value = "查看优惠活动中的商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "优惠活动ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "page", value = "页码", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数目", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "资源不存在")
    })
    @RequestMapping(value="/couponactivities/{id}/products", method=RequestMethod.GET)
    public Object listProductsByCouponActivityId(@ApiParam(value = "优惠活动ID", required = true) @PathVariable("id") Long couponActivityId,
                                                @ApiParam(value = "页码") @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                                @ApiParam(value = "每页数目") @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        ReturnObject<PageInfo<VoObject>> retVoObject =
                couponService.listProductsByCouponActivityId(couponActivityId, pageNumber, pageSize);
        return Common.decorateReturnObject(Common.getPageRetObject(retVoObject));

    }



    @ApiOperation(value = "查看商品的上线的优惠活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "货品ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "page", value = "页码", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数目", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "资源不存在")
    })
    @RequestMapping(value = "products/{id}/couponactivities", method=RequestMethod.GET)
    public Object listCouponActivitiesByProductId(@ApiParam(value = "货品ID", required = true) @PathVariable("id") Long productId,
                                                  @ApiParam(value = "页码") @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                                  @ApiParam(value = "每页数目") @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        ReturnObject<PageInfo<VoObject>> retVoObject =
                couponService.listCouponActivitiesByProductId(productId, pageNumber, pageSize);

        if (retVoObject.getCode().equals(ReturnNo.OK))
            return Common.decorateReturnObject(Common.getPageRetObject(retVoObject));
        else
            return Common.decorateReturnObject(retVoObject);
    }


    @ApiOperation(value = "管理员修改己方某优惠活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "用户token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "优惠活动ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "body", value = "可修改的优惠活动信息", required = true, dataType = "Long", paramType = "body")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 507, message = "当前状态禁止此操作"),
            @ApiResponse(code = 947, message = "开始时间不能晚于结束时间"),
            @ApiResponse(code = 950, message = "优惠卷领卷时间晚于活动开始时间")
    })
    @RequestMapping(value = "/shops/{shopId}/couponactivities/{id}", method=RequestMethod.PUT)
    public Object updateCouponActivity(@ApiParam(value = "商店ID", required = true) @PathVariable("shopId") Long shopId,
                                       @ApiParam(value = "优惠活动ID", required = true) @PathVariable("id") Long couponActivityId,
                                       @ApiParam(value = "可修改的优惠活动信息", required = true) @Validated @RequestBody CouponActivityVo couponActivityVo,
                                       Long userId, String userName, BindingResult bindingResult) {
        userId = 123L;
        userName = "hqg";
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }

        if (couponActivityVo.getBeginTime().isAfter(couponActivityVo.getEndTime()))
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.ACT_LATE_BEGINTIME));

        if (couponActivityVo.getCouponTime().isAfter((couponActivityVo.getBeginTime())))
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.COUPON_LATE_COUPONTIME));

        ReturnObject returnObject = couponService.updateCouponActivity(userId, userName, shopId, couponActivityId, couponActivityVo, null);
        return Common.decorateReturnObject(returnObject);
    }


    @ApiOperation(value = "管理员物理删除己方某优惠活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "用户token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "优惠活动ID", required = true, dataType = "Long", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 507, message = "当前状态禁止此操作"),
    })
    @RequestMapping(value = "/shops/{shopId}/couponactivities/{id}", method=RequestMethod.DELETE)
    public Object deleteCouponActivity(@ApiParam(value = "商店ID", required = true) @PathVariable("shopId") Long shopId,
                                       @ApiParam(value = "优惠活动ID", required = true) @PathVariable("id") Long couponActivityId,
                                       Long userId, String userName) {
        userId = 123L;
        userName = "hqg";
        ReturnObject returnObject = couponService.deleteCouponActivity(userId, userName, shopId, couponActivityId);
        return Common.decorateReturnObject(returnObject);
    }

    @ApiOperation(value = "管理员为己方某优惠券活动新增限定范围")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "用户token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "优惠活动ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "sid", value = "销售活动ID", required = true, dataType = "Long", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 507, message = "当前状态禁止此操作"),
    })
    @RequestMapping(value = "/shops/{shopId}/couponactivities/{id}/onsales/{sid}", method=RequestMethod.POST)
    public Object insertCouponOnsale(@ApiParam(value = "商店ID", required = true) @PathVariable("shopId") Long shopId,
                                                         @ApiParam(value = "优惠活动ID", required = true) @PathVariable("id") Long couponActivityId,
                                                         @ApiParam(value = "销售活动ID", required = true) @PathVariable("sid") Long onsaleId,
                                                         Long userId, String userName){
        userId = 123L;
        userName = "hqg";
        ReturnObject returnObject = couponService.insertCouponOnsale(userId, userName, shopId, couponActivityId, onsaleId);
        return Common.decorateReturnObject(returnObject);
    }


    @ApiOperation(value = "店家删除己方某优惠券活动的某限定范围")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "用户token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "couponOnsaleId", required = true, dataType = "Long", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 507, message = "当前状态禁止此操作"),
    })
    @RequestMapping(value = "/shops/{shopId}/coupononsale/{id}", method=RequestMethod.DELETE)
    public Object deleteCouponOnsale(@ApiParam(value = "商店ID", required = true) @PathVariable("shopId") Long shopId,
                                     @ApiParam(value = "couponOnsaleId", required = true) @PathVariable("id") Long couponOnsaleId,
                                     Long userId, String userName){
        userId = 123L;
        userName = "hqg";
        ReturnObject returnObject = couponService.deleteCouponOnsale(userId, userName, shopId, couponOnsaleId);
        return Common.decorateReturnObject(returnObject);
    }


    @ApiOperation(value = "上线优惠活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "用户token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "优惠活动ID", required = true, dataType = "Long", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 507, message = "当前状态禁止此操作"),
    })
    @RequestMapping(value = "/shops/{shopId}/couponactivities/{id}/online", method=RequestMethod.PUT)
    public Object updateCouponActivityToOnline(@ApiParam(value = "商店ID", required = true) @PathVariable("shopId") Long shopId,
                                               @ApiParam(value = "优惠活动ID", required = true) @PathVariable("id") Long couponActivityId,
                                               Long userId, String userName) {
        userId = 123L;
        userName = "hqg";
        ReturnObject returnObject = couponService.updateCouponActivity(userId, userName, shopId, couponActivityId, null, CouponActivity.State.ONLINE);
        return Common.decorateReturnObject(returnObject);
    }


    @ApiOperation(value = "下线优惠活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "用户token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "优惠活动ID", required = true, dataType = "Long", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 507, message = "当前状态禁止此操作"),
    })
    @RequestMapping(value = "/shops/{shopId}/couponactivities/{id}/offline", method=RequestMethod.PUT)
    public Object updateCouponActivityToOffline(@ApiParam(value = "商店ID", required = true) @PathVariable("shopId") Long shopId,
                                               @ApiParam(value = "优惠活动ID", required = true) @PathVariable("id") Long couponActivityId,
                                               Long userId, String userName) {
        userId = 123L;
        userName = "hqg";
        ReturnObject returnObject = couponService.updateCouponActivity(userId, userName, shopId, couponActivityId, null, CouponActivity.State.OFFLINE);
        return Common.decorateReturnObject(returnObject);
    }


}
