package cn.edu.xmu.oomall.activity.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * 管理员查询所有商品的价格浮动（2021-1-3） 返回的列表
 *
 * @author xiuchen lang 22920192204222
 * @date 2021/11/13 22:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleSaleInfoVo {
    Long id;
    Integer price;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime beginTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime endTime;
    Integer quantity;
    Long activityId;
    Byte type;
    Long shareActId;

}
