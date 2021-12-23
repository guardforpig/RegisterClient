package cn.edu.xmu.oomall.activity.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gao Yanfeng
 * @date 2021/12/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupOnOnsalePostVo {
    private Long price;
    private Integer quantity;
    private Integer numKey;
    private Integer maxQuantity;
}