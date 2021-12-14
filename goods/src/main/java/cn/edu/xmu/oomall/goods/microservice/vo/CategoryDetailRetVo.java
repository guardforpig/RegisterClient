package cn.edu.xmu.oomall.goods.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author:李智樑
 * @time:2021/12/14 10:01
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDetailRetVo {
    private Long id;
    private String name;
    private Integer commissionRatio;
    private Long pid;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime gmtCreate;
    private Long modifierId;
    private LocalDateTime gmtModified;
    private String modifierName;
}
