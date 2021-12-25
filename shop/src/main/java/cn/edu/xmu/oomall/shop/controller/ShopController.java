
package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.shop.model.bo.Shop;
import cn.edu.xmu.oomall.shop.model.vo.ShopConclusionVo;
import cn.edu.xmu.oomall.shop.model.vo.ShopRetVo;
import cn.edu.xmu.oomall.shop.model.vo.ShopVo;
import cn.edu.xmu.oomall.shop.service.ShopService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.Depart;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Api(value = "店铺", tags = "shop")
@RestController /*Restful的Controller对象*/
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
@Component
public class ShopController {
    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private ShopService shopService;

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "获得店铺简单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "shopToken", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "商店id", required = true, dataType = "Long", paramType = "path")
    })
    @GetMapping(value = "/shops/{id}")
    public Object getSimpleShopById(@PathVariable Long id) {
        ReturnObject ret = shopService.getSimpleShopByShopId(id);
        return Common.decorateReturnObject(ret);
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "管理员获得店铺信息")
    @GetMapping(value = "/shops/{id}/shops")
    @Audit(departName = "shops")
    public Object getAllShop(@PathVariable Long id, @Depart Long departId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) {
        if (id != 0 || departId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject<PageInfo<Object>> ret = shopService.getAllShop(page, pageSize);
        return Common.decorateReturnObject(Common.getPageRetVo(ret, ShopRetVo.class));
    }


    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "获得店铺的所有状态")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功")})
    @GetMapping(value = "/shops/states")
    public Object getshopState() {
        ReturnObject<List> returnObject = shopService.getShopStates();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "店家申请店铺")
    @ApiImplicitParam(name = "authorization", value = "shopToken", required = true, dataType = "String", paramType = "header")
    @ApiResponses(value = {
            @ApiResponse(code = 969, message = "用户已经有店铺"),
            @ApiResponse(code = 200, message = "成功")})
    @Audit(departName = "shops")
    @PostMapping(value = "/shops")
    public Object addShop(@Validated @RequestBody ShopVo shopvo, BindingResult bindingResult, @Depart Long shopid, @LoginUser Long loginUser, @LoginName String loginUsername) {
        Object obj = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != obj) {
            return obj;
        }
        ReturnObject ret=new ReturnObject();
        if (shopid.equals(-1L)) {
            ret = shopService.newShop(shopvo, loginUser, loginUsername);
        } else {
            ret = new ReturnObject(ReturnNo.SHOP_USER_HASSHOP, "您已经拥有店铺，无法重新申请");
        }
        if (ret.getCode().equals(ReturnNo.OK))
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(ret);
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "店家修改店铺信息", nickname = "modifyShop", notes = "", tags = {"shop",})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "shopToken", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "商店id", required = true, dataType = "Long", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 507, message = "该店铺无法修改")})
    @Audit(departName = "shops")
    @PutMapping(value = "/shops/{id}")
    public Object modifyShop(@Depart Long departId, @Validated @RequestBody ShopVo shopVo, BindingResult bindingResult, @PathVariable Long id, @LoginUser Long loginUser, @LoginName String loginUsername) {

        if (departId != id && departId != 0L) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        Object obj = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != obj) {
            return obj;
        } else {
            ReturnObject ret = shopService.updateShop(id, shopVo, loginUser, loginUsername);
            return Common.decorateReturnObject(ret);
        }
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "管理员或店家关闭店铺", nickname = "deleteShop", notes = "如果店铺从未上线则物理删除", tags = {"shop",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 180, message = "该店铺无法被执行关闭操作")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "shopToken", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "商店id", required = true, dataType = "Long", paramType = "path")
    })
    @DeleteMapping(value = "/shops/{id}")
    @Audit(departName = "shops")
    public Object deleteShop(@ApiParam(value = "shop ID", required = true) @PathVariable("id") Long id, @Depart Long departId, @LoginUser Long loginUser, @LoginName String loginUsername) {
        if (departId != 0L) {
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject ret = shopService.deleteShopById(id, loginUser, loginUsername);
        return Common.decorateReturnObject(ret);
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "平台管理员审核店铺信息", nickname = "shopsShopIdNewshopsIdAuditPut", notes = "", tags = {"shop",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 150, message = "该店铺不是待审核状态")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "adminToken", required = true, dataType = "String", paramType = "header")
    })
    @Audit(departName = "shops")
    @PutMapping(value = "/shops/{shopId}/newshops/{id}/audit")
    public Object auditShop(@LoginUser Long loginUser, @LoginName String loginUsername, @Depart Long departId, @PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @RequestBody ShopConclusionVo conclusion) {


        if (id != departId && departId != 0L)
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));

        ReturnObject ret = shopService.passShop(id, conclusion, loginUser, loginUsername);
        return Common.decorateReturnObject(ret);

    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "管理员上线店铺", nickname = "shopsIdOnshelvesPut", notes = "", tags = {"shop",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 160, message = "该店铺无法上线")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "adminToken", required = true, dataType = "String", paramType = "header")
    })
    @Audit(departName = "shops")
    @PutMapping(value = "/shops/{id}/online")
    public Object shopsIdOnshelvesPut(@Depart Long departId, @PathVariable("id") Long id, @LoginUser Long loginUser, @LoginName String loginUsername) {

        if (departId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject ret = shopService.onShelfShop(id, loginUser, loginUsername);
        return Common.decorateReturnObject(ret);
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "管理员下线店铺", nickname = "shopsIdOffshelvesPut", notes = "", tags = {"shop",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 170, message = "该店铺无法下线")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "adminToken", required = true, dataType = "String", paramType = "header")
    })
    @PutMapping(value = "/shops/{id}/offline")
    @Audit(departName = "shops")
    public Object shopsIdOffshelvesPut(@Depart Long departId, @PathVariable("id") Long id, @LoginUser Long loginUser, @LoginName String loginUsername) {

        if (departId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject ret = shopService.offShelfShop(id, loginUser, loginUsername);
        return Common.decorateReturnObject(ret);
    }

}
