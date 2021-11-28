package cn.edu.xmu.oomall.goods.model.vo;

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
public class SimpleProductRetVo {
    private Long id;
    private String name;
    private String imageUrl;
}
