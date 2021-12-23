package cn.edu.xmu.oomall.activity.controller;

import cn.edu.xmu.oomall.activity.constant.TimeFormat;
import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.model.bo.AdvanceSaleState;
import cn.edu.xmu.oomall.activity.model.vo.AdvanceSaleModifyVo;
import cn.edu.xmu.oomall.activity.service.AdvanceSaleService;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.oomall.activity.model.bo.AdvanceSale;
import cn.edu.xmu.oomall.activity.model.vo.AdvanceSaleVo;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import cn.edu.xmu.oomall.core.util.ReturnNo;

/**
 * @author Gxc 22920192204194
 */
@RestController
@Slf4j
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8",consumes = "application/json;charset=UTF-8")
public class AdvanceSaleController {
    @Autowired
    AdvanceSaleService advanceSaleService;

    @Autowired
    GoodsService goodsService;
    @Autowired
    private HttpServletResponse httpServletResponse;
    /**
     * @author Gxc 22920192204194
     */
    @Audit
    @ApiOperation(value = "商铺管理员上线预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "预售活动id", required = true)
    })
    @PutMapping("/shops/{shopId}/advancesales/{id}/online")
    public Object onlineAdvancesale(Long adminId,String adminName, @PathVariable("shopId")Long shopId, @PathVariable("id")Long advancesaleId) {
        adminId=1L;adminName="店铺管理员";
        ReturnObject returnObject = null;
        returnObject= advanceSaleService.onlineAdvancesale(adminId,adminName,shopId,advancesaleId);
        return Common.decorateReturnObject(returnObject);
    }
    @Audit
    @ApiOperation(value = "商铺管理员下线预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "预售活动id", required = true)
    })
    @PutMapping("/shops/{shopId}/advancesales/{id}/offline")
    public Object offlineAdvancesale(Long adminId,String adminName, @PathVariable("shopId")Long shopId, @PathVariable("id")Long advancesaleId)  {
        adminId=1L;adminName="店铺管理员";
        ReturnObject returnObject = null;
        returnObject= advanceSaleService.offlineAdvancesale(adminId,adminName,shopId,advancesaleId);
        return Common.decorateReturnObject(returnObject);
    }
    @Audit
    @ApiOperation(value = "商铺管理员修改预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "预售活动id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AdvanceSaleModifyVo", name = "advancesalemodifyvo", value = "修改内容", required = true)
    })
    @PutMapping("/shops/{shopId}/advancesales/{id}")
    public Object modifyAdvancesale(Long adminId, String adminName, @PathVariable("shopId")Long shopId, @PathVariable("id")Long advancesaleId,
                                    @RequestBody AdvanceSaleModifyVo advanceSaleModifyVo){
        adminId=1L;adminName="店铺管理员";
        ReturnObject returnObject=null;
        //开始时间晚于结束时间
        if(advanceSaleModifyVo.getBeginTime()!=null&&advanceSaleModifyVo.getEndTime()!=null){
        if (advanceSaleModifyVo.getBeginTime().isAfter(advanceSaleModifyVo.getEndTime())) {
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.LATE_BEGINTIME,"开始时间不能晚于结束时间"));
        }}
        //支付尾款时间晚于活动结束时间
        if(advanceSaleModifyVo.getPayTime()!=null&&advanceSaleModifyVo.getEndTime()!=null){
        if (advanceSaleModifyVo.getPayTime().isAfter(advanceSaleModifyVo.getEndTime())) {
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.ACT_LATE_PAYTIME,"尾款支付时间晚于活动结束时间"));
        }}
        //支付尾款时间早于活动开始时间
        if(advanceSaleModifyVo.getBeginTime()!=null&&advanceSaleModifyVo.getPayTime()!=null){
        if (advanceSaleModifyVo.getBeginTime().isAfter(advanceSaleModifyVo.getPayTime())) {
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.ACT_EARLY_PAYTIME,"尾款支付时间早于于活动开始时间"));
        }}
        returnObject= advanceSaleService.modifyAdvancesale(adminId,shopId,adminName,advancesaleId,advanceSaleModifyVo);
        return Common.decorateReturnObject(returnObject);
    }
    @Audit
    @ApiOperation(value = "商铺管理员删除预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "预售活动id", required = true)
    })
    @DeleteMapping("/shops/{shopId}/advancesales/{id}")
    public Object deleteAdvancesale(Long adminId,String adminName, @PathVariable("shopId")Long shopId, @PathVariable("id")Long advancesaleId) {
        adminId=1L;adminName="店铺管理员";
        ReturnObject returnObject = null;
        returnObject= advanceSaleService.deleteAdvancesale(adminId,shopId,advancesaleId);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * @author Jiawei Zheng
     * @date 2021-11-26
     */

    /**
     * 获得预售活动的所有状态
     * @return
     */
    @ApiOperation(value = "获得预售活动的所有状态")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")})
    @GetMapping(value = "/advancesales/states")
    public Object getAdvancesaleState() {
        ReturnObject returnObject=advanceSaleService.getAdvanceSaleState();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 查询所有上线的预售活动
     * @param shopId
     * @param productId
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "查询所有上线的预售活动")
    @ApiImplicitParams(value={
            @ApiImplicitParam(name = "shopId",dataType = "Long",value = "商铺id",required = false),
            @ApiImplicitParam(name = "productId",dataType = "Long",value = "货品id",required = false),
            @ApiImplicitParam(name = "beginTime",dataType = "LocalDateTime",value = "开始时间",required = false),
            @ApiImplicitParam(name = "endTime",dataType = "LocalDateTime",value = "结束时间",required = false),
            @ApiImplicitParam(name="page",dataType = "Integer",value = "页数",required = false),
            @ApiImplicitParam(name="pageSize",dataType = "Integer",value = "页大小",required = false)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")})
    @GetMapping(value = "/advancesales")
    public Object queryAllOnlineAdvanceSales(
            @RequestParam(name = "shopId", required = false) Long shopId,
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(required = false) @DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime beginTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime endTime,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "pageSize",  required = false) Integer pageSize) {
        //输入参数合法性检查
        LocalDateTime begin=null;
        LocalDateTime end=null;
        if(beginTime!=null&&endTime!=null) {
            begin=beginTime.toLocalDateTime();
            end=endTime.toLocalDateTime();
            if(beginTime.isAfter(endTime)) {
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME, "开始时间不能晚于结束时间"));
            }
        }
        ReturnObject ret = advanceSaleService.getAllAdvanceSale(shopId,productId, AdvanceSale.state.ONLINE.getCode(), begin,end,page,pageSize);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 查询上线预售活动的详细信息
     * @param id
     * @return
     */
    @ApiOperation(value = "查询上线预售活动的详细信息")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "path",name = "id",dataType = "Long",value = "预售活动id",required = false)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")})
    @GetMapping(value = "/advancesales/{id}")
    public Object queryOnlineAdvanceSaleInfo(
            @PathVariable(name = "id") Long id) {
        ReturnObject ret=advanceSaleService.getAdvanceSaleInfo(id,AdvanceSaleState.ONLINE);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 管理员查询特定商铺所有预售活动
     * @param shopId
     * @param productId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "管理员查询特定商铺所有预售活动")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path",name = "shopId",dataType = "Long",value = "商铺id",required = false),
            @ApiImplicitParam(name = "productId",dataType = "Long",value = "货品id",required = false),
            @ApiImplicitParam(name = "state",dataType = "Byte",value = "活动状态",required = false),
            @ApiImplicitParam(name = "beginTime",dataType = "LocalDateTime",value = "开始时间",required = false),
            @ApiImplicitParam(name = "endTime",dataType = "LocalDateTime",value = "结束时间",required = false),
            @ApiImplicitParam(name="page",dataType = "Integer",value = "页数",required = false),
            @ApiImplicitParam(name="pageSize",dataType = "Integer",value = "页大小",required = false)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")})
    @GetMapping(value = "/shops/{shopId}/advancesales")
    @Audit(departName = "shops")
    public Object queryAllShopAdvanceSale(
            @LoginUser Long loginUserId,
            @LoginName String loginUserName,
            @PathVariable("shopId") Long shopId,
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "state", required = false) Byte state,
            @RequestParam(required = false) @DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime beginTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime endTime,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "pageSize",  required = false) Integer pageSize) {
        LocalDateTime begin=null;
        LocalDateTime end=null;
        //输入参数合法性检查
        if(beginTime!=null&&endTime!=null) {
            begin=beginTime.toLocalDateTime();
            end=endTime.toLocalDateTime();
            if(beginTime.isAfter(endTime)) {
                return  Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME, "开始时间不能晚于结束时间"));
            }
        }
        ReturnObject ret = advanceSaleService.getAllAdvanceSale(shopId,productId,state,begin,end,page,pageSize);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 管理员新增预售
     * @param loginUserId
     * @param loginUserName
     * @param shopId
     * @param id
     * @param advanceSaleVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员新增预售")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path",name = "shopId",dataType = "Long",value = "商铺id",required = true),
            @ApiImplicitParam(paramType = "path",name = "id",dataType = "Long",value = "货品id",required = true),
            @ApiImplicitParam(paramType = "body", dataType = "Object", name = "body", value ="新增的预售活动信息" ,required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 902,message = "商铺销售时间冲突"),
            @ApiResponse(code = 947, message = "开始时间不能晚于结束时间"),
            @ApiResponse(code = 948, message = "尾款支付时间晚于活动结束时间"),
            @ApiResponse(code = 949, message = "尾款支付时间早于于活动开始时间")
    })
    @PostMapping(value = "/shops/{shopId}/products/{id}/advancesales")
    @Audit(departName = "shops")
    public Object addAdvanceSale(
            @LoginUser Long loginUserId,
            @LoginName String loginUserName,
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @Valid @RequestBody AdvanceSaleVo advanceSaleVo,
            BindingResult bindingResult) {
        try{
        Object fieldErrors = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (fieldErrors!=null) {
            return fieldErrors;
        }
        //开始时间晚于结束时间
        if (advanceSaleVo.getBeginTime().isAfter(advanceSaleVo.getEndTime())) {
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.LATE_BEGINTIME,"开始时间不能晚于结束时间"));
        }
        //支付尾款时间晚于活动结束时间
        if (advanceSaleVo.getPayTime().isAfter(advanceSaleVo.getEndTime())) {
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.ACT_LATE_PAYTIME,"尾款支付时间晚于活动结束时间"));
        }
        //支付尾款时间早于活动开始时间
        if (advanceSaleVo.getBeginTime().isAfter(advanceSaleVo.getPayTime())) {
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.ACT_EARLY_PAYTIME,"尾款支付时间早于于活动开始时间"));
        }
        ReturnObject ret = advanceSaleService.addAdvanceSale(loginUserId,loginUserName, shopId,id, advanceSaleVo);
        return Common.decorateReturnObject(ret);}
        catch(Exception e){ return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 管理员查询商铺的特定预售活动
     * @param loginUserId
     * @param loginUserName
     * @param shopId
     * @param id
     * @return
     */
    @ApiOperation(value = "管理员查询商铺的特定预售活动")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path",name = "shopId",dataType = "Long",value = "商铺id",required = true),
            @ApiImplicitParam(paramType = "path",name = "shopId",dataType = "Long",value = "商铺id",required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")})
    @GetMapping(value = "/shops/{shopId}/advancesales/{id}")
    @Audit(departName = "shops")
    public Object queryShopAdvanceSaleInfo(
            @LoginUser Long loginUserId,
            @LoginName String loginUserName,
            @PathVariable(name = "shopId") Long shopId,
            @PathVariable(name = "id") Long id) {
        ReturnObject ret= advanceSaleService.getShopAdvanceSaleInfo(id,shopId);
        return Common.decorateReturnObject(ret);
    }
}
