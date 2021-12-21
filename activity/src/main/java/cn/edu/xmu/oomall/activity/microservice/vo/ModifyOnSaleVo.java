package cn.edu.xmu.oomall.activity.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import java.time.ZonedDateTime;

/**
 * @author YuJie 22920192204242
 * @date 2021/11/15
 */
@Data
public class ModifyOnSaleVo {


    @Min(0)
    private Long price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime beginTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime endTime;

    @Min(1)
    private Integer quantity;

    @Min(1)
    private Integer maxQuantity;

    @Min(1)
    private Integer numKey;

    private Long shareActId;


}
