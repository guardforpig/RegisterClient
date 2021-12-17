package cn.edu.xmu.oomall.goods.microservice.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtCreate;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtModified;
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