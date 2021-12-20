package cn.edu.xmu.oomall.activity.model.vo;

import cn.edu.xmu.oomall.activity.constant.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Gao Yanfeng
 * @date 2021/11/11
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "创建团购活动时的提交信息")
public class GroupOnActivityPostVo  {
    @Length(min = 1, max = 128)
    @NotNull
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime beginTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime endTime;

    private List<@Valid GroupOnStrategyVo> strategy;
}
