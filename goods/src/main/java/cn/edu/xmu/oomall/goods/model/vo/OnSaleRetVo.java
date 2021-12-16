package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.goods.constant.Constants;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleShopVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime beginTime;
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime endTime;
    private Byte type;
    private Long activityId;
    private Long shareActId;
    private Integer numKey;
    private Integer maxQuantity;
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime gmtCreate;
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime gmtModified;
    private Byte state;

    private SimpleProductRetVo product;
    private SimpleShopVo shop;
    private SimpleAdminUserBo creator;
    private SimpleAdminUserBo modifier;
}
