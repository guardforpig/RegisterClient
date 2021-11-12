package cn.edu.xmu.oomall.goods.model.bo;

import cn.edu.xmu.oomall.goods.model.po.OnSalePo;
import cn.edu.xmu.oomall.goods.model.po.ProductDraftPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;

import java.util.HashMap;



public class OnSale implements VoObject{
    private OnSalePo onSalePo;
    public Long getId(){return onSalePo.getId();}
    public String getName(){return onSalePo.getCreateName();}
    public OnSale(ProductPo productPo)
    {
        onSalePo.setProductId(productPo.getId());
        onSalePo.setState(new Byte("2"));
    }
    public OnSalePo getOnSalePo(){return onSalePo;}

    @Override
    public HashMap<String,Object> createVo()
    {
        HashMap<String,Object> productInfo=new HashMap<>();
        productInfo.put("id",this.getId());
        productInfo.put("Createname",this.getName());
        return productInfo;
    }
}
