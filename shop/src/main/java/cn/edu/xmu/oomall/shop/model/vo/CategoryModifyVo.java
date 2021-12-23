package cn.edu.xmu.oomall.shop.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author:李智樑
 * @time:2021/12/22 11:18
 **/
@Data
@NoArgsConstructor
public class CategoryModifyVo {
    @ApiModelProperty(value = "用户名")
    private String name;
    @ApiModelProperty(value = "佣金率")
    private Integer commissionRatio;
}
