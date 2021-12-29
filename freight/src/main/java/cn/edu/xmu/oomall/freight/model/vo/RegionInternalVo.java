package cn.edu.xmu.oomall.freight.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegionInternalVo {
    private Long id;
    private Long pid;
    private String name;
    private Byte state;
}