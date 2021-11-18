package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.bo.Goods;
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import cn.edu.xmu.oomall.goods.service.GoodsService;
import cn.edu.xmu.oomall.goods.service.ProductService;
import cn.edu.xmu.privilegegateway.annotation.annotation.Audit;
import cn.edu.xmu.privilegegateway.annotation.annotation.LoginName;
import cn.edu.xmu.privilegegateway.annotation.annotation.LoginUser;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static cn.edu.xmu.oomall.core.util.Common.cloneVo;

/**
 * @author Huang Tianyue
 * 2021.11.15
 **/
@Api(value = "商品", tags = "goods")
@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class GoodsController {

    @Autowired
    GoodsService goodsService;
    @Autowired
    ProductService productService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @PostMapping("shops/{id}/goods")
    @Audit
    public Object insertGoods(@PathVariable("id") Long shopId, @RequestBody GoodsVo goodsVo, @LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        ReturnObject returnObject = goodsService.insertGoods(shopId,goodsVo);
        return Common.getRetObject(returnObject);

    }

    @PutMapping("shops/{shopId}/goods/{id}")
    @Audit
    public Object updateGoods(@PathVariable("shopId") Long shopId,@PathVariable("id") Long id,@RequestBody GoodsVo goodsVo,@LoginUser Long loginUserId,@LoginName String loginUserNamee)
    {
        return Common.decorateReturnObject(goodsService.updateGoods(shopId,id,goodsVo));

    }

    @GetMapping(value="/shops/{shopId}/goods/{id}" )
    @Audit
    public Object searchGoods(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.getRetObject(goodsService.searchById(shopId,id));
    }

    @DeleteMapping(value="shops/{shopId}/goods/{id}")
    @Audit
    public Object deleteGoods(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(goodsService.deleteGoods(shopId,id));
    }


    @PutMapping(value="shops/{shopId}/products/{id}/publish")
    @Audit
    public Object publishProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.getRetObject(productService.pulishProduct(shopId,id));
    }

    @PutMapping(value="shops/{shopId}/products/{id}/onshelves")
    @Audit
    public Object onshelvesProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(productService.onshelvesProduct(shopId,id));
    }

    @PutMapping(value="shops/{shopId}/products/{id}/offshelves")
    @Audit
    public Object offshelvesProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(productService.offshelvesProduct(shopId,id));
    }

    @PutMapping(value="shops/{shopId}/products/{id}/allow")
    @Audit
    public Object allowProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(productService.allowProduct(shopId,id));
    }

    @PutMapping(value="shops/{shopId}/products/{id}/prohibit")
    @Audit
    public Object prohibitProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(productService.prohibitProduct(shopId,id));
    }
}
