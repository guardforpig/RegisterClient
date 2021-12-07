package cn.edu.xmu.oomall.goods.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author 王言光 22920192204292
 * @date 2021/12/7
 */
@Data
@ApiModel(value = "修改Product视图")
public class ProductChangeVo {
    String skuSn;
    @NotBlank
    @NotNull
    String name;
    @Min(0)
    Long originalPrice;
    Long categoryId;
    @Min(0)
    Long weight;
    @Min(0)
    String barCode;
    String unit;
    String originPlace;
}
