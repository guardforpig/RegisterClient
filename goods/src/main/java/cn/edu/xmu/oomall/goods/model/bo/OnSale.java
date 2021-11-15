package cn.edu.xmu.oomall.goods.model.bo;

import cn.edu.xmu.oomall.goods.model.po.OnSalePo;
import cn.edu.xmu.oomall.goods.model.po.ProductDraftPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;


@Data
@NoArgsConstructor
public class OnSale implements Serializable {
    private OnSalePo onSalePo;
    public Long getId(){return onSalePo.getId();}
    public String getName(){return onSalePo.getCreateName();}
    public OnSale(ProductPo productPo)
    {
        onSalePo.setProductId(productPo.getId());
        onSalePo.setState(new Byte("2"));
    }
    public OnSalePo getOnSalePo(){return onSalePo;}

}
