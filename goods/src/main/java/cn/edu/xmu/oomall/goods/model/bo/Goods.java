package cn.edu.xmu.oomall.goods.model.bo;

import cn.edu.xmu.oomall.goods.model.po.GoodsPo;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@ToString
public class Goods implements VoObject{
    private GoodsPo goodsPo;
    private List<Product> productList;
    public Goods()
    {
        this.goodsPo=new GoodsPo();
    }
    public void setGoods(Goods goods){
        this.setName(goods.getName());
    }
    public Goods(GoodsPo goodsPo)
    {
        this.goodsPo=goodsPo;
    }
    public GoodsPo getGoodsPo(){
        return goodsPo;
    }
    public void setGoodsPo(GoodsPo goodsPo)
    {
        this.goodsPo=goodsPo;
    }
    public void setName(String name)
    {
        goodsPo.setName(name);
    }
    public void setShopId(Long shopId)
    {
        goodsPo.setShopId(shopId);
    }
    public String getName()
    {
        return goodsPo.getName();
    }
    public Long getShopId()
    {
        return goodsPo.getShopId();
    }
    public Long getId(){return goodsPo.getId();}
    public Long getCreatedBy(){return goodsPo.getCreatedBy();}
    public LocalDateTime getGmtCreate(){return goodsPo.getGmtCreate();}
    public LocalDateTime getGmtModified(){return goodsPo.getGmtModified();}
    public Long getModifiedBy(){return goodsPo.getModifiedBy();}
    public void setProductList(List<Product> productList)
    {
        this.productList=productList;
    }

    @Override
    public Object createVo()
    {
        ArrayList<HashMap<String,Object>> productInfos=new ArrayList<>();
        for(Product product:this.productList)
            productInfos.add(product.createVo());
        HashMap<String,Object> retGoodsInfo=new HashMap<>();
        retGoodsInfo.put("id",goodsPo.getId());
        retGoodsInfo.put("name",goodsPo.getName());
        retGoodsInfo.put("products",productInfos);
        retGoodsInfo.put("createdBy",goodsPo.getCreatedBy());
        retGoodsInfo.put("gmtCreate",goodsPo.getGmtCreate());
        retGoodsInfo.put("gmtModified",goodsPo.getGmtModified());
        retGoodsInfo.put("modifiedBy",goodsPo.getModifiedBy());
        retGoodsInfo.put("shop_id",goodsPo.getShopId());
        return retGoodsInfo;
    }
}
