package cn.edu.xmu.oomall.coupon.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author:李智樑
 * @time:2021/12/10 10:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponActivityDetailRetVo {
    private Long id;
    private String name;
    private LocalDateTime couponTime;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Integer quantity;
    private Byte quantityType;
    private Byte validTerm;
    private String imageUrl;
    private Long numKey;
    private String strategy;
    private Byte state;
}
