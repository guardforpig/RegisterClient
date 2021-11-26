package cn.edu.xmu.oomall.activity.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;



@Data
public class AdvanceSaleVo{
    @ApiModelProperty(value = "价格")
    @Min(0)
    @NotNull(message = "价格不能为空")
    private Long price;

    @ApiModelProperty(value = "预售活动名称")
    @NotBlank(message = "预售活动名称不能为空")
    private String name;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    @ApiModelProperty(value = "结束时间")
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "数量")
    @Min(0)
    @NotNull(message = "数量不能为空")
    private Long quantity;

    @ApiModelProperty(value = "支付尾款时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    @NotNull(message = "尾款支付时间不能为空")
    private LocalDateTime payTime;

    @ApiModelProperty(value = "订金")
    @DecimalMin("0")
    @Min(0)
    @NotNull(message = "订金不能为空")
    private Long advancePayPrice;
}