package cn.edu.xmu.oomall.goods.microservice.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 何赟
 * @date 2021-12-5
 */
@Data
public class CategoryVo implements Serializable {

    private Long id;
    private Integer commissionRatio;
    private String name;
    private CreatorBean creator;
    private String gmtCreate;
    private String gmtModified;
    private ModifierBean modifier;

    @Data
    public static class CreatorBean implements Serializable {
        private Long id;
        private String name;
    }

    @Data
    public static class ModifierBean implements Serializable {
        private Long id;
        private String name;
    }
}