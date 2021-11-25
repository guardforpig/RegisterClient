package cn.edu.xmu.oomall.goods.model.bo;

import cn.edu.xmu.oomall.core.model.VoObject;
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
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
@Data
@Getter
@Setter
@NoArgsConstructor
public class Product implements Serializable {
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

    private Long creatorId;

    private String creatorName;


    private Long modifierId;


    private String modifierName;

    private LocalDateTime gmtCreate;


    private LocalDateTime gmtModified;


    private Byte state;


    public enum ProductState
    {
        /**
         * 共四种状态
         */
        WAIT_FOR_AUDIT(1,"待审核"),
        OFFSHELF(2,"下架"),
        ONSHELF(3,"上架"),
        BANNED(4,"禁售中");
        private int code;
        private String state;
        ProductState(int code, String state) {
            this.code=code;
            this.state=state;
        }
        public int getCode(){
            return code;
        }
    }

}
