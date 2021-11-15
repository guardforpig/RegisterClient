package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.goods.model.bo.Goods;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
/**
 * @author Huang Tianyue
 * 2021.11.15
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description="商品视图对象")
public class GoodsVo {
    @ApiModelProperty(value="商品名称")
    private String name;
    @ApiModelProperty(value="店铺id")
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
