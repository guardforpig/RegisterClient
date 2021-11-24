package cn.edu.xmu.oomall.core.util;


import cn.edu.xmu.oomall.core.util.bo.Category;
import cn.edu.xmu.oomall.core.util.bo.CategoryRetVo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CloneVoTest {

    /**
     * @author xucangbai
     * @date 2021/11/13
     */
    @Test
    void test() {
        Category categoryBo=new Category();
        categoryBo.setId(1L);
        categoryBo.setCommissionRatio(1);
        categoryBo.setCreatorId(2L);
        categoryBo.setCreatorName("CreateName");
        categoryBo.setModifierId(3L);
        categoryBo.setModifierName("ModiName");
        LocalDateTime gmtCreate=LocalDateTime.now().minusDays(1);
        LocalDateTime gmtModified=LocalDateTime.now();
        categoryBo.setGmtCreate(gmtCreate);
        categoryBo.setGmtModified(gmtModified);
        categoryBo.setPid(2L);
        categoryBo.setName("name");


        CategoryRetVo categoryRetVo = (CategoryRetVo) Common.cloneVo(categoryBo, CategoryRetVo.class);
        assertEquals(categoryRetVo.getId(),1L);
        assertEquals(categoryRetVo.getName(),"name");

        assertEquals(categoryRetVo.getCreator().getId(),2L);
        assertEquals(categoryRetVo.getCreator().getName(),"CreateName");
        assertEquals(categoryRetVo.getModifier().getId(),3L);
        assertEquals(categoryRetVo.getModifier().getName(),"ModiName");
        assertEquals(categoryRetVo.getGmtCreate(),gmtCreate);
        assertEquals(categoryRetVo.getGmtModified(),gmtModified);
    }
}