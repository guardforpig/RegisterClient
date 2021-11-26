package cn.edu.xmu.oomall.activity.controller;

import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.model.vo.AdvanceSaleModifyVo;
import cn.edu.xmu.oomall.activity.service.AdvanceSaleService;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.annotation.Audit;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import cn.edu.xmu.privilegegateway.annotation.annotation.LoginName;
import cn.edu.xmu.privilegegateway.annotation.annotation.LoginUser;
import cn.edu.xmu.oomall.activity.model.bo.AdvanceSale;
import cn.edu.xmu.oomall.activity.model.vo.AdvanceSaleVo;
import javax.validation.Valid;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import cn.edu.xmu.oomall.core.util.ReturnNo;

/**
 * @author Gxc 22920192204194
 */
@RestController
@Slf4j
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
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
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")})
    @GetMapping(value = "/advancesales")
    public Object queryAllAdvanceSales(
            @RequestParam(name = "shopId", required = false) Long shopId,
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "beginTime", required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSZ") LocalDateTime beginTime,
            @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSZ")LocalDateTime endTime,
            @RequestParam(name = "page", required = false,defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize",  required = false,defaultValue = "10") Integer pageSize) {
        //输入参数合法性检查
        if (shopId!=null&&shopId < 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "shopId不能为负数"));
        }
        if (productId!=null&&productId < 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "productId不能为负数"));
        }
        if(beginTime!=null&&endTime!=null)
        {
            if(beginTime.isAfter(endTime))
            {
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID, "开始时间不能晚于结束时间"));
            }
        }
        ReturnObject onlineAdvanceSale = advanceSaleService.getAllAdvanceSale(shopId,productId, AdvanceSale.state.ONLINE.getCode(), beginTime,endTime,page,pageSize);
        return Common.decorateReturnObject(onlineAdvanceSale);
    }

    /**
     * 查询上线预售活动的详细信息
     * @param id
     * @return
     */
    @ApiOperation(value = "查询上线预售活动的详细信息")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")})
    @GetMapping(value = "/advancesales/{id}")
    public Object queryOnlineAdvanceSaleDetails(
            @PathVariable(name = "id") Long id) {
        return Common.decorateReturnObject(advanceSaleService.getOnlineAdvanceSaleDetails(id));
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
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")})
    @GetMapping(value = "/shops/{shopId}/advancesales")
    @Audit(departName = "shops")
    public Object queryShopAdvanceSale(
            @LoginUser Long loginUserId, @LoginName String loginUserName,
            @PathVariable(name = "shopId", required = true) Long shopId,
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "state", required = false) Byte state,
            @RequestParam(name = "beginTime", required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime beginTime,
            @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")LocalDateTime endTime,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "pageSize",  required = false) Integer pageSize) {
        //输入参数合法性检查
        if (shopId <= 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID, "shopId错误"));
        }
        if (productId!=null&&productId < 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "productId错误"));
        }
        if(beginTime!=null&&endTime!=null) {
            if(beginTime.isAfter(endTime)) {
                return  Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME, "开始时间不能晚于结束时间"));
            }
        }
        ReturnObject onlineAdvanceSale = advanceSaleService.getAllAdvanceSale(shopId,productId,state,beginTime,endTime,page,pageSize);
        return Common.decorateReturnObject(Common.getPageRetObject(onlineAdvanceSale));

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
    @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")})
    @PostMapping(value = "/shops/{shopId}/products/{id}/advanceSale")
    @Audit(departName = "shops")
    public Object addAdvanceSale(
            @LoginUser Long loginUserId,
            @LoginName String loginUserName,
            @ApiParam(value = "店铺id", required = true) @PathVariable("shopId") Long shopId,
            @ApiParam(value = "货品id", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "可修改的预售活动信息", required = true) @Valid @RequestBody AdvanceSaleVo advanceSaleVo,
            BindingResult bindingResult) {
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
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.ACT_LATE_PAYTIME,"尾款支付时间不能晚于活动结束时间"));
        }
        //尾款支付时间早于活动开始时间
        if (advanceSaleVo.getBeginTime().isAfter(advanceSaleVo.getPayTime())) {
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.ACT_EARLY_PAYTIME,"尾款支付时间不能早于活动开始时间"));
        }
        ReturnObject ret = advanceSaleService.addAdvanceSale(loginUserId,loginUserName, shopId,id, advanceSaleVo);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 管理员查询商铺的特定预售活动
     * @param shopId
     * @param id
     * @return
     */
    @ApiOperation(value = "管理员查询商铺的特定预售活动")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")})
    @GetMapping(value = "/shops/{shopId}/advancesales/{id}")
    @Audit
    public Object queryShopAdvanceSaleInfo(
            @LoginUser Long loginUserId, @LoginName String loginUserName,
            @PathVariable(name = "shopId") Long shopId,
            @PathVariable(name = "id") Long id) {
        //输入参数合法性检查
        ReturnObject returnObject= advanceSaleService.getShopAdvanceSale(shopId, id);
        return Common.decorateReturnObject(returnObject);

    }
}
