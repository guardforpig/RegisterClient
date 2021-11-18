package cn.edu.xmu.oomall.comment.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
/**
 * 评论
 * @author Xinyu Jiang
 * @sn 22920192204219
 **/
@Data
public class CommentSelectRetVo {
    private Long page;
    private Long pageSize;
    private Long total;
    private Long pages;
    private List<Object> list=new ArrayList<>();
}
