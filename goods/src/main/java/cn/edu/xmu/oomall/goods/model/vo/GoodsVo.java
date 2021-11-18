package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.goods.model.bo.Product;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
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
    @NotBlank(message="商品名称不能为空")
    private String name;
    private List<Product> productList;

}
