package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.bo.Goods;
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import cn.edu.xmu.oomall.goods.service.GoodsService;
import cn.edu.xmu.oomall.goods.service.ProductService;
import cn.edu.xmu.privilegegateway.annotation.annotation.Audit;
import cn.edu.xmu.privilegegateway.annotation.annotation.LoginName;
import cn.edu.xmu.privilegegateway.annotation.annotation.LoginUser;
import cn.edu.xmu.privilegegateway.aop.PageAspect;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static cn.edu.xmu.oomall.core.util.Common.cloneVo;
import static cn.edu.xmu.oomall.core.util.Common.processFieldErrors;

/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
@Api(value = "商品", tags = "goods")
@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class GoodsController {

    @Autowired
    GoodsService goodsService;
    @Autowired
    ProductService productService;

    @ApiOperation(value="查看运费模板用到的商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="fid",value="运费模板id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name = "page", value = "页码", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数目", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code=503,message = "字段不合法"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping("shops/{shopId}/freightmodels/{fid}/products")
    @Audit(departName = "shops")
    public Object getFreightProducts(@PathVariable("shopId") Long shopId,@PathVariable("fid") Long fid,@RequestParam(value = "page", required = false) Integer page,
                                     @RequestParam(value = "pageSize", required = false) Integer pageSize,@LoginUser Long loginUserId, @LoginName String loginUserName)
    {
        ReturnObject<PageInfo<VoObject>> retVoObject =
                productService.listProductsByFreightId(shopId,fid, page, pageSize);
        return Common.decorateReturnObject(Common.getPageRetObject(retVoObject));
    }

    @ApiOperation(value="新建商品集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="goodsVo",value="商品集合详细信息",dataType = "GoodsVo",paramType = "body"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code=503,message = "字段不合法"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping("shops/{id}/goods")
    @Audit(departName = "shops")
    public Object insertGoods(@PathVariable("id") Long shopId, @Validated @RequestBody GoodsVo goodsVo, BindingResult bindingResult, @LoginUser Long loginUserId, @LoginName String loginUserName)
    {
        if(bindingResult.hasErrors()){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID,"传入的RequestBody参数格式不合法"));
        }
        ReturnObject ro = goodsService.insertGoods(shopId,goodsVo,loginUserId,loginUserName);
        return Common.getRetObject(ro);

    }

    @ApiOperation(value="修改特定商品集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="商品集合id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="goodsVo",value="商品集合详细信息",dataType = "GoodsVo",paramType = "body"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code=503,message = "字段不合法"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PutMapping("shops/{shopId}/goods/{id}")
    @Audit(departName = "shops")
    public Object updateGoods(@PathVariable("shopId") Long shopId,@PathVariable("id") Long id,@RequestBody GoodsVo goodsVo,BindingResult bindingResult,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        if(bindingResult.hasErrors()){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID,"传入的RequestBody参数格式不合法"));
        }
        return Common.decorateReturnObject(goodsService.updateGoods(shopId,id,goodsVo,loginUserId,loginUserName));

    }

    @ApiOperation(value="获取特定商品集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="商品集合id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping(value="/shops/{shopId}/goods/{id}" )
    @Audit(departName = "shops")
    public Object searchGoods(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.getRetObject(goodsService.searchById(shopId,id));
    }

    @ApiOperation(value="删除特定商品集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="商品集合id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @DeleteMapping(value="shops/{shopId}/goods/{id}")
    @Audit(departName = "shops")
    public Object deleteGoods(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(goodsService.deleteGoods(shopId,id));
    }


    @ApiOperation(value="发布货品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="货品id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PutMapping(value="shops/{shopId}/products/{id}/publish")
    @Audit(departName = "shops")
    public Object publishProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.getRetObject(productService.pulishProduct(shopId,id));
    }

    @ApiOperation(value="上架货品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="货品id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PutMapping(value="shops/{shopId}/products/{id}/onshelves")
    @Audit(departName = "shops")
    public Object onshelvesProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(productService.onshelvesProduct(shopId,id));
    }

    @ApiOperation(value="下架货品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="货品id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PutMapping(value="shops/{shopId}/products/{id}/offshelves")
    @Audit(departName = "shops")
    public Object offshelvesProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(productService.offshelvesProduct(shopId,id));
    }

    @ApiOperation(value="解禁货品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="货品id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PutMapping(value="shops/{shopId}/products/{id}/allow")
    @Audit(departName = "shops")
    public Object allowProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(productService.allowProduct(shopId,id));
    }

    @ApiOperation(value="禁售货品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="货品id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PutMapping(value="shops/{shopId}/products/{id}/prohibit")
    @Audit(departName = "shops")
    public Object prohibitProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(productService.prohibitProduct(shopId,id));
    }
}
