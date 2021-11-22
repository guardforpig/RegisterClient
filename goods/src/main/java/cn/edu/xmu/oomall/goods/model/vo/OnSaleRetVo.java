package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleShareActBo;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleShopBo;
import cn.edu.xmu.oomall.goods.model.bo.SimpleProductBo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/11 02:57
 **/
@Data
@NoArgsConstructor
public class OnSaleRetVo implements VoObject {
    private Long id;
    private Long price;
    private Integer quantity;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Byte type;
    private Long activityId;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    private SimpleProductBo product;
    private SimpleShopBo shop;
    private SimpleShareActBo shareAct;
    private SimpleAdminUserBo createdBy;
    private SimpleAdminUserBo modifiedBy;

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
