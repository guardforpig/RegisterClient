package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.goods.model.bo.VoObject;
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import cn.edu.xmu.oomall.goods.service.GoodsService;
import cn.edu.xmu.oomall.goods.service.ProductService;
import cn.edu.xmu.oomall.goods.util.Common;
import cn.edu.xmu.oomall.goods.util.ResponseCode;
import cn.edu.xmu.oomall.goods.util.ResponseUtil;
import cn.edu.xmu.oomall.goods.util.ReturnObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
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
        //System.out.println("Yes!");
        goodsVo.setShopId(shopId);
        return Common.decorateReturnObject(goodsService.insertGoods(goodsVo));

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
        return Common.decorateReturnObject(productService.pulishProduct(id));
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
