package cn.edu.xmu.oomall.freight.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
/**
 * @author Yitong  Gao
 */
@Data
@NoArgsConstructor
public class PieceFreightVo {
    @ApiModelProperty(value = "抵达地区码")
    @Min(0)
    private Long regionId;
    @Min(0)
    @ApiModelProperty(value = "首件数")
    private Integer firstItems;
    @ApiModelProperty(value = "首费")
    @Min(0)
    private Long firstItemFreight;
    @ApiModelProperty(value = "续件数")
    @Min(0)
    private Integer additionalItems;
    @ApiModelProperty(value = "续费")
    @Min(0)
    private Long additionalItemsPrice;
}
