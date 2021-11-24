package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Zijun Min
 * @description
 * @createTime 2021/11/24 15:21
 **/
@Data
@NoArgsConstructor
public class SimpleOnSaleRetVo implements VoObject {
    private Long id;

    private Long price;

    @JsonFormat(shape = JsonFormat.Shape.STRING ,pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime beginTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING ,pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime endTime;

    private Integer quantity;

    private Long activityId;

    private Long shareActId;

    private Byte type;

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
