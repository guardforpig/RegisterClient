package cn.edu.xmu.oomall.goods.model.bo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static cn.edu.xmu.oomall.core.util.Common.cloneVo;

/**
 * @author Huang Tianyue
 * 2021.11.15
 **/
@Data
@Getter
@Setter
@NoArgsConstructor
public class Goods implements VoObject,Serializable {
    private Long id;
    private String name;
    private List<Product> productList;
    private Long createdBy;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long modifiedBy;
    @Override
    public GoodsVo createVo() {
        return (GoodsVo)cloneVo(this,GoodsVo.class);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
