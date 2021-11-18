package cn.edu.xmu.oomall.coupon.controller;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.microservice.GoodsService;
import cn.edu.xmu.oomall.coupon.util.CreateObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author qingguo Hu 22920192204208
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Rollback(value = true)
public class CouponControllerTest {

    private static String adminToken;

    private static JwtHelper jwtHelper = new JwtHelper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GoodsService goodsService;

    @Before
    public void init() throws Exception {
        // 返回ProductVo
        ReturnObject<VoObject> ProductVo1 = CreateObject.createProductVo(100L);
        ReturnObject<VoObject> ProductVo2 = CreateObject.createProductVo(10L);
        Mockito.when(goodsService.getProductByOnsaleId(1L)).thenReturn(ProductVo1);
        Mockito.when(goodsService.getProductByOnsaleId(2L)).thenReturn(ProductVo2);

        // 返回OnsaleVoList
        ReturnObject<List<VoObject>> onsaleVoList1 = CreateObject.createOnsaleVoList1();
        Mockito.when(goodsService.listOnsalesByProductId(1550L)).thenReturn(onsaleVoList1);
        ReturnObject<List<VoObject>> onsaleVoList2 = CreateObject.createOnsaleVoList2();
        Mockito.when(goodsService.listOnsalesByProductId(10000L)).thenReturn(onsaleVoList2);
        ReturnObject<List<VoObject>> onsaleVoList3 = CreateObject.createOnsaleVoList3();
        Mockito.when(goodsService.listOnsalesByProductId(1549L)).thenReturn(onsaleVoList3);
        ReturnObject<List<VoObject>> onsaleVoList4 = CreateObject.createOnsaleVoList4();
        Mockito.when(goodsService.listOnsalesByProductId(1548L)).thenReturn(onsaleVoList4);

        adminToken = jwtHelper.createToken(1L,"admin",1L, 3600);
    }


    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testListProductsByCouponActivityId() throws Exception {
        // 活动不存在
        String responseString = mvc.perform(get("/couponactivities/10000/products"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 字段不合法
        responseString = mvc.perform(get("/couponactivities/-1/products"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 活动没有对应的商品
        responseString = mvc.perform(get("/couponactivities/12/products"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 正常
        responseString = mvc.perform(get("/couponactivities/11/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":10,\"name\":null,\"imageUrl\":null},{\"id\":100,\"name\":null,\"imageUrl\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testListCouponActivitiesByProductId() throws Exception {
        // 字段不合法
        String responseString = mvc.perform(get("/products/-1/couponactivities"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 货品不存在
        responseString = mvc.perform(get("/products/10000/couponactivities"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 货品对应的Onsale不在CouponOnsale中
        responseString = mvc.perform(get("/products/1549/couponactivities"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // CouponOnsale的活动不存在
        responseString = mvc.perform(get("/products/1548/couponactivities"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 正常
        responseString = mvc.perform(get("/products/1550/couponactivities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":1,\"page\":1,\"list\":[{\"id\":11,\"name\":null,\"beginTime\":null,\"endTime\":null,\"couponTime\":null,\"quantity\":null,\"imageUrl\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testUpdateCouponActivity() throws Exception {

        // 正常
        String requestBody = "{\"name\": \"lalala\",\"beginTime\": \"2021-11-11T15:40:45.000\",\"endTime\": \"2021-11-11T15:40:50.000\",\"couponTime\": \"2021-11-10T15:40:45.000\",\"quantity\": 1110,\"quantityType\": 0,\"validTerm\": 0,\"strategy\": \"string\"}";
        String responseString = mvc.perform(put("/shops/1/couponactivities/3").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 字段不合法
        requestBody = "{\"name\": \"lalala\",\"beginTime\": \"2021-11-11T15:40:45.000\",\"endTime\": \"2021-11-10T15:40:50.000\",\"couponTime\": \"2021-11-10T15:40:45.000\",\"quantity\": 1110,\"quantityType\": 0,\"validTerm\": 0,\"strategy\": \"string\"}";
        responseString = mvc.perform(put("/shops/-1/couponactivities/3").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestBody))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 开始时间晚于结束时间
        requestBody = "{\"name\": \"lalala\",\"beginTime\": \"2021-11-11T15:40:45.000\",\"endTime\": \"2021-11-10T15:40:50.000\",\"couponTime\": \"2021-11-10T15:40:45.000\",\"quantity\": 1110,\"quantityType\": 0,\"validTerm\": 0,\"strategy\": \"string\"}";
        responseString = mvc.perform(put("/shops/1/couponactivities/3").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestBody))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 优惠券发布时间晚于开始时间
        requestBody = "{\"name\": \"lalala\",\"beginTime\": \"2021-11-10T15:40:45.000\",\"endTime\": \"2021-11-10T15:40:50.000\",\"couponTime\": \"2021-11-12T15:40:45.000\",\"quantity\": 1110,\"quantityType\": 0,\"validTerm\": 0,\"strategy\": \"string\"}";
        responseString = mvc.perform(put("/shops/1/couponactivities/3").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestBody))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":950,\"errmsg\":\"优惠卷领卷时间晚于活动开始时间\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 不在草稿态
        requestBody = "{\"name\": \"lalala\",\"beginTime\": \"2021-11-11T15:40:45.000\",\"endTime\": \"2021-11-11T15:40:50.000\",\"couponTime\": \"2021-11-10T15:40:45.000\",\"quantity\": 1110,\"quantityType\": 0,\"validTerm\": 0,\"strategy\": \"string\"}";
        responseString = mvc.perform(put("/shops/3/couponactivities/4").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testDeleteCouponActivity() throws Exception {
        // 正常
        String responseString = mvc.perform(delete("/shops/1/couponactivities/11").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 字段不合法
        responseString = mvc.perform(delete("/shops/-1/couponactivities/13").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 活动不存在
        responseString = mvc.perform(delete("/shops/1/couponactivities/13").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 活动和店铺不对应
        responseString = mvc.perform(delete("/shops/1/couponactivities/10").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"该优惠活动不属于该商店\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 活动没有对应的商品
        responseString = mvc.perform(delete("/shops/2/couponactivities/12").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 不在草稿态
        responseString = mvc.perform(delete("/shops/2/couponactivities/1").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testInsertCouponOnsale() throws Exception {
        // 正常
        String responseString = mvc.perform(post("/shops/3/couponactivities/2/onsales/1").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 字段不合法
        responseString = mvc.perform(post("/shops/-2/couponactivities/15/onsales/1").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 活动不存在
        responseString = mvc.perform(post("/shops/2/couponactivities/15/onsales/1").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 当前状态不允许
        responseString = mvc.perform(post("/shops/2/couponactivities/1/onsales/1").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 活动和店铺不对应
        responseString = mvc.perform(post("/shops/2/couponactivities/11/onsales/1").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"该优惠活动不属于该商店\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testDeleteCouponOnsale() throws Exception {
        // 正常
        String responseString = mvc.perform(delete("/shops/3/coupononsale/5").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 字段不合法
        responseString = mvc.perform(delete("/shops/-3/coupononsale/0").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // CouponSale不存在
        responseString = mvc.perform(delete("/shops/3/coupononsale/0").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 活动不存在
        responseString = mvc.perform(delete("/shops/3/coupononsale/3347").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 该CouponOnSale参与的优惠活动不属于该商店
        responseString = mvc.perform(delete("/shops/1/coupononsale/12").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"该CouponOnSale参与的优惠活动不属于该商店\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 对应活动下线
        responseString = mvc.perform(delete("/shops/2/coupononsale/3307").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

    }


    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testUpdateCouponActivityToOnline() throws Exception {
        // 正常
        String responseString = mvc.perform(put("/shops/2/couponactivities/1/online").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 字段不合法
        responseString = mvc.perform(put("/shops/-3/couponactivities/1/online").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 活动不存在
        responseString = mvc.perform(put("/shops/2/couponactivities/15/online").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 处于上线态
        responseString = mvc.perform(put("/shops/3/couponactivities/2/online").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 活动和店铺不对应
        responseString = mvc.perform(put("/shops/3/couponactivities/11/online").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"该优惠活动不属于该商店\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }


    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testUpdateCouponActivityToOffline() throws Exception {
        // 正常
        String responseString = mvc.perform(put("/shops/3/couponactivities/2/offline").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 字段不合法
        responseString = mvc.perform(put("/shops/-2/couponactivities/1/offline").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 处于下线态
        responseString = mvc.perform(put("/shops/2/couponactivities/1/offline").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

}
