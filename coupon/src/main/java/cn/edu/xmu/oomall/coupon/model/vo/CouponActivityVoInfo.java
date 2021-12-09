package cn.edu.xmu.oomall.coupon.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.coupon.model.bo.CouponActivity;
import cn.edu.xmu.oomall.coupon.model.po.CouponActivityPo;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author RenJieZheng 22920192204334
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CouponActivityVoInfo implements VoObject {
    private Long id;
    private String name;
    private SimpleShopRetVo shop;
    private LocalDateTime couponTime;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Integer quantity;
    private Byte quantityType;
    private Byte validTerm;
    private String imageUrl;
    private String strategy;
    private Byte state;
    private SimpleUserRetVo createBy;
    private SimpleUserRetVo modifiedBy;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public CouponActivityVoInfo(CouponActivity couponActivity) {
        this.id = couponActivity.getId();
        this.name = couponActivity.getName();
        this.beginTime = couponActivity.getBeginTime();
        this.endTime = couponActivity.getEndTime();
        this.couponTime = couponActivity.getCouponTime();
        this.state = couponActivity.getState();
        this.shop = new SimpleShopRetVo(couponActivity.getShopId(),couponActivity.getShopName());
        this.quantity = couponActivity.getQuantity();
        this.validTerm = couponActivity.getValidTerm();
        this.imageUrl = couponActivity.getImageUrl();
        this.strategy = couponActivity.getStrategy();
        this.gmtCreate = couponActivity.getGmtCreate();
        this.gmtModified = couponActivity.getGmtModified();
        this.quantityType = couponActivity.getQuantityType();
        this.createBy = new SimpleUserRetVo(couponActivity.getCreatorId(),couponActivity.getCreatorName());
        this.modifiedBy = new SimpleUserRetVo(couponActivity.getModifierId(),couponActivity.getModifierName());
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
