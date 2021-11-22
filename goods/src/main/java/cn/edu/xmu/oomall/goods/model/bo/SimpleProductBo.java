package cn.edu.xmu.oomall.goods.model.bo;

import cn.edu.xmu.oomall.core.model.VoObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/11 02:20
 **/
@Data
@NoArgsConstructor
public class SimpleProductBo implements VoObject, Serializable {
    private Long id;
    private String name;
    private String imageUrl;

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
