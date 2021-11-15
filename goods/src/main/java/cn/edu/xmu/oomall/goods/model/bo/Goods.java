package cn.edu.xmu.oomall.goods.model.bo;

import cn.edu.xmu.oomall.goods.model.po.GoodsPo;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
public class Goods implements Serializable {
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
        this.goodsPo.setName(name);
    }
    public void setShopId(Long shopId)
    {
        this.goodsPo.setShopId(shopId);
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


}
