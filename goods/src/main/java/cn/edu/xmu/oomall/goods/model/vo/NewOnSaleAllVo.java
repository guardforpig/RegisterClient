package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.goods.constant.Constants;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author YuJie 22920192204242
 * @date 2021/11/29
 */
@Data
public class NewOnSaleAllVo {

    @Min(0)
    private Long price;

    @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime endTime;

    @Min(1)
    private Integer quantity;

    @NotNull
    private Byte type;

    private Long activityId;

    public NewOnSaleAllVo(){}
}
