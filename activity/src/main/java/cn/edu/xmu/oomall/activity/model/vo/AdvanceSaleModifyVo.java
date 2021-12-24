package cn.edu.xmu.oomall.activity.model.vo;

import cn.edu.xmu.oomall.activity.model.po.AdvanceSalePo;
import cn.edu.xmu.oomall.activity.util.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;


@Data
public class AdvanceSaleModifyVo {

    @ApiModelProperty(value = "活动名")
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime beginTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime endTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime payTime;
    @ApiModelProperty(value = "首款金额")
    private Long advancePayPrice;
    @ApiModelProperty(value = "数量")
    private Integer quantity;
    @ApiModelProperty(value = "价格")
    private Long price;

    public AdvanceSaleModifyVo() {
    }
}
