package cn.edu.xmu.oomall.coupon.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnsaleVo {

    @Data
    class ShareAct {
        private Long id;
        private String name;
    }

    @Data
    class CreatedBy {
        private Long id;
        private String username;
    }

    @Data
    class ModifiedBy {
        private Long id;
        private String username;
    }

    private Long id;

    private ShopVo shop;

    private ProductVo product;

    private Integer price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime beginTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime endTime;

    private Integer quantity;

    private Integer type;

    private ShareAct shareAct;

    private CreatedBy createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime gmtCreate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime gmtModified;

    private ModifiedBy modifiedBy;
}