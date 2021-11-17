package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import cn.edu.xmu.oomall.goods.service.GoodsService;
import cn.edu.xmu.oomall.goods.service.ProductService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("shops/{id}/goods")
    public Object insertGoods(@PathVariable("id") Long shopId, @RequestBody GoodsVo goodsVo)
    {
        goodsVo.setShopId(shopId);
        ReturnObject returnObject = goodsService.insertGoods(goodsVo);
        return Common.getRetObject(returnObject);

    }

    @PutMapping("shops/{shopId}/goods/{id}")
    public Object updateGoods(@PathVariable("shopId") Long shopId,@PathVariable("id") Long id,@RequestBody GoodsVo goodsVo)
    {
        return Common.decorateReturnObject(goodsService.updateGoods(shopId,id,goodsVo));

    }

    @GetMapping(value="/shops/{shopId}/goods/{id}" )
    public Object searchGoods(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id)
    {
        return Common.getRetObject(goodsService.searchById(shopId,id));
    }

    @DeleteMapping(value="shops/{shopId}/goods/{id}")
    public Object deleteGoods(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id)
    {
        return Common.decorateReturnObject(goodsService.deleteGoods(shopId,id));
    }


    @PutMapping(value="shops/{shopId}/products/{id}/publish")
    public Object publishProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id)
    {
        return Common.getRetObject(productService.pulishProduct(shopId,id));
    }

    @PutMapping(value="shops/{shopId}/products/{id}/onshelves")
    public Object onshelvesProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id)
    {
        return Common.decorateReturnObject(productService.onshelvesProduct(shopId,id));
    }

    @PutMapping(value="shops/{shopId}/products/{id}/offshelves")
    public Object offshelvesProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id)
    {
        return Common.decorateReturnObject(productService.offshelvesProduct(shopId,id));
    }

    @PutMapping(value="shops/{shopId}/products/{id}/allow")
    public Object allowProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id)
    {
        return Common.decorateReturnObject(productService.allowProduct(shopId,id));
    }

    @PutMapping(value="shops/{shopId}/products/{id}/prohibit")
    public Object prohibitProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id)
    {
        return Common.decorateReturnObject(productService.prohibitProduct(shopId,id));
    }
}
