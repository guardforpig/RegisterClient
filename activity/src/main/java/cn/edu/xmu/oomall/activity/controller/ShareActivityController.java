package cn.edu.xmu.oomall.activity.controller;

import cn.edu.xmu.oomall.activity.model.bo.ShareActivityStatesBo;
import cn.edu.xmu.oomall.activity.model.vo.ShareActivityVo;
import cn.edu.xmu.oomall.activity.service.ShareActivityService;
import cn.edu.xmu.oomall.core.util.*;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/11/12 12:52
 */
@Api(value = "分享活动API", tags = "activity")
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
public class ShareActivityController {

    @Autowired
    private ShareActivityService shareActivityService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    /**
     * 获得分享活动的所有状态
     *
     * @return
     */
    @GetMapping("/shareactivities/states")
    public Object getShareState() {
        ReturnObject returnObject = shareActivityService.getShareState();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 显示所有状态的分享活动
     *
     * @param shopId    店铺Id
     * @param productId 货品id
     * @param beginTime 晚于此开始时间
     * @param endTime   早于此结束时间
     * @param state     分享活动状态
     * @param page      页码
     * @param pageSize  每页数目
     * @return
     */
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/shareactivities")
    public Object getShareByShopId(@PathVariable Long shopId,
                                   @RequestParam(required = false) Long productId,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime beginTime,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime,
                                   @RequestParam(required = false) Byte state,
                                   @RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize) {
        if (beginTime != null && endTime != null) {
            if (beginTime.isAfter(endTime)) {
                return new ReturnObject<>(ReturnNo.LATE_BEGINTIME);
            }
        }
        LocalDateTime beginLocalDateTime = null;
        LocalDateTime endLocalDateTime = null;
        if (beginTime != null) {
            beginLocalDateTime = beginTime.toLocalDateTime();
        }
        if (endTime != null) {
            endLocalDateTime = endTime.toLocalDateTime();
        }
        ReturnObject shareByShopId = shareActivityService.getShareByShopId(shopId, productId, beginLocalDateTime, endLocalDateTime, state, page, pageSize);
        return Common.decorateReturnObject(shareByShopId);
    }

    /**
     * 管理员新增分享活动
     *
     * @param shopId          店铺id
     * @param shareActivityVo 可修改的信息
     * @param bindingResult   合法性检验结果
     * @return
     */
    @PostMapping("/shops/{shopId}/shareactivities")
    @Audit(departName = "shops")
    public Object addShareAct(@LoginName String createName,
                              @LoginUser Long createId,
                              @PathVariable(value = "shopId") Long shopId,
                              @Validated @RequestBody ShareActivityVo shareActivityVo,
                              BindingResult bindingResult) {
        Object obj = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != obj) {
            return obj;
        }
        if (shareActivityVo.getBeginTime().isAfter(shareActivityVo.getEndTime())) {
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.FIELD_NOTVALID, "开始时间不得早于结束时间"));
        }
        ReturnObject returnObject = shareActivityService.addShareAct(createName, createId, shopId, shareActivityVo);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 查询分享活动 只显示上线状态的分享活动
     *
     * @param shopId    店铺Id
     * @param productId 货品id
     * @param beginTime 晚于此开始时间
     * @param endTime   早于此结束时间
     * @param page      页码
     * @param pageSize  每页数目
     * @return
     */
    @Audit(departName = "shops")
    @GetMapping("/shareactivities")
    public Object getShareActivity(@RequestParam(name = "shopId", required = false) Long shopId,
                                   @RequestParam(name = "productId", required = false) Long productId,
                                   @RequestParam(name = "beginTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime beginTime,
                                   @RequestParam(name = "endTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime,
                                   @RequestParam(name = "page", required = false) Integer page,
                                   @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        if (shopId != null && shopId <= 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID, "shopId错误"));
        }
        if (productId != null && productId <= 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID, "productId错误"));
        }
        if (beginTime != null && endTime != null) {
            if (beginTime.isAfter(endTime)) {
                return new ReturnObject<>(ReturnNo.LATE_BEGINTIME);
            }
        }
        LocalDateTime beginLocalDateTime = null;
        LocalDateTime endLocalDateTime = null;
        if (beginTime != null) {
            beginLocalDateTime = beginTime.toLocalDateTime();
        }
        if (endTime != null) {
            endLocalDateTime = endTime.toLocalDateTime();
        }
        ReturnObject shareByShopId = shareActivityService.getShareByShopId(shopId, productId, beginLocalDateTime, endLocalDateTime, ShareActivityStatesBo.ONLINE.getCode(), page, pageSize);
        return Common.decorateReturnObject(shareByShopId);
    }

    /**
     * 查看分享活动详情 只显示上线状态的分享活动
     *
     * @param id 分享活动Id
     * @return
     */
    @Audit(departName = "shops")
    @GetMapping("/shareactivities/{id}")
    public Object getShareActivityById(@PathVariable(value = "id", required = true) Long id) {
        return Common.decorateReturnObject(shareActivityService.getShareActivityById(id));
    }

    /**
     * 查看特定分享活动详情,显示所有状态的分享活动
     *
     * @param shopId 店铺Id
     * @param id     分享活动Id
     * @return
     */
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/shareactivities/{id}")
    public Object getShareActivityByShopIdAndId(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id) {
        return Common.decorateReturnObject(shareActivityService.getShareActivityByShopIdAndId(shopId, id));
    }

    @ApiOperation(value = "在已有销售上增加分享")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "在售商品id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "sid", value = "分享活动id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 507, message = "信息签名不正确"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @Audit(departName = "shops")
    @PostMapping("/shops/{shopId}/onSale/{id}/shareActivities/{sid}")
    public Object addShareActivityOnOnSale(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @PathVariable("sid") Long sid, @LoginUser Long loginUser, @LoginName String loginUsername) {
        ReturnObject ret = shareActivityService.addShareActivityOnOnSale(shopId, id, sid, loginUser, loginUsername);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "取消已有销售上的分享")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "在售商品id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "sid", value = "分享活动id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @Audit(departName = "shops")
    @DeleteMapping("/shops/{shopId}/onSale/{id}/shareActivities/{sid}")
    public Object deleteShareActivityOnOnSale(@PathVariable("shopId") Long shopId,
                                              @PathVariable("id") Long id,
                                              @PathVariable("sid") Long sid,
                                              @LoginUser Long loginUser,
                                              @LoginName String loginUsername) {
        ReturnObject ret = shareActivityService.deleteShareActivityOnOnSale(shopId, id, sid, loginUser, loginUsername);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "修改平台分享活动的内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "分享活动id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 507, message = "信息签名不正确"),
            @ApiResponse(code = 947, message = "开始时间不能晚于结束时间")
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/shareactivities/{id}")
    public Object modifyShareActivity(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @RequestBody ShareActivityVo shareActivityVo, @LoginUser Long loginUser, @LoginName String loginUsername) {
        if (shareActivityVo.getBeginTime().compareTo(shareActivityVo.getEndTime()) > 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME));
        }
        ReturnObject ret = shareActivityService.modifyShareActivity(id, shareActivityVo, loginUser, loginUsername);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "删除草稿状态的分享活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "分享活动id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 507, message = "信息签名不正确"),
    })
    @Audit(departName = "shops")
    @DeleteMapping("/shops/{shopId}/shareactivities/{id}")
    public Object deleteShareActivity(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @LoginUser Long loginUser, @LoginName String loginUsername) {
        ReturnObject ret = shareActivityService.deleteShareActivity(id, loginUser, loginUsername);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "上线分享活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "分享活动id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 507, message = "信息签名不正确"),
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/shareactivities/{id}/online")
    public Object onlineShareActivity(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @LoginUser Long loginUser, @LoginName String loginUsername) {
        ReturnObject ret = shareActivityService.onlineShareActivity(id, loginUser, loginUsername);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "下线分享活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "分享活动id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 507, message = "信息签名不正确"),
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/shareactivities/{id}/offline")
    public Object offlineShareActivity(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @LoginUser Long loginUser, @LoginName String loginUsername) {
        ReturnObject ret = shareActivityService.offlineShareActivity(id, loginUser, loginUsername);
        return Common.decorateReturnObject(ret);
    }

}
