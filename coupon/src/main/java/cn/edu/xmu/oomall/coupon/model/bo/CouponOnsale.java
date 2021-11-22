package cn.edu.xmu.oomall.coupon.model.bo;


import cn.edu.xmu.oomall.core.model.VoObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponOnsale implements VoObject, Serializable {

    private Long id;

    private Long activityId;

    private Long onsaleId;

    private Long createdBy;

    private String createName;

    private Long modifiedBy;

    private String modiName;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;


    /**
     * 创建Vo对象
     *
     * @return Vo对象
     */
    @Override
    public Object createVo() {
        return null;
    }

    /**
     * 创建简单Vo对象
     *
     * @return 简单Vo对象
     */
    @Override
    public Object createSimpleVo() {
        return null;
    }
}
