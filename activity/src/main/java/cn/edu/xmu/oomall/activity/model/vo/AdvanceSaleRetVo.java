package cn.edu.xmu.oomall.activity.model.vo;
import cn.edu.xmu.oomall.activity.microservice.vo.ProductVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class AdvanceSaleRetVo{
    @ApiModelProperty(value = "预售活动id")
    private Long id;

    @ApiModelProperty(value = "预售活动名称")
    private String name;

    @ApiModelProperty(value = "店铺")
    private ShopVo shop;

    @ApiModelProperty(value = "货品")
    private ProductVo product;

    @ApiModelProperty(value = "支付尾款时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime payTime;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime beginTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "价格")
    private Long price;

    @ApiModelProperty(value = "数量")
    private Long quantity;

    @ApiModelProperty(value = "订金")
    private Long advancePayPrice;

    @ApiModelProperty(value = "创建者")
    private SimpleAdminUserVo creator;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    @ApiModelProperty(value = "修改者")
    private SimpleAdminUserVo modifier;

    @ApiModelProperty(value = "状态")
    private Byte state;
}