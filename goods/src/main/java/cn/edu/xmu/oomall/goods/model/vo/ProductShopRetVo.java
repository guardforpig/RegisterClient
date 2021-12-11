package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.goods.constant.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author 王言光 22920192204292
 * @date 2021/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductShopRetVo {
    private Long id;
    private SimpleObject shop;
    private Long goodsId;
    private Long onSaleId;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Long originalPrice;
    private Long weight;
    private Byte state;
    private String unit;
    private String barCode;
    private String originPlace;
    private SimpleObject category;
    private SimpleObject createBy;
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = "GMT+8")
    private LocalDateTime gmtCreate;
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = "GMT+8")
    private LocalDateTime gmtModified;
    private SimpleObject modifiedBy;
}
