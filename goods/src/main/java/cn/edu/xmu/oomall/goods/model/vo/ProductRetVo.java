package cn.edu.xmu.oomall.goods.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author 王言光 22920192204292
 * @date 2021/12/7
 */
@AllArgsConstructor
@NoArgsConstructor
public class ProductRetVo {
    private Long id;
    private SimpleObject shop;
    private Long goodsId;
    private Long onsaleId;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Long originalPrice;
    private Long weight;
    private Long price;
    private Integer quantity;
    private Byte state;
    private String unit;
    private String barCode;
    private String originPlace;
    private SimpleObject category;
    private Boolean shareable;
    private Long freightId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SimpleObject getShop() {
        return shop;
    }

    public void setShop(SimpleObject shop) {
        this.shop = shop;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getOnsaleId() {
        return onsaleId;
    }

    public void setOnsaleId(Long onsaleId) {
        this.onsaleId = onsaleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkuSn() {
        return skuSn;
    }

    public void setSkuSn(String skuSn) {
        this.skuSn = skuSn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Long originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getOriginPlace() {
        return originPlace;
    }

    public void setOriginPlace(String originPlace) {
        this.originPlace = originPlace;
    }

    public SimpleObject getCategory() {
        return category;
    }

    public void setCategory(SimpleObject category) {
        this.category = category;
    }

    public Boolean getShareable() {
        return shareable;
    }

    public void setShareable(Boolean shareable) {
        this.shareable = shareable;
    }

    public Long getFreightId() {
        return freightId;
    }

    public void setFreightId(Long freightId) {
        this.freightId = freightId;
    }
}
