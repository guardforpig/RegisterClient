package cn.edu.xmu.oomall.comment.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentRetVo{
    @ApiModelProperty(value = "评论id")
    private Long id;

    private Long productId;

    private Byte type;

    private String content;

    private Byte state;
    private SimpleUserRetVo createdBy;
    private SimpleUserRetVo modifiedBy;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;

}
