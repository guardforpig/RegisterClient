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
    public void setState(Product.State state) {
        Integer code=state.getCode();
        Byte b=code.byteValue();
        this.state=b;
    }
    public enum Type {
        NOACTIVITY(0, "无活动"),
        SECKILL(1, "秒杀"),
        GROUPON(2, "团购"),
        PRESALE(3, "预售");


        private static final Map<Integer, Product.Type> TYPE_MAP;

        static {
            TYPE_MAP = new HashMap();
            for (Product.Type enum1 : values()) {
                TYPE_MAP.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        Type(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Product.Type getTypeByCode(Integer code) {
            return TYPE_MAP.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

    }
    public enum State {
        DRAFT(0, "草稿"),
        ONLINE(1, "上线"),
        OFFLINE(2, "下线"),
        PROHIBIT(3,"禁售");


        private static final Map<Integer, Product.State> STATE_MAP;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            STATE_MAP = new HashMap();
            for (Product.State enum1 : values()) {
                STATE_MAP.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code=code;
            this.description=description;
        }

        public static Product.State getStatusByCode(Integer code){
            return STATE_MAP.get(code);
        }

        public Integer getCode(){
            return code;
        }

        public String getDescription() {return description;}

    }

}
