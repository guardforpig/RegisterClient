package cn.edu.xmu.oomall.coupon.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author GXC
 */
@Data
@NoArgsConstructor
public class CouponInternalRetVo {
    private Long id;
    private String name;
    private String imageUrl;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime endTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime couponTime;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 数量的种类：0每人数量，1总数控制
     */
    private Byte quantityType;
    /**
     * 时长，单位天
     */
    private Byte validTerm;
    /**
     * 活动状态，012草稿上线下线
     */
    private Byte state;
}
