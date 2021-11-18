package cn.edu.xmu.oomall.coupon.microservice.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import lombok.Data;

@Data
public class ProductVo implements VoObject {

    private Long id;

    private String name;

    private String imageUrl;

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
