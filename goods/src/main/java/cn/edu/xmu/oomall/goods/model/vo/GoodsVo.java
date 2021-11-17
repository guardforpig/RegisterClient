package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.goods.model.bo.Product;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private Long id;
    private String name;
    private Long shopId;
    private List<Product> productList;
    private Long createdBy;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long modifiedBy;

}
