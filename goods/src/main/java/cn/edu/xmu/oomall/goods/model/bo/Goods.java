package cn.edu.xmu.oomall.goods.model.bo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static cn.edu.xmu.oomall.core.util.Common.cloneVo;

/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
@Data
@Getter
@Setter
@NoArgsConstructor
public class Goods implements Serializable {
    private Long id;
    private String name;
    private Long shopId;
    private List<Product> productList;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long modifierId;
    private String modifierName;
}
