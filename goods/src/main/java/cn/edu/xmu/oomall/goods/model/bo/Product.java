package cn.edu.xmu.oomall.goods.model.bo;

import cn.edu.xmu.oomall.goods.model.po.ProductDraftPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;

import java.util.HashMap;

public class Product implements VoObject{
    private ProductPo productPo;
    public Product(){this.productPo=new ProductPo();}
    public Product(ProductPo productPo){
        this.productPo=productPo;
    }
    public ProductPo getProductPo(){return productPo;}
    public Long getId(){return productPo.getId();}
    public String getName(){return productPo.getName();}
    public String getImageUrl(){return productPo.getImageUrl();}
    public Product(ProductDraftPo productDraftPo)
    {
        this.productPo.setShopId(productDraftPo.getShopId());
        this.productPo.setGoodsId(productDraftPo.getGoodsId());
        this.productPo.setCategoryId(productDraftPo.getCategoryId());
        this.productPo.setFreightId(productDraftPo.getFreightId());
        this.productPo.setSkuSn(productDraftPo.getSkuSn());
        this.productPo.setName(productDraftPo.getName());
        this.productPo.setOriginalPrice(productDraftPo.getOriginalPrice());
        this.productPo.setWeight(productDraftPo.getWeight());
        this.productPo.setImageUrl(productDraftPo.getImageUrl());
        this.productPo.setBarcode(productDraftPo.getBarcode());
        this.productPo.setUnit(productDraftPo.getUnit());
        this.productPo.setOriginPlace(productDraftPo.getOriginPlace());
    }
    @Override
    public HashMap<String,Object> createVo()
    {
        HashMap<String,Object> productInfo=new HashMap<>();
        productInfo.put("id",this.getId());
        productInfo.put("name",this.getName());
        productInfo.put("imageUrl",this.getImageUrl());
        return productInfo;
    }
}
