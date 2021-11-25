package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.goods.GoodsApplication;
import cn.edu.xmu.privilegegateway.util.JwtHelper;
import cn.edu.xmu.privilegegateway.util.RedisUtil;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
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
    public void ListByfreightIdTest1() throws Exception
    {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/0/freightmodels/1/products").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"total\":10,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    public void ListByfreightIdTest2() throws Exception
    {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/freightmodels/1/products").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":505,\"errmsg\":\"此商铺没有发布货品的权限\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }


    @Test
    public void insertGoodsTest()throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String json="{\"name\": \"小米\"}";
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/1/goods").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println(responseString);
        String expected="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"name\":\"小米\"}}";
        JSONAssert.assertEquals(expected,responseString,true);

    }
    @Test
    public void insertGoodsTest2()throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String json="{\"name\": \"小米\"}";
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/10/goods").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":512,\"name\":\"小米\",\"productList\":null}}";
        JSONAssert.assertEquals(expected,responseString,true);

    }

    @Test
    public void searchGoodsTest()throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 3600,0);
        Mockito.when(redisUtil.get("g_"+1L)).thenReturn(null);
        Mockito.when(redisUtil.get("g_"+5L)).thenReturn(null);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/goods/1").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println(responseString);
        String expected="{\"code\":\"FIELD_NOTVALID\",\"errmsg\":\"该商品不属于该商铺\",\"data\":null}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    public void searchGoodsTest2()throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        Mockito.when(redisUtil.get("g_"+1L)).thenReturn(null);
        Mockito.when(redisUtil.get("g_"+5L)).thenReturn(null);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/10/goods/1").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"name\":\"集合1\",\"productList\":[{\"id\":2261,\"name\":\"金双汇\",\"imageUrl\":null},{\"id\":2489,\"name\":\"红枣干\",\"imageUrl\":null},{\"id\":3163,\"name\":\"超市清真牛肉肠(50)\",\"imageUrl\":null},{\"id\":3635,\"name\":\"240一见钟情花生奶\",\"imageUrl\":null},{\"id\":4010,\"name\":\"和氏贝能健较大婴儿奶粉\",\"imageUrl\":null},{\"id\":4454,\"name\":\"苯鸡蛋礼箱\",\"imageUrl\":null},{\"id\":4496,\"name\":\"花翠鸟唇膏\",\"imageUrl\":null},{\"id\":4707,\"name\":\"11512桶华龙珍品坊\",\"imageUrl\":null},{\"id\":5211,\"name\":\"辉煌冷水壶\",\"imageUrl\":null}],\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T14:00:33\",\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    public void updateGoodsTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String json="{\"name\": \"小米\"}";
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/10/goods/1").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected=" {\"errno\":0,\"data\":{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    public void updateGoodsTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String json="{\"name\": \"小米\"}";
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/5/goods/2").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }

    @Test
    public void deleteGoodsTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        Mockito.when(redisUtil.get("g_"+1L)).thenReturn(null);
        Mockito.when(redisUtil.get("g_"+5L)).thenReturn(null);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/1/goods/1").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"该商品不属于该商铺\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    public void deleteGoodsTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        Mockito.when(redisUtil.get("g_"+1L)).thenReturn(null);
        Mockito.when(redisUtil.get("g_"+5L)).thenReturn(null);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/10/goods/1").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }

    @Test
    public void publishProductTest() throws Exception {
        adminToken =jwtHelper.createToken(0L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/0/products/5/publish").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"操作的资源id不存在\",\"data\":null}";
        JSONAssert.assertEquals(expected,responseString,true);
    }

    @Test
    public void publishProductTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/10/products/1550/publish").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null}";
        JSONAssert.assertEquals(expected,responseString,true);
    }

    @Test
    public void onshelvesProductTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/0/products/5/onshelves").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"操作的资源id不存在\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    public void onshelvesProductTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/10/products/2/onshelves").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }

    @Test
    public void offshelvesProductTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/products/10/offshelves").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    public void offshelvesProductTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/4/products/1552/offshelves").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }

    @Test
    public void allowProductTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/products/10/allow").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    public void allowProductTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/10/products/1/allow").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }

    @Test
    public void prohibitProductTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/products/5/prohibit").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    public void prohibitProductTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/10/products/1550/prohibit").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void GET_testGoods01() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/shops/4/goods/21").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"name\":\"集合21\",\"productList\":[{\"id\":2056,\"name\":\"彩虹果汁糖\",\"imageUrl\":null},{\"id\":2153,\"name\":\"白象大骨面、原汁猪骨\",\"imageUrl\":null},{\"id\":2424,\"name\":\"鲜鸡塘汤面\",\"imageUrl\":null},{\"id\":2457,\"name\":\"护舒宝\",\"imageUrl\":null},{\"id\":2792,\"name\":\"野生紫菜\",\"imageUrl\":null},{\"id\":3377,\"name\":\"金阳光烤肉\",\"imageUrl\":null},{\"id\":3702,\"name\":\"晨露固体清香剂（茉莉花）\",\"imageUrl\":null},{\"id\":4835,\"name\":\"五合巧力卷\",\"imageUrl\":null},{\"id\":4873,\"name\":\"双枪竹筷\",\"imageUrl\":null}],\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T14:00:33\",\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void GET_testGoods02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/shops/4/goods/20000").header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":\"504\",\"errmsg\":\"商品id不存在\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void GET_testGoods04() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/shops/5/goods/291").header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":\"505\",\"errmsg\":\"该商品不属于该商铺\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void POST_testGoods01() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String requestJson="{\"name\":\"新建商品\"}";
        String responseString = this.mockMvc.perform(post("/shops/5/goods").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())

                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"name\":\"新建商品\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void POST_testGoods02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String requestJson="{\"name\":\"\"}";
        String responseString = this.mockMvc.perform(post("/shops/1/goods").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":503,\"errmsg\":\"传入的RequestBody参数格式不合法\"}}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void DELETE_testGoods01() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(delete("/shops/9/goods/500").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void DELETE_testGoods02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(delete("/shops/4/goods/668").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"商品id不存在\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void DELETE_testGoods03() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(delete("/shops/6/goods/20000").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"商品id不存在\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void PUT_testGoods01() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String requestJson="{\"name\":\"修改商品\"}";
        String responseString = this.mockMvc.perform(put("/shops/4/goods/145").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void PUT_testGoods02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String requestJson="{\"name\":\"\"}";
        String responseString = this.mockMvc.perform(put("/shops/4/goods/21").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":503,\"errmsg\":\"传入的RequestBody参数格式不合法\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void PUT_testGoods03() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String requestJson="{\"name\":\"修改商品\"}";
        String responseString = this.mockMvc.perform(put("/shops/5/goods/21").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"该商品不属于该商铺\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    public void PUT_testGoods05() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String requestJson="{\"name\":\"修改商品\"}";
        String responseString = this.mockMvc.perform(put("/shops/4/goods/20000").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"商品id不存在\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }

    //ProductController
    @Test
    @Transactional
    public void PUB_testProduct01() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/products/1550/publish").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"操作的资源id不存在\",\"data\":null}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void PUB_testProduct02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/1/products/1550/publish").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void PUB_testProduct03() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/1/products/20000/publish").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void ONSHELF_testProduct01() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/4/products/1551/onshelves").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void ONSHELF_testProduct02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/5/products/1551/onshelves").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void ONSHELF_testProduct03() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/5/products/20000/onshelves").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void ONSHELF_testProduct05() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/5/products/1555/onshelves").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void OFFSHELF_testProduct01() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/4/products/1552/offshelves").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void OFFSHELF_testProduct02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/5/products/1552/offshelves").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void OFFSHELF_testProduct03() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/products/20000/offshelves").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"操作的资源id不存在\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void OFFSHELF_testProduct05() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/products/1555/offshelves").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"STATENOTALLOW\",\"errmsg\":\"当前状态禁止此操作\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void BANSHELF_testProduct01() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/products/1553/prohibit").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void BANSHELF_testProduct02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/5/products/1553/prohibit").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"此商铺没有发布货品的权限\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void BANSHELF_testProduct03() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/products/20000/prohibit").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"操作的资源id不存在\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void BANSHELF_testProduct05() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/products/1555/prohibit").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void UNBANSHELF_testProduct01() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/products/1554/allow").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"STATENOTALLOW\",\"errmsg\":\"当前状态禁止此操作\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void UNBANSHELF_testProduct02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/products/1554/allow").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"STATENOTALLOW\",\"errmsg\":\"当前状态禁止此操作\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void UNBANSHELF_testProduct03() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/products/20000/allow").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"操作的资源id不存在\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void UNBANSHELF_testProduct05() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/products/1555/allow").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"code\":\"STATENOTALLOW\",\"errmsg\":\"当前状态禁止此操作\",\"data\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }

}