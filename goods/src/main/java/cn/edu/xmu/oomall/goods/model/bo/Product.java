package cn.edu.xmu.oomall.goods.model.bo;

import cn.edu.xmu.oomall.goods.model.po.ProductDraftPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
/**
 * @author Huang Tianyue
 * 2021.11.15
 **/
@Data
@NoArgsConstructor
public class Product implements Serializable {
    private ProductPo productPo;
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
}
