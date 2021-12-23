package cn.edu.xmu.oomall.activity.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class OnsaleModifyVo {

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
