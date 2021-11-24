package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.oomall.shop.ShopApplication;
import cn.edu.xmu.privilegegateway.util.JwtHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 商品分类测试类
 *
 * @author Zhiliang Li
 * @date 2021/11/22
 */
@SpringBootTest(classes = ShopApplication.class)
@AutoConfigureMockMvc
public class CategoryControllerTest {
    private static String adminToken;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    private static void login() {
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);
    }

    @Test
    @Transactional
    public void addCategory() throws Exception {
        // 命名重复
        String requestJson = "{\"name\": \"女装男装\"}";
        String responseString = this.mvc.perform(post("/shops/0/categories/0/subcategories")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":901,\"errmsg\":\"类目名称已存在\"}", responseString);

        // 找不到
        requestJson = "{\"name\": \"女装男装\"}";
        responseString = this.mvc.perform(post("/shops/0/categories/500/subcategories")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString);

        // 成功插入，二级目录
        requestJson = "{\"name\": \"童装a\"}";
        responseString = this.mvc.perform(post("/shops/0/categories/1/subcategories")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"commissionRatio\":null,\"name\":\"童装a\"}}", responseString, false);

        // 成功插入，一级目录
        requestJson = "{\"name\": \"军械\"}";
        responseString = this.mvc.perform(post("/shops/0/categories/0/subcategories")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"commissionRatio\":null,\"name\":\"军械\"}}", responseString, false);

        // 不能插入成为三级目录
        requestJson = "{\"name\": \"手机游戏\"}";
        responseString = this.mvc.perform(post("/shops/0/categories/277/subcategories")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":967,\"errmsg\":\"不允许增加新的下级分类\"}", responseString);

        // 传参错误
        requestJson = "{\"name\": \"\"}";
        responseString = this.mvc.perform(post("/shops/0/categories/277/subcategories")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":503,\"errmsg\":\"分类名不能为空;\"}", responseString);

        // 服务器错误
        requestJson = "{\"name\":\"conditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditioncondition\"}";
        responseString = this.mvc.perform(post("/shops/0/categories/0/subcategories")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken)
                        .content(requestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":500}", responseString, false);

        // id小于0
        requestJson = "{\"name\": \"t1\"}";
        responseString = this.mvc.perform(post("/shops/0/categories/-1/subcategories")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString);
    }

    @Test
    @Transactional
    public void getCategory() throws Exception {
        String responseString;

        // 有子分类
        responseString = this.mvc.perform(get("/categories/1/subcategories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"code\":\"OK\",\"errmsg\":\"成功\"}", responseString, false);

        // 找不到
        responseString = this.mvc.perform(get("/categories/500/subcategories"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString);

        // 无子分类
        responseString = this.mvc.perform(get("/categories/277/subcategories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"code\":\"OK\",\"errmsg\":\"成功\"}", responseString, false);

        // 尝试查所有一级分类
        responseString = this.mvc.perform(get("/categories/0/subcategories"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString);

        // 查所有单独分类
        responseString = this.mvc.perform(get("/orphoncategories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"code\":\"OK\",\"errmsg\":\"成功\"}", responseString, false);
    }

    @Test
    @Transactional
    public void modifyCategory() throws Exception {
        // 可以修改
        String requestJson = "{\"name\": \"test\"}";
        String responseString = this.mvc.perform(put("/shops/0/categories/313")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", responseString);

        // 重名
        requestJson = "{\"name\": \"童装\"}";
        responseString = this.mvc.perform(put("/shops/0/categories/313")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":901,\"errmsg\":\"类目名称已存在\"}", responseString);

        // 找不到资源
        responseString = this.mvc.perform(put("/shops/0/categories/500")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString);

        // 传参错误
        requestJson = "{\"name\": \"\"}";
        responseString = this.mvc.perform(put("/shops/0/categories/313")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":503,\"errmsg\":\"分类名不能为空;\"}", responseString);

        // 服务器错误
        requestJson = "{\"name\":\"conditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditioncondition\"}";
        responseString = this.mvc.perform(put("/shops/0/categories/313")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken)
                        .content(requestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":500}", responseString, false);

    }

    @Test
    @Transactional
    public void deleteCategory() throws Exception {
        // 删有子类别的
        String responseString = this.mvc.perform(delete("/shops/0/categories/1")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", responseString);

        // 删单独的
        responseString = this.mvc.perform(delete("/shops/0/categories/277")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", responseString);

        // 找不到
        responseString = this.mvc.perform(delete("/shops/0/categories/500")
                        .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString);

        // 删为0或-1的
        responseString = this.mvc.perform(delete("/shops/0/categories/0")
                        .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString);

    }
}
