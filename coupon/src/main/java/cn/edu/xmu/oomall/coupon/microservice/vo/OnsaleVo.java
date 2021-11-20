package cn.edu.xmu.oomall.coupon.microservice.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import lombok.Data;

@Data
public class OnsaleVo implements VoObject {

    private Long id;

    private Long shopId;

    private Long productId;

    /**
     * 创建Vo对象
     *
     * @return Vo对象
     */
    @Override
    public Object createVo() {
        return this;
    }

    /**
     * 创建简单Vo对象
     *
     * @return 简单Vo对象
     */
    @Override
    public Object createSimpleVo() {
        return this;
    }
}
