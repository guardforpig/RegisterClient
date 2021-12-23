package cn.edu.xmu.oomall.activity.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/11/12 17:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareActivityVo {
    @NotBlank(message="预售活动名称不能为空")
    private String name;

    @NotNull(message="开始时间不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime beginTime;

    @NotNull(message="结束时间不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime endTime;

    @Valid
    private List<StrategyVo> strategy;
}
