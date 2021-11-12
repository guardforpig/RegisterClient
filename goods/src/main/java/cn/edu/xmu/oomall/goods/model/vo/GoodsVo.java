package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.goods.model.bo.Goods;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel(description="商品视图对象")
public class GoodsVo {
    private String name;
    private Long shopId;
    public void setShopId(Long shopId)
    {
        this.shopId=shopId;
    }
    public Goods createGoods()
    {
        Goods goods=new Goods();
        goods.setName(this.name);
        goods.setShopId(this.shopId);
        return goods;
    }
}
