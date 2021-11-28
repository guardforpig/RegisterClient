package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.goods.constant.Constants;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleShareActBo;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleShopBo;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class OnSaleRetVo{
    private Long id;
    private Long price;
    private Integer quantity;
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = "GMT+8")
    private LocalDateTime beginTime;
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = "GMT+8")
    private LocalDateTime endTime;
    private Byte type;
    private Long activityId;
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = "GMT+8")
    private LocalDateTime gmtCreate;
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = "GMT+8")
    private LocalDateTime gmtModified;

    private SimpleProductRetVo product;
    private SimpleShopBo shop;
    private SimpleShareActBo shareAct;
    private SimpleAdminUserBo createdBy;
    private SimpleAdminUserBo modifiedBy;
}
