package cn.edu.xmu.oomall.comment.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentSelectRetVo {
    private Long page;
    private Long pageSize;
    private Long total;
    private Long pages;
    private List<Object> list=new ArrayList<>();
}
