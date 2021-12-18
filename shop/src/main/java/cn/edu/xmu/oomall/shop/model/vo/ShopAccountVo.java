package cn.edu.xmu.oomall.shop.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.shop.model.po.ShopAccountPo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author  Xusheng Wang
 * @date  2021-11-11
 * @studentId 34520192201587
 */

@Data
@NoArgsConstructor
public class ShopAccountVo{
    @NotNull
    private long id;

    @ApiModelProperty(value = "支付渠道")
    private Byte type;

    @ApiModelProperty(value = "账户号")
    private String account;

    @ApiModelProperty(value = "账户名称")
    private String name;

    @ApiModelProperty(value = "汇入优先级")
    private Byte priority;


    private SimpleAdminUserVo creator;
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime gmtCreate;
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime gmtModified;
    private SimpleAdminUserVo modifier;
}
