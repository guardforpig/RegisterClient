package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import cn.edu.xmu.oomall.goods.service.OnSaleGetService;
import cn.edu.xmu.privilegegateway.annotation.annotation.Audit;
import cn.edu.xmu.privilegegateway.annotation.annotation.LoginName;
import cn.edu.xmu.privilegegateway.annotation.annotation.LoginUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pagehelper.PageInfo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/13 02:44
 **/
@RestController
@RequestMapping(produces = "application/json;charset=UTF-8")
public class OnSaleGetController {

    @Autowired
    private OnSaleGetService onSaleService;

    /**
     * 管理员查询特定商品的价格浮动
     * @param loginUser
     * @param loginUsername
     * @param shopId
     * @param id
     * @param page
     * @param pageSize
     * @return
     */
    @Audit
    @GetMapping("shops/{shopId}/products/{id}/onsales")
    public Object selectCertainOnsale(@LoginUser Long loginUser, @LoginName String loginUsername,
                                      @PathVariable("shopId")Long shopId, @PathVariable("id")Long id,
                                      @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                      @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        ReturnObject<PageInfo<VoObject>> returnObject=onSaleService.selectCertainOnsale(shopId,id,page,pageSize);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员查询特定价格浮动的详情
     * @param loginUser
     * @param loginUsername
     * @param shopId
     * @param id
     * @return 只返回普通和秒杀，其他类型出403，返回的是完整订单
     */
    @Audit
    @GetMapping("shops/{shopId}/onsales/{id}")
    public Object selectOnsale(@LoginUser Long loginUser,@LoginName String loginUsername,
                               @PathVariable("shopId")Long shopId, @PathVariable("id")Long id){
        ReturnObject returnObject = onSaleService.selectOnsale(shopId,id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 查询团购预售活动的所有价格浮动
     * @param id
     * @param state
     * @param page
     * @param pageSize
     * @return
     */
    @Audit
    @GetMapping("internal/shops/{did}/activities/{id}/onsales")
    public Object selectActivities(@LoginUser Long loginUser, @LoginName String loginUsername,
                                   @PathVariable("did")Long did, @PathVariable("id")Long id, @RequestParam(required = false) Byte state,
                                   @RequestParam(value = "beginTime",required = false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                                   @RequestParam(value = "endTime",required = false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                                   @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                   @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        if(state!=null){
            if(state< OnSale.State.DRAFT.getCode()||state>OnSale.State.OFFLINE.getCode()){
                ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.FIELD_NOTVALID);
                return Common.decorateReturnObject(returnObjectNotValid);
            }
        }
        if(beginTime!=null&&endTime!=null&&beginTime.isAfter(endTime)){
            ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.LATE_BEGINTIME);
            return Common.decorateReturnObject(returnObjectNotValid);
        }
        ReturnObject returnObject= onSaleService.selectActivities(id,did,state,beginTime,endTime,page,pageSize);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 内部API-查询特定活动的所有价格浮动
     * @param id
     * @param state
     * @param page
     * @param pageSize
     * @return
     */
    @Audit
    @GetMapping("internal/shops/{did}/shareactivities/{id}/onsales")
    public Object selectShareActivities(@LoginUser Long loginUser,@LoginName String loginUsername,
                                        @PathVariable("did")Long did, @PathVariable("id")Long id,@RequestParam(required = false) Byte state,
                                        @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                        @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        if(state!=null){
            if(state< OnSale.State.DRAFT.getCode()||state>OnSale.State.OFFLINE.getCode()){
                ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.FIELD_NOTVALID);
                return Common.decorateReturnObject(returnObjectNotValid);
            }
        }
        ReturnObject returnObject = onSaleService.selectShareActivities(did,id,state,page,pageSize);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 内部API- 查询特定价格浮动的详情
     * @param id
     * @return 所有类型都会返回
     */
    @Audit
    @GetMapping( "internal/onsales/{id}")
    public Object selectFullOnsale(@LoginUser Long loginUser,@LoginName String loginUsername,@PathVariable("id")Long id) {
        ReturnObject returnObject = onSaleService.selectFullOnsale(id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员查询所有商品的价格浮动
     * @param loginUser
     * @param loginUsername
     * @param shopId
     * @param productId
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Audit
    @GetMapping("internal/onsales")
    public Object selectAnyOnsale(@LoginUser Long loginUser, @LoginName String loginUsername,
                                  @RequestParam(required = false) Long shopId, @RequestParam(required = false) Long productId,
                                  @RequestParam(value = "beginTime",required = false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                                  @RequestParam(value = "endTime",required = false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")LocalDateTime endTime,
                                  @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                  @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        if(beginTime!=null&&endTime!=null&&beginTime.isAfter(endTime)){
            ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.LATE_BEGINTIME);
            return Common.decorateReturnObject(returnObjectNotValid);
        }
        ReturnObject returnObject= onSaleService.selectAnyOnsale(shopId,productId,beginTime,endTime,page,pageSize);
        return Common.decorateReturnObject(returnObject);
    }



}
