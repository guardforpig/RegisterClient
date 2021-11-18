package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.GoodsApplication;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import com.auth0.jwt.JWTCreator;
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
    private static String adminToken;
    private static JwtHelper jwtHelper = new JwtHelper();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RedisUtil redisUtil;

    @Test
    public void insertGoodsTest()throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        String json="{\"name\": \"小米\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/1/goods").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    }
    @Test
    public void insertGoodsTest2()throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        String json="{\"name\": \"小米\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/10/goods").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    }

    @Test
    public void searchGoodsTest()throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        Mockito.when(redisUtil.get("g_"+1L)).thenReturn(null);
        Mockito.when(redisUtil.get("g_"+5L)).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/goods/1").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
    @Test
    public void searchGoodsTest2()throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        Mockito.when(redisUtil.get("g_"+1L)).thenReturn(null);
        Mockito.when(redisUtil.get("g_"+5L)).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/10/goods/1").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
    @Test
    public void updateGoodsTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        String json="{\"name\": \"小米\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/10/goods/1").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
    @Test
    public void updateGoodsTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        String json="{\"name\": \"小米\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/5/goods/2").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    public void deleteGoodsTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        Mockito.when(redisUtil.get("g_"+1L)).thenReturn(null);
        Mockito.when(redisUtil.get("g_"+5L)).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/1/goods/1").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
    @Test
    public void deleteGoodsTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        Mockito.when(redisUtil.get("g_"+1L)).thenReturn(null);
        Mockito.when(redisUtil.get("g_"+5L)).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/10/goods/1").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    public void publishProductTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/9/products/5/publish").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    public void publishProductTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/10/products/1550/publish").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    public void onshelvesProductTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/products/5/onshelves").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
    @Test
    public void onshelvesProductTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/10/products/2/onshelves").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    public void offshelvesProductTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/products/10/offshelves").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
    @Test
    public void offshelvesProductTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/4/products/1552/offshelves").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    public void allowProductTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/products/10/allow").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
    @Test
    public void allowProductTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/10/products/1/allow").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    public void prohibitProductTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/products/5/prohibit").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
    @Test
    public void prohibitProductTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/10/products/1550/prohibit").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

}