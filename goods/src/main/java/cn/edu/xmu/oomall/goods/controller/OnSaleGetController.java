package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.service.OnSaleGetService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Object selectCertainOnsale(@LoginUser Long loginUser,@LoginName String loginUsername,
                                      @PathVariable("shopId")Long shopId, @PathVariable("id")Long id,
                                      @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                      @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        loginUsername="admin";
        loginUser=1L;
        if(page<0||pageSize<0){
            ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.FIELD_NOTVALID);
            return Common.decorateReturnObject(returnObjectNotValid);
        }
        ReturnObject<PageInfo<VoObject>> returnObject=onSaleService.selectCertainOnsale(shopId,id,loginUser,loginUsername,page,pageSize);
        return Common.decorateReturnObject(Common.getPageRetObject(returnObject));
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
        loginUsername="admin";
        loginUser=1L;
        ReturnObject returnObject;
        returnObject = onSaleService.selectOnsale(shopId,id,loginUser,loginUsername);
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
    @GetMapping("internal/activities/{id}/onsales")
    public Object selectActivities(@PathVariable("id")Long id,@RequestParam Byte state,
                                   @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                   @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        if(state<0||state>3){
            ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.FIELD_NOTVALID);
            return Common.decorateReturnObject(returnObjectNotValid);
        }
        ReturnObject returnObject= onSaleService.selectActivities(id,state,page,pageSize);
        return Common.decorateReturnObject(Common.getPageRetObject(returnObject));
    }

    /**
     * 内部API-查询特定活动的所有价格浮动
     * @param id
     * @param state
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("internal/shareactivities/{id}/onsales")
    public Object selectShareActivities(@PathVariable("id")Long id,@RequestParam Byte state,
                                        @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                        @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        if(state<0||state>3){
            ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.FIELD_NOTVALID);
            return Common.decorateReturnObject(returnObjectNotValid);
        }
        ReturnObject returnObject = onSaleService.selectShareActivities(id,state,page,pageSize);
        return Common.decorateReturnObject(Common.getPageRetObject(returnObject));
    }

    /**
     * 内部API- 查询特定价格浮动的详情
     * @param id
     * @return 所有类型都会返回
     */
    @GetMapping( "internal/onsales/{id}")
    public Object selectFullOnsale(@PathVariable("id")Long id) {
        ReturnObject returnObject;
        returnObject = onSaleService.selectFullOnsale(id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员查询所有商品的价格浮动
     * @param id
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("internal/products/{id}/onsales")
    public Object selectAnyOnsale(@PathVariable("id")Long id,
                                  @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                  @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        ReturnObject returnObject= onSaleService.selectAnyOnsale(id,page,pageSize);
        return Common.decorateReturnObject(Common.getPageRetObject(returnObject));
    }



}
