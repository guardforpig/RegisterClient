package cn.edu.xmu.oomall.goods.model.vo;

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
    private Map<String,Object> shop;
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
    private Map<String,Object> category;
    private Map<String,Object> createBy;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Map<String,Object> modifiedBy;
}
