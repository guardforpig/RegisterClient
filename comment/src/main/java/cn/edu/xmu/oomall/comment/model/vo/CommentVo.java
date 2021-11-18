package cn.edu.xmu.oomall.comment.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "评论视图")
public class CommentVo {

    @Max(2)
    @Min(0)
    @ApiModelProperty(value = "评论类型")
    Long type;
    @NotBlank(message = "评论不能为空")
    @ApiModelProperty(value = "评论内容")
    String content;
    @ApiModelProperty(value = "商铺id")
    Long shopId;
}
