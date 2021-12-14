package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.goods.constant.Constants;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author yujie lin
 * @date 2021/11/10
 */
@Data
public class NewOnSaleVo {

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

    @Min(1)
    private Integer maxQuantity;

    @Min(1)
    private Integer numKey;

    public NewOnSaleVo(){}

}
