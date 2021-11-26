package cn.edu.xmu.oomall.activity.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleOnSaleInfoVo {
    @ApiModelProperty(value = "销售id")
    private Long onsaleId;

    @ApiModelProperty(value = "价格")
    private Long price;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime beginTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "数量")
    private Long quantity;

    @ApiModelProperty(value = "活动id")
    private Long activityId;

    @ApiModelProperty(value = "类型")
    private String type;
}
