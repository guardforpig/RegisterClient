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
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        Mockito.when(redisUtil.get("g_"+1L)).thenReturn(null);
        Mockito.when(redisUtil.get("g_"+5L)).thenReturn(null);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/goods/1").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"code\":\"FIELD_NOTVALID\",\"errmsg\":\"字段不合法\",\"data\":null}";
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
        String expected="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":1,\"name\":\"集合1\",\"productList\":[{\"id\":2261,\"shopId\":10,\"shopName\":\"商铺10\",\"goodsId\":1,\"categoryId\":248,\"freightId\":1,\"skuSn\":null,\"name\":\"金双汇\",\"originalPrice\":43648,\"weight\":65,\"imageUrl\":null,\"barcode\":\"6902890223153\",\"unit\":\"根\",\"originPlace\":\"漯河\",\"createdBy\":1,\"createName\":\"admin\",\"modifiedBy\":null,\"modiName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":2489,\"shopId\":10,\"shopName\":\"商铺10\",\"goodsId\":1,\"categoryId\":275,\"freightId\":1,\"skuSn\":null,\"name\":\"红枣干\",\"originalPrice\":93421,\"weight\":2005,\"imageUrl\":null,\"barcode\":\"6922802411232\",\"unit\":\"\",\"originPlace\":\"\",\"createdBy\":1,\"createName\":\"admin\",\"modifiedBy\":null,\"modiName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":3163,\"shopId\":10,\"shopName\":\"商铺10\",\"goodsId\":1,\"categoryId\":227,\"freightId\":1,\"skuSn\":null,\"name\":\"超市清真牛肉肠(50)\",\"originalPrice\":29757,\"weight\":1016,\"imageUrl\":null,\"barcode\":\"6902890031017\",\"unit\":\"袋\",\"originPlace\":\"\",\"createdBy\":1,\"createName\":\"admin\",\"modifiedBy\":null,\"modiName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":3635,\"shopId\":10,\"shopName\":\"商铺10\",\"goodsId\":1,\"categoryId\":209,\"freightId\":1,\"skuSn\":null,\"name\":\"240一见钟情花生奶\",\"originalPrice\":28458,\"weight\":20,\"imageUrl\":null,\"barcode\":\"6906301002665\",\"unit\":\"瓶\",\"originPlace\":\"\",\"createdBy\":1,\"createName\":\"admin\",\"modifiedBy\":null,\"modiName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":4010,\"shopId\":10,\"shopName\":\"商铺10\",\"goodsId\":1,\"categoryId\":225,\"freightId\":1,\"skuSn\":null,\"name\":\"和氏贝能健较大婴儿奶粉\",\"originalPrice\":17029,\"weight\":400,\"imageUrl\":null,\"barcode\":\"6914394888066\",\"unit\":\"袋\",\"originPlace\":\"\",\"createdBy\":1,\"createName\":\"admin\",\"modifiedBy\":null,\"modiName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":4454,\"shopId\":10,\"shopName\":\"商铺10\",\"goodsId\":1,\"categoryId\":241,\"freightId\":1,\"skuSn\":null,\"name\":\"苯鸡蛋礼箱\",\"originalPrice\":2195,\"weight\":60,\"imageUrl\":null,\"barcode\":\"6920674731616\",\"unit\":\"箱\",\"originPlace\":\"\",\"createdBy\":1,\"createName\":\"admin\",\"modifiedBy\":null,\"modiName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":4496,\"shopId\":10,\"shopName\":\"商铺10\",\"goodsId\":1,\"categoryId\":230,\"freightId\":1,\"skuSn\":null,\"name\":\"花翠鸟唇膏\",\"originalPrice\":42333,\"weight\":45,\"imageUrl\":null,\"barcode\":\"6920850266668\",\"unit\":\"支\",\"originPlace\":\"\",\"createdBy\":1,\"createName\":\"admin\",\"modifiedBy\":null,\"modiName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":4707,\"shopId\":10,\"shopName\":\"商铺10\",\"goodsId\":1,\"categoryId\":261,\"freightId\":1,\"skuSn\":null,\"name\":\"11512桶华龙珍品坊\",\"originalPrice\":14052,\"weight\":12,\"imageUrl\":null,\"barcode\":\"6921555500712\",\"unit\":\"件\",\"originPlace\":\"\",\"createdBy\":1,\"createName\":\"admin\",\"modifiedBy\":null,\"modiName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":5211,\"shopId\":10,\"shopName\":\"商铺10\",\"goodsId\":1,\"categoryId\":223,\"freightId\":1,\"skuSn\":null,\"name\":\"辉煌冷水壶\",\"originalPrice\":8520,\"weight\":0,\"imageUrl\":null,\"barcode\":\"6923173697973\",\"unit\":\"个\",\"originPlace\":\"\",\"createdBy\":1,\"createName\":\"admin\",\"modifiedBy\":null,\"modiName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2}]}}";
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
        String expected="{\"errno\":0,\"data\":{\"name\":\"集合21\",\"productList\":[{\"id\":2056,\"shopId\":4,\"shopName\":\"努力向前\",\"goodsId\":21,\"categoryId\":266,\"freightId\":1,\"skuSn\":null,\"name\":\"彩虹果汁糖\",\"originalPrice\":27450,\"weight\":30,\"imageUrl\":null,\"barcode\":\"6923450603574\",\"unit\":\"个\",\"originPlace\":\"广州\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":2153,\"shopId\":4,\"shopName\":\"努力向前\",\"goodsId\":21,\"categoryId\":254,\"freightId\":1,\"skuSn\":null,\"name\":\"白象大骨面、原汁猪骨\",\"originalPrice\":65436,\"weight\":110,\"imageUrl\":null,\"barcode\":\"6935270601739\",\"unit\":\"袋\",\"originPlace\":\"河南\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":2424,\"shopId\":4,\"shopName\":\"努力向前\",\"goodsId\":21,\"categoryId\":247,\"freightId\":1,\"skuSn\":null,\"name\":\"鲜鸡塘汤面\",\"originalPrice\":27586,\"weight\":330,\"imageUrl\":null,\"barcode\":\"6925996501512\",\"unit\":\"\",\"originPlace\":\"\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":2457,\"shopId\":4,\"shopName\":\"努力向前\",\"goodsId\":21,\"categoryId\":258,\"freightId\":1,\"skuSn\":null,\"name\":\"护舒宝\",\"originalPrice\":83836,\"weight\":10,\"imageUrl\":null,\"barcode\":\"6903148032121\",\"unit\":\"\",\"originPlace\":\"\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":2792,\"shopId\":4,\"shopName\":\"努力向前\",\"goodsId\":21,\"categoryId\":273,\"freightId\":1,\"skuSn\":null,\"name\":\"野生紫菜\",\"originalPrice\":66017,\"weight\":50,\"imageUrl\":null,\"barcode\":\"6936906025585\",\"unit\":\"\",\"originPlace\":\"\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":3377,\"shopId\":4,\"shopName\":\"努力向前\",\"goodsId\":21,\"categoryId\":218,\"freightId\":1,\"skuSn\":null,\"name\":\"金阳光烤肉\",\"originalPrice\":65767,\"weight\":30,\"imageUrl\":null,\"barcode\":\"6902890230052\",\"unit\":\"包\",\"originPlace\":\"\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":3702,\"shopId\":4,\"shopName\":\"努力向前\",\"goodsId\":21,\"categoryId\":273,\"freightId\":1,\"skuSn\":null,\"name\":\"晨露固体清香剂（茉莉花）\",\"originalPrice\":67288,\"weight\":70,\"imageUrl\":null,\"barcode\":\"6907469009114\",\"unit\":\"盒\",\"originPlace\":\"\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":4835,\"shopId\":4,\"shopName\":\"努力向前\",\"goodsId\":21,\"categoryId\":247,\"freightId\":1,\"skuSn\":null,\"name\":\"五合巧力卷\",\"originalPrice\":23328,\"weight\":80,\"imageUrl\":null,\"barcode\":\"6921828053242\",\"unit\":\"包\",\"originPlace\":\"\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":4873,\"shopId\":4,\"shopName\":\"努力向前\",\"goodsId\":21,\"categoryId\":246,\"freightId\":1,\"skuSn\":null,\"name\":\"双枪竹筷\",\"originalPrice\":19336,\"weight\":10,\"imageUrl\":null,\"barcode\":\"6921988313415\",\"unit\":\"袋\",\"originPlace\":\"浙江\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void GET_testGoods02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/shops/4/goods/20000").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"操作的资源id不存在\",\"data\":null}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void GET_testGoods04() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/shops/5/goods/291").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"该商品不属于该商铺\",\"data\":null}";
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
        String expected="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":503,\"name\":\"新建商品\",\"productList\":null}}";
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
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"操作的资源id不存在\",\"data\":null},\"errmsg\":\"成功\"}";
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
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"操作的资源id不存在\",\"data\":null},\"errmsg\":\"成功\"}";
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
        String expected="{\"errno\":0,\"data\":{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"操作的资源id不存在\",\"data\":null},\"errmsg\":\"成功\"}";
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
        String expected="{\"errno\":0,\"data\":{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null},\"errmsg\":\"成功\"}";
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