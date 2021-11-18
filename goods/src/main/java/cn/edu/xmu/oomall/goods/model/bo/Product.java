package cn.edu.xmu.oomall.goods.model.bo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import cn.edu.xmu.oomall.goods.model.vo.ProductVo;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static cn.edu.xmu.oomall.core.util.Common.cloneVo;

/**
 * @author YuJie 22920192204242
 * @date 2021/11/14
 */
@Data
@Getter
@Setter
@NoArgsConstructor
public class Product implements VoObject,Serializable {
    private Long id;


    private Long shopId;

    private String shopName;

    private Long goodsId;


    private Long categoryId;

    private Long freightId;

    private String skuSn;


    private String name;


    private Long originalPrice;


    private Long weight;


    private String imageUrl;

    private String barcode;


    private String unit;

    private String originPlace;

    private Long createdBy;

    private String createName;


    private Long modifiedBy;


    private String modiName;

    private LocalDateTime gmtCreate;


    private LocalDateTime gmtModified;


    private Byte state;
    @Override
    public ProductVo createVo() {
        return (ProductVo) cloneVo(this,ProductVo.class);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }



}
