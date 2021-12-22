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
    String name;
    Long originalPrice;
    Long categoryId;
    Long weight;
    String barCode;
    String unit;
    String originPlace;
}
