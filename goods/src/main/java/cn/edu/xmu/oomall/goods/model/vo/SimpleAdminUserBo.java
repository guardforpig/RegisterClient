package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/11 02:59
 **/
@Data
@NoArgsConstructor
public class SimpleAdminUserBo implements VoObject {
    private Long id;
    private String userName;

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
