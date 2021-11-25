package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.shop.model.vo.ShopVo;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description="onsaleRet视图对象")
public class OnSaleRetVo {
    private Long id;
    private ProductVo productVo;
    private Long price;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Integer quantity;
    private Byte type;
    private Long activityId;
    private Long shareActId;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
