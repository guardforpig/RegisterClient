package cn.edu.xmu.oomall.comment.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleUserRetVo {
    @ApiModelProperty(value = "用户id")
    private Long id;
    @ApiModelProperty(value = "用户名")
    private String name;

    public SimpleUserRetVo(){
    }
}
