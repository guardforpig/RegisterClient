package cn.edu.xmu.oomall.goods.model.vo;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author wyg
 * @Date 2021/11/13
 */
@Data
@ApiModel(value = "goods返回视图")
@AllArgsConstructor
@NoArgsConstructor
public class GoodsRetVo {

    private Long id;
    private String name;
    private List<SimpleProductRetVo> productList;

}
