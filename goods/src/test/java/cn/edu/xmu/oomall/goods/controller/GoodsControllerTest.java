package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.GoodsApplication;
import cn.edu.xmu.oomall.goods.microservice.FreightService;
import cn.edu.xmu.oomall.goods.microservice.ShopService;
import cn.edu.xmu.oomall.goods.microservice.vo.CategoryVo;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleCategoryVo;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleShopVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
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
//    @MockBean
//    private ShopService shopService;
//    @MockBean
//    private FreightService freightService;

    @Test
    public void ListByfreightIdTest1() throws Exception
    {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/0/freightmodels/1/products").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"data\":{\"total\":3912,\"pages\":392,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1550,\"name\":\"欢乐家久宝桃罐头\",\"imageUrl\":null},{\"id\":1551,\"name\":\"欢乐家杨梅罐头\",\"imageUrl\":null},{\"id\":1552,\"name\":\"欢乐家蜜桔\",\"imageUrl\":null},{\"id\":1553,\"name\":\"欢乐家岭南杂果罐头\",\"imageUrl\":null},{\"id\":1554,\"name\":\"黑金刚巧力\",\"imageUrl\":null},{\"id\":1555,\"name\":\"黑金刚咔奇脆巧力\",\"imageUrl\":null},{\"id\":1556,\"name\":\"黑金刚大蘑头\",\"imageUrl\":null},{\"id\":1557,\"name\":\"奥利奥原味\",\"imageUrl\":null},{\"id\":1558,\"name\":\"奥利奥树莓蓝莓\",\"imageUrl\":null},{\"id\":1559,\"name\":\"奥利奥缤纷双果味\",\"imageUrl\":null}]},\"errmsg\":\"成功\"}";
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
    public void ListByfreightIdTest3() throws Exception
    {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/0/freightmodels/2/products").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
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
        String expected="{\"errno\":504,\"errmsg\":\"商品id不存在\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void GET_testGoods03() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/shops/5/goods/291").header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":505,\"errmsg\":\"该商品不属于该商铺\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void GET_testGoods04() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",10L, 3600,0);
        String responseString=this.mockMvc.perform(get("/shops/5/goods/291").header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":503,\"errmsg\":\"departId不匹配\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void POST_testGoods01() throws Exception {
        String requestJson="{\"name\":\"新建商品\"}";
        String responseString = this.mockMvc.perform(post("/shops/5/goods").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"name\":\"新建商品\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
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
    public void POST_testGoods03() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",10L, 3600,0);
        String requestJson="{\"name\":\"\"}";
        String responseString = this.mockMvc.perform(post("/shops/1/goods").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":503,\"errmsg\":\"departId不匹配\"}";
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
        adminToken =jwtHelper.createToken(1L,"admin",10L, 3600,0);
        String responseString = this.mockMvc.perform(delete("/shops/4/goods/668").header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":503,\"errmsg\":\"departId不匹配\"}";
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
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void PUT_testGoods02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String requestJson="{\"name\":\"\"}";
        String responseString = this.mockMvc.perform(put("/shops/4/goods/21").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isBadRequest())
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
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":505,\"errmsg\":\"该商品不属于该商铺\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void PUT_testGoods04() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String requestJson="{\"name\":\"修改商品\"}";
        String responseString = this.mockMvc.perform(put("/shops/4/goods/21").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    public void PUT_testGoods05() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String requestJson="{\"name\":\"修改商品\"}";
        String responseString = this.mockMvc.perform(put("/shops/4/goods/20000").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":504,\"errmsg\":\"商品id不存在\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }

    //ProductController
    @Test
    @Transactional
    public void PUB_testProduct01() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,10000);
        String responseString = this.mockMvc.perform(put("/shops/0/draftproducts/598656/publish")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"id\":1576,\"name\":null,\"imageUrl\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void PUB_testProduct02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/1/draftproducts/1550/publish").header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void PUB_testProduct03() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/draftproducts/20000/publish").header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void ONSHELF_testProduct01() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/3/products/1551/onshelves").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":507,\"errmsg\":\"当前货品状态不支持进行该操作\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void ONSHELF_testProduct02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/5/products/1551/onshelves").header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":505,\"errmsg\":\"该货品不属于该商铺\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void ONSHELF_testProduct03() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/5/products/20000/onshelves").header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":504,\"errmsg\":\"货品id不存在\"}";
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
        String expected="{\"errno\":507,\"errmsg\":\"当前货品状态不支持进行该操作\"}";
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
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void OFFSHELF_testProduct02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/5/products/1552/offshelves").header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":505,\"errmsg\":\"该货品不属于该商铺\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void OFFSHELF_testProduct03() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/products/20000/offshelves").header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":504,\"errmsg\":\"货品id不存在\"}";
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
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void BANSHELF_testProduct02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/1/products/1553/prohibit").header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":505,\"errmsg\":\"此商铺没有发布货品的权限\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void BANSHELF_testProduct03() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/products/20000/prohibit").header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":504,\"errmsg\":\"货品id不存在\"}";
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
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
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
        String expected="{\"errno\":507,\"errmsg\":\"当前货品状态不支持进行该操作\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void UNBANSHELF_testProduct02() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/0/products/1553/allow").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":507,\"errmsg\":\"当前货品状态不支持进行该操作\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void UNBANSHELF_testProduct03() throws Exception {
        String responseString = this.mockMvc.perform(put("/shops/0/products/20000/allow").header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":504,\"errmsg\":\"货品id不存在\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }
    @Test
    @Transactional
    public void UNBANSHELF_testProduct05() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600,0);
        String responseString = this.mockMvc.perform(put("/shops/1/products/1555/allow").header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":505,\"errmsg\":\"此商铺没有发布货品的权限\"}";
        JSONAssert.assertEquals(expected,responseString,true);
    }

    @BeforeEach
    public void init() {
        CategoryVo categoryVo2 = new CategoryVo();
        categoryVo2.setId(266L);
        SimpleShopVo simpleShopVo = new SimpleShopVo();
        simpleShopVo.setId(0L);
        simpleShopVo.setName("");


//        Mockito.when(shopService.getSecondCategory(0L)).thenReturn(new InternalReturnObject(0, "", List.of(categoryVo2)));
//        Mockito.when(shopService.getSimpleShopById(1L)).thenReturn(new InternalReturnObject(1, "", List.of(simpleShopVo)));
//        Mockito.when(shopService.getSimpleShopById(2L)).thenReturn(new InternalReturnObject(1, "", List.of()));
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);
    }
    @Test
    @Transactional(readOnly = true)
    public void secondProducts1() throws Exception {
        String contentAsString = this.mockMvc.perform(get("/categories/266/products")
                        .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"total\":46,\"pages\":5,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1561,\"name\":\"奥利奥（桶装）\",\"imageUrl\":null},{\"id\":1567,\"name\":\"六神花露水\",\"imageUrl\":null},{\"id\":1765,\"name\":\"康师傅包（爆椒）\",\"imageUrl\":null},{\"id\":1935,\"name\":\"50立白儿童牙膏\",\"imageUrl\":null},{\"id\":1970,\"name\":\"凯达空气清新剂\",\"imageUrl\":null},{\"id\":1971,\"name\":\"凯达桂花空气清新剂\",\"imageUrl\":null},{\"id\":2056,\"name\":\"彩虹果汁糖\",\"imageUrl\":null},{\"id\":2088,\"name\":\"不锈钢口杯\",\"imageUrl\":null},{\"id\":2118,\"name\":\"达能王子草莓饼干\",\"imageUrl\":null},{\"id\":2255,\"name\":\"双汇清真鸡肉肠400\",\"imageUrl\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,contentAsString,true);
    }
    @Test
    @Transactional(readOnly = true)
    public void secondProducts2() throws Exception {
        String contentAsString = this.mockMvc.perform(get("/categories/3/products")
                        .header("authorization", adminToken))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expected,contentAsString,true);
    }
    @Test
    @Transactional(readOnly = true)
    public void secondShopProducts1() throws Exception {
        String contentAsString = this.mockMvc.perform(get("/shops/1/categories/266/products")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"total\":6,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1561,\"name\":\"奥利奥（桶装）\",\"imageUrl\":null},{\"id\":1971,\"name\":\"凯达桂花空气清新剂\",\"imageUrl\":null},{\"id\":2739,\"name\":\"迎华牌中老年无糖麦\",\"imageUrl\":null},{\"id\":3407,\"name\":\"金龙鱼AE营养菜籽油5000\",\"imageUrl\":null},{\"id\":4560,\"name\":\"400鹰威饼干\",\"imageUrl\":null},{\"id\":5124,\"name\":\"金顺昌壮乡桂圆糕150\",\"imageUrl\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,contentAsString,true);
    }
    @Test
    @Transactional(readOnly = true)
    public void secondShopProducts2() throws Exception {
        String contentAsString = this.mockMvc.perform(get("/shops/1/categories/3/products")
                        .header("authorization", adminToken))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expected,contentAsString,true);
    }

    /**
     * 获得货品的所有状态
     */
    @Test
    @Transactional
    public void getAllState() throws Exception {
        String responseString = this.mockMvc.perform(get("/products/states"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"草稿\"},{\"code\":1,\"name\":\"下架\"},{\"code\":2,\"name\":\"上架\"},{\"code\":3,\"name\":\"禁售中\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    public void searchProduct() throws Exception {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        //shopId和barCode都不为空
        String responseString = this.mockMvc.perform(get("/products?shopId=10&barCode=6924583291690")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{" +
                "    \"errno\": 0," +
                "    \"data\": {" +
                "        \"page\": 1," +
                "        \"pageSize\": 1," +
                "        \"total\": 1," +
                "        \"pages\": 1," +
                "        \"list\": [" +
                "            {" +
                "                \"id\": 1576," +
                "                \"name\": \"龙亮逍遥胡辣汤\"," +
                "                \"imageUrl\": null" +
                "            }" +
                "        ]" +
                "    }," +
                "    \"errmsg\": \"成功\"" +
                "}";
        JSONAssert.assertEquals(expected, responseString, true);

        //shopId和barCode都不匹配
        String responseString3 = this.mockMvc.perform(get("/products?shopId=10&barCode=1")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected3 = "{" +
                "    \"errno\": 0," +
                "    \"data\": {" +
                "        \"page\": 1," +
                "        \"pageSize\": 0," +
                "        \"total\": 0," +
                "        \"pages\": 0," +
                "        \"list\": [" +
                "        ]" +
                "    }," +
                "    \"errmsg\": \"成功\"" +
                "}";
        JSONAssert.assertEquals(expected3, responseString3, true);
    }

    /**
     * 获得product的详细信息
     *
     * @throws Exception
     * @author wyg
     * @Date 2021/11/13
     */
    @Test
    @Transactional
    public void getProductDetail() throws Exception {
        SimpleCategoryVo simpleCategoryVo = new SimpleCategoryVo();
        simpleCategoryVo.setId(1L);
        simpleCategoryVo.setName("test");
//        Mockito.when(shopService.getCategoryById(270L)).thenReturn(new InternalReturnObject(0, "", simpleCategoryVo));
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        //正常
        String responseString = this.mockMvc.perform(get("/products/1576")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"id\":1576,\"shop\":{\"id\":10,\"name\":\"商铺10\"},\"goodsId\":243,\"onSaleId\":27,\"name\":\"龙亮逍遥胡辣汤\",\"skuSn\":null,\"imageUrl\":null,\"originalPrice\":18039,\"weight\":85,\"price\":4938,\"quantity\":36,\"state\":2,\"unit\":\"包\",\"barCode\":null,\"originPlace\":\"河南\",\"category\":{\"id\":270,\"name\":\"订餐服务\"},\"shareable\":null,\"freightId\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 店家查看货品详细信息
     *
     * @throws Exception
     * @author wyg
     * @Date 2021/11/13
     */
    @Test
    @Transactional
    public void getShopProductDetail() throws Exception {
//        SimpleCategoryVo simpleCategoryVo = new SimpleCategoryVo();
//        simpleCategoryVo.setId(1L);
//        simpleCategoryVo.setName("test");

//        Mockito.when(shopService.getCategoryById(270L)).thenReturn(new InternalReturnObject(0, "", simpleCategoryVo));
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        String responseString = this.mockMvc.perform(get("/shops/10/products/1576")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"id\":1576,\"shop\":{\"id\":10,\"name\":\"商铺10\"},\"goodsId\":243,\"onSaleId\":27,\"name\":\"龙亮逍遥胡辣汤\",\"skuSn\":null,\"imageUrl\":null,\"originalPrice\":18039,\"weight\":85,\"state\":2,\"unit\":\"包\",\"barCode\":null,\"originPlace\":\"河南\",\"category\":{\"id\":270,\"name\":\"订餐服务\"},\"createBy\":null,\"gmtCreate\":\"2021-11-11T13:12:48.000\",\"gmtModified\":null,\"modifiedBy\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    public void addProductToGoods() throws Exception {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        //正常 由于返回的Json中有创建时间，这一项不测试返回的字符串
        String json = "{\"skuSn\": \"test11\"," +
                "\"name\": \"测试商品1\"," +
                "\"originalPrice\": 100000," +
                "\"weight\": 1000," +
                "\"categoryId\": 275," +
                "  \"goodsId\": 92}";
        String responseString = this.mockMvc.perform(post("/shops/10/draftproducts")
                .header("authorization", adminToken)
                .content(json)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String expected = "{" +
                "    \"errno\": 0," +
                "    \"errmsg\": \"成功\"" +
                "}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 上传货品图片
     *
     * @throws Exception
     * @author wyg
     * @Date 2021/11/13
     */
/*    @Test
    @Transactional
    public void upLoadImage() throws Exception {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/shops/10/draftproducts/60/uploadImg")
                .file(new MockMultipartFile("multipartFile", "imageTest.jpg", "jpg",
                        new FileInputStream(new File("D:/Code/JAVA/otherModule/oomall-main/goods/src/test/resources/imageTest.jpg"))))
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"));
        MvcResult mvcResult = resultActions.andDo(MockMvcResultHandlers.print()).andExpect(status().isInternalServerError()).andReturn();
        String responseString = mvcResult.getResponse().getContentAsString();
    }*/

    /**
     * 管理员或店家物理删除审核态的Products
     *
     * @throws Exception
     * @author wyg
     * @Date 2021/11/13
     */
    @Test
    @Transactional
    public void deleteDraftProduct() throws Exception {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        String responseString = this.mockMvc.perform(delete("/shops/10/draftproducts/70")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{" +
                "    \"errno\": 0," +
                "    \"errmsg\": \"成功\"" +
                "}";
        JSONAssert.assertEquals(expected, responseString, true);

        //shopId和productId不匹配
        String responseString1 = this.mockMvc.perform(delete("/shops/10/draftproducts/1")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected1 = "{" +
                "    \"errno\": 504," +
                "    \"errmsg\": \"操作的资源id不存在\"" +
                "}";
        JSONAssert.assertEquals(expected1, responseString1, true);
    }

    /**
     * 店家修改审核态货品信息
     *
     * @throws Exception
     * @author wyg
     * @Date 2021/11/13
     */
    @Test
    @Transactional
    public void changeDraftProduct() throws Exception {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 100000);
        String json = "{\"skuSn\": \"2222222\"}";
        String responseString = this.mockMvc.perform(put("/shops/1/draftproducts/71")
                .header("authorization", adminToken)
                .content(json)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{" +
                "    \"errno\": 0," +
                "    \"errmsg\": \"成功\"" +
                "}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 店家修改下线态货品信息
     *
     * @throws Exception
     * @author wyg
     * @Date 2021/11/13
     */
    @Test
    @Transactional
    public void changeProduct() throws Exception {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        String requestJson = "{" +
                "\"skuSn\": \"string\"," +
                "\"name\": \"string\"," +
                "\"originalPrice\": 1," +
                "\"categoryId\": 1," +
                "\"weight\": 1," +
                "\"barCode\": \"123456\"," +
                "\"unit\": \"string\"," +
                "\"originPlace\": \"string\"" +
                "}";
        String responseString = this.mockMvc.perform(put("/shops/3/products/1570")
                .header("authorization", adminToken)
                .content(requestJson)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{" +
                "    \"errno\": 0," +
                "    \"errmsg\": \"成功\"" +
                "}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    public void getGoodsProduct()throws Exception{
        //正常
        String responseString = this.mockMvc.perform(get("/goods/289")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{" +
                "    \"errno\": 0," +
                "    \"data\": {" +
                "        \"id\": 289," +
                "        \"name\": \"集合289\"," +
                "        \"productList\": [" +
                "            {" +
                "                \"id\": 1555," +
                "                \"name\": \"黑金刚咔奇脆巧力\"," +
                "                \"imageUrl\": null" +
                "            }," +
                "            {" +
                "                \"id\": 2134," +
                "                \"name\": \"米多奇馍丁\"," +
                "                \"imageUrl\": null" +
                "            }," +
                "            {" +
                "                \"id\": 2903," +
                "                \"name\": \"燕姿秀女袜\"," +
                "                \"imageUrl\": null" +
                "            }," +
                "            {" +
                "                \"id\": 3292," +
                "                \"name\": \"香烤火腿(280)\"," +
                "                \"imageUrl\": null" +
                "            }," +
                "            {" +
                "                \"id\": 3920," +
                "                \"name\": \"好吃点金牌卷心酥酸奶\"," +
                "                \"imageUrl\": null" +
                "            }," +
                "            {" +
                "                \"id\": 4397," +
                "                \"name\": \"P-6040投降狗\"," +
                "                \"imageUrl\": null" +
                "            }," +
                "            {" +
                "                \"id\": 4435," +
                "                \"name\": \"津城果仁巧力\"," +
                "                \"imageUrl\": null" +
                "            }," +
                "            {" +
                "                \"id\": 4528," +
                "                \"name\": \"枣都多花种蜂蜜950\"," +
                "                \"imageUrl\": null" +
                "            }," +
                "            {" +
                "                \"id\": 5333," +
                "                \"name\": \"神田香辣芝麻盐\"," +
                "                \"imageUrl\": null" +
                "            }," +
                "            {" +
                "                \"id\": 5383," +
                "                \"name\": \"400统业中小学生加锌奶粉\"," +
                "                \"imageUrl\": null" +
                "            }," +
                "            {" +
                "                \"id\": 5448," +
                "                \"name\": \"舒莱16P、棉\"," +
                "                \"imageUrl\": null" +
                "            }" +
                "        ]" +
                "    }," +
                "    \"errmsg\": \"成功\"" +
                "}";
        JSONAssert.assertEquals(expected, responseString, true);

        //goods不存在
        String responseString1 = this.mockMvc.perform(get("/goods/0")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected1 = "{" +
                "    \"errno\": 504," +
                "    \"errmsg\": \"操作的资源id不存在\"" +
                "}";
        JSONAssert.assertEquals(expected1, responseString1, true);
    }

    /**
     * 内部API-将上线态的秒杀商品加载到Redis
     *
     * @throws Exception
     * @Date 2021/11/13
     */
    @Test
    @Transactional
    public void loadSecondKillProduct() throws Exception {
        CustomComparator CUSTOM_COMPARATOR = new CustomComparator(JSONCompareMode.LENIENT,
                new Customization("data.id", (o1, o2) -> true));
        String responseString = this.mockMvc.perform(get("/internal/secondkillproducts/load?beginTime=2021-11-11 15:01:02&endTime=2021-11-11 15:01:02")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, CUSTOM_COMPARATOR);
    }


    @Test
    @Transactional
    public void getFreightModels() throws Exception {
//        Mockito.when(freightService.getFreightModel(10L,1L)).thenReturn(new InternalReturnObject(ReturnNo.OK));

        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        String responseString = this.mockMvc.perform(get("/shops/10/products/1576/freightmodels").header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"id\":1,\"name\":\"freight model/100g\",\"type\":0,\"unit\":100,\"defaultModel\":0,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2020-12-02T20:33:08.000\",\"gmtModified\":\"2020-12-02T20:33:08.000\",\"modifier\":{\"id\":null,\"name\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    public void changeFreightModels() throws Exception {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        String responseString = this.mockMvc.perform(post("/shops/10/products/1576/freightmodels/2").header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\": 0,\"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }
}
