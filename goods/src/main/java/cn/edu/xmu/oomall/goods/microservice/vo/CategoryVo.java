package cn.edu.xmu.oomall.goods.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime gmtCreate;
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
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