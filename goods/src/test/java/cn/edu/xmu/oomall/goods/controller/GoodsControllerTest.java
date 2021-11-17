package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.GoodsApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = GoodsApplication.class)
@WebAppConfiguration        //调用Java Web组件，如自动注入ServletContext Bean等
@Transactional      //防止脏数据
@AutoConfigureMockMvc
class GoodsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RedisUtil redisUtil;

    @Test
    public void insertGoodsTest()throws Exception {
        String json="{\"name\": \"小米\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/1/goods")
                .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

//        JSONAssert.assertEquals(expect, res,true);
    }

    @Test
    public void searchGoodsTest()throws Exception {
        Mockito.when(redisUtil.get("g_"+1L)).thenReturn(null);
        Mockito.when(redisUtil.get("g_"+5L)).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/goods/1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
    @Test
    public void updateGoodsTest() throws Exception {
        String json="{\"name\": \"小米\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/10/goods/1")
                .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    public void deleteGoodsTest() throws Exception {
        Mockito.when(redisUtil.get("g_"+1L)).thenReturn(null);
        Mockito.when(redisUtil.get("g_"+5L)).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/1/goods/1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    public void publishProductTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/products/5/publish")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    public void onshelvesProductTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/products/5/onshelves")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    public void offshelvesProductTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/products/10/offshelves")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    public void allowProductTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/products/10/allow")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    public void prohibitProductTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/products/5/prohibit")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

}