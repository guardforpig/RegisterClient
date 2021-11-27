package cn.edu.xmu.oomall.activity.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 调用接口需要用的vo
 * @author xiuchen lang 22920192204222
 * @date 2021/11/13 14:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopInfoVo{
    private Long id;
    private String name;
}
