package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import cn.edu.xmu.oomall.goods.service.GoodsService;
import cn.edu.xmu.oomall.goods.service.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author Huang Tianyue
 * 2021.11.15
 **/
@RestController
@RequestMapping(value = "",produces = "application/json;charset=UTF-8")
public class GoodsController {

    @Autowired
    GoodsService goodsService;
    @Autowired
    ProductService productService;


    //@PostMapping("{id}/goods")

    @PostMapping("/shops/{id}/goods")
    @ResponseBody
    public Object insertGoods(@PathVariable("id") Long shopId, @RequestBody GoodsVo goodsVo)
    {
        goodsVo.setShopId(shopId);
        ReturnObject returnObject = goodsService.insertGoods(goodsVo);

        return Common.decorateReturnObject(returnObject);

    }

    @PutMapping("/shops/{shopId}/goods/{id}")
    @ResponseBody
    public Object updateGoods(@PathVariable("id") Long id,@RequestBody GoodsVo goodsVo)
    {
        return Common.decorateReturnObject(goodsService.updateGoods(id,goodsVo));

    }

    @GetMapping(value="/shops/{shopId}/goods/{id}" )
    @ResponseBody
    public Object searchGoods(@PathVariable("id") Long id)
    {
        return Common.decorateReturnObject(goodsService.searchById(id));
    }

    @DeleteMapping("/shops/{shopId}/goods/{id}")
    @ResponseBody
    public Object deleteGoods(@PathVariable("id") Long id)
    {
        return Common.decorateReturnObject(goodsService.deleteGoods(id));
    }


    @PutMapping(value="/shops/{shopId}/products/{id}/publish")
    @ResponseBody
    public Object publishProduct(@PathVariable("id") Long id)
    {
        ReturnObject returnObject = productService.pulishProduct(id);
        return Common.decorateReturnObject(returnObject);
    }

    @PutMapping(value="/shops/{shopId}/products/{id}/onshelves")
    @ResponseBody
    public Object onshelvesProduct(@PathVariable("id") Long id)
    {
        return Common.decorateReturnObject(productService.onshelvesProduct(id));
    }

    @PutMapping(value="/shops/{shopId}/products/{id}/offshelves")
    @ResponseBody
    public Object offshelvesProduct(@PathVariable("id") Long id)
    {
        return Common.decorateReturnObject(productService.offshelvesProduct(id));
    }

    @PutMapping(value="/shops/{shopId}/products/{id}/allow")
    @ResponseBody
    public Object allowProduct(@PathVariable("id") Long id)
    {
        return Common.decorateReturnObject(productService.allowProduct(id));
    }

    @PutMapping(value="/shops/{shopId}/products/{id}/prohibit")
    @ResponseBody
    public Object prohibitProduct(@PathVariable("id") Long id)
    {
        return Common.decorateReturnObject(productService.prohibitProduct(id));
    }
}
