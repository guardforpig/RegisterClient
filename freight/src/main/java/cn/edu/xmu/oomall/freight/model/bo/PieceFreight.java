package cn.edu.xmu.oomall.freight.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Gao Yanfeng
 * @date 2021/11/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PieceFreight extends FreightItem implements Serializable {
    private Long id;
    private Long freightModelId;
    private Long regionId;
    private Integer firstItems;
    private Long firstItemFreight;
    private Integer additionalItems;
    private Long additionalItemsPrice;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long createdBy;
    private String createName;
    private Long modifiedBy;
    private String modiName;

    @Override
    public Long calculate(Integer quantity, Integer unit) {
        return firstItemFreight + calculatePart(firstItems, null, quantity, unit, additionalItemsPrice);
    }
}