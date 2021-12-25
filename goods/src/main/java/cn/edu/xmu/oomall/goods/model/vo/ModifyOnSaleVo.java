package cn.edu.xmu.oomall.goods.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import java.time.ZonedDateTime;

/**
 * @author YuJie 22920192204242
 * @date 2021/11/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModifyOnSaleVo {



    private Long price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime beginTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime endTime;

    private Integer quantity;

    private Integer maxQuantity;

    private Integer numKey;

    private Long shareActId;

}
