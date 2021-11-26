package cn.edu.xmu.oomall.activity.model.vo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class SimpleAdvanceSaleRetVo{

    @ApiModelProperty(value = "活动id")
    private Long id;

    @ApiModelProperty(value = "活动名")
    private String name;
}