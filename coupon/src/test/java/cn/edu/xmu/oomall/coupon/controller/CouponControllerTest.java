package cn.edu.xmu.oomall.coupon.controller;


import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.microservice.GoodsService;
import cn.edu.xmu.oomall.coupon.microservice.vo.OnsaleVo;
import cn.edu.xmu.oomall.coupon.util.CreateObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author qingguo Hu 22920192204208
 */
@SpringBootTest
@AutoConfigureMockMvc
@Rollback(value = true)
@Transactional
public class CouponControllerTest {

    private static String adminToken;

    private static JwtHelper jwtHelper = new JwtHelper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GoodsService goodsService;

    @MockBean
    private RedisUtil redisUtil;

    @BeforeEach
    public void init() throws Exception {
        // 返回ProductVo
        ReturnObject<OnsaleVo> OnsaleVo1 = CreateObject.createOnsaleVo(100L);
        ReturnObject<OnsaleVo> OnsaleVo2 = CreateObject.createOnsaleVo(10L);
        Mockito.when(goodsService.getOnsaleById(3L)).thenReturn(OnsaleVo1);
        Mockito.when(goodsService.getOnsaleById(4L)).thenReturn(OnsaleVo2);

        // 返回OnsaleVoList
        ReturnObject<List<OnsaleVo>> onsaleVoList1 = CreateObject.createOnsaleVoList1();
        Mockito.when(goodsService.listOnsale(1550L,1, 100)).thenReturn(onsaleVoList1);
        ReturnObject<List<OnsaleVo>> onsaleVoList2 = CreateObject.createOnsaleVoList2();
        Mockito.when(goodsService.listOnsale(10000L, 1, 100)).thenReturn(onsaleVoList2);
        ReturnObject<List<OnsaleVo>> onsaleVoList3 = CreateObject.createOnsaleVoList3();
        Mockito.when(goodsService.listOnsale(1549L,1, 100)).thenReturn(onsaleVoList3);
        ReturnObject<List<OnsaleVo>> onsaleVoList4 = CreateObject.createOnsaleVoList4();
        Mockito.when(goodsService.listOnsale(1548L, 1, 100)).thenReturn(onsaleVoList4);

        // 返回OnsaleVo
        ReturnObject<OnsaleVo> onsaleVo1 = CreateObject.createOnsaleVo1();
        Mockito.when(goodsService.getOnsaleById(3915L)).thenReturn(onsaleVo1);
        ReturnObject<OnsaleVo> onsaleVo2 = CreateObject.createOnsaleVo2();
        Mockito.when(goodsService.getOnsaleById(1L)).thenReturn(onsaleVo2);
        ReturnObject<OnsaleVo> onsaleVo3 = CreateObject.createOnsaleVo3();
        Mockito.when(goodsService.getOnsaleById(2L)).thenReturn(onsaleVo3);
        ReturnObject<OnsaleVo> onsaleVo4 = CreateObject.createOnsaleVo4();
        Mockito.when(goodsService.getOnsaleById(3912L)).thenReturn(onsaleVo4);

//        Mockito.when(redisUtil.get("couponactivity_11")).thenReturn("{\"id\":11,\"name\":null,\"shopId\":1,\"shopName\":null,\"couponTime\":null,\"beginTime\":null,\"endTime\":null,\"quantity\":null,\"quantityType\":null,\"validTerm\":null,\"imageUrl\":null,\"strategy\":null,\"state\":0,\"creatorId\":null,\"creatorName\":null,\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":[2021,11,17,19,2,20],\"gmtModified\":null}");
//        Mockito.when(redisUtil.get("coupononsale_5")).thenReturn("{\"id\":5,\"activityId\":2,\"onsaleId\":21,\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":[2021,11,11,15,40,45],\"gmtModified\":null}");
        Mockito.when(redisUtil.get("couponactivity_11")).thenReturn(null);
        Mockito.when(redisUtil.get("coupononsale_5")).thenReturn(null);

        adminToken = jwtHelper.createToken(1L,"admin",0L, 1,3600);
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
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
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
        expectString = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":10,\"name\":null,\"imageUrl\":null},{\"id\":100,\"name\":null,\"imageUrl\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testListCouponActivitiesByProductId() throws Exception {


        // 正常
        String responseString = mvc.perform(get("/products/1550/couponactivities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":8,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"优惠活动2\",\"beginTime\":\"2021-11-11T14:53:49\",\"endTime\":\"2022-02-19T14:53:49\",\"couponTime\":\"2021-11-01T14:53:49\",\"quantity\":0,\"imageUrl\":null},{\"id\":4,\"name\":\"优惠活动4\",\"beginTime\":\"2021-11-11T14:53:49\",\"endTime\":\"2022-02-19T14:53:49\",\"couponTime\":\"2021-11-01T14:53:49\",\"quantity\":0,\"imageUrl\":null},{\"id\":5,\"name\":\"优惠活动5\",\"beginTime\":\"2021-11-11T14:53:49\",\"endTime\":\"2022-02-19T14:53:49\",\"couponTime\":\"2021-11-01T14:53:49\",\"quantity\":0,\"imageUrl\":null},{\"id\":7,\"name\":\"优惠活动7\",\"beginTime\":\"2021-11-11T14:53:49\",\"endTime\":\"2022-02-19T14:53:49\",\"couponTime\":\"2021-11-01T14:53:49\",\"quantity\":0,\"imageUrl\":null},{\"id\":8,\"name\":\"优惠活动8\",\"beginTime\":\"2021-11-11T14:53:49\",\"endTime\":\"2022-02-19T14:53:49\",\"couponTime\":\"2021-11-01T14:53:49\",\"quantity\":0,\"imageUrl\":null},{\"id\":9,\"name\":\"优惠活动9\",\"beginTime\":\"2021-11-11T14:53:49\",\"endTime\":\"2022-02-19T14:53:49\",\"couponTime\":\"2021-11-01T14:53:49\",\"quantity\":0,\"imageUrl\":null},{\"id\":10,\"name\":\"优惠活动10\",\"beginTime\":\"2021-11-11T14:53:49\",\"endTime\":\"2022-02-19T14:53:49\",\"couponTime\":\"2021-11-01T14:53:49\",\"quantity\":0,\"imageUrl\":null},{\"id\":11,\"name\":null,\"beginTime\":null,\"endTime\":null,\"couponTime\":null,\"quantity\":null,\"imageUrl\":null}]},\"errmsg\":\"成功\"}";
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

    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testUpdateCouponActivity() throws Exception {
        // 正常
        String requestBody = "{\"name\": \"lalala\",\"beginTime\": \"2021-11-11T15:40:45.000+0800\",\"endTime\": \"2021-11-11T15:40:50.000+0800\",\"couponTime\": \"2021-11-10T15:40:45.000+0800\",\"quantity\": 1110,\"quantityType\": 0,\"validTerm\": 0,\"strategy\": \"string\"}";
        String responseString = mvc.perform(put("/shops/1/couponactivities/3").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 字段不合法
        requestBody = "{\"name\": \"lalala\",\"beginTime\": \"2021-11-09T15:40:45.000+0800\",\"endTime\": \"2021-11-10T15:40:50.000+0800\",\"couponTime\": \"2021-11-01T15:40:45.000+0800\",\"quantity\": 1110,\"quantityType\": 0,\"validTerm\": 0,\"strategy\": \"string\"}";
        responseString = mvc.perform(put("/shops/1/couponactivities/-3").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestBody))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 开始时间晚于结束时间
        requestBody = "{\"name\": \"lalala\",\"beginTime\": \"2021-11-11T15:40:45.000+0800\",\"endTime\": \"2021-11-10T15:40:50.000+0800\",\"couponTime\": \"2021-11-10T15:40:45.000+0800\",\"quantity\": 1110,\"quantityType\": 0,\"validTerm\": 0,\"strategy\": \"string\"}";
        responseString = mvc.perform(put("/shops/1/couponactivities/3").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestBody))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 不在草稿态
        requestBody = "{\"name\": \"lalala\",\"beginTime\": \"2021-11-11T15:40:45.000+0800\",\"endTime\": \"2021-11-11T15:40:50.000+0800\",\"couponTime\": \"2021-11-10T15:40:45.000+0800\",\"quantity\": 1110,\"quantityType\": 0,\"validTerm\": 0,\"strategy\": \"string\"}";
        responseString = mvc.perform(put("/shops/1/couponactivities/6").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestBody))
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
        String responseString = mvc.perform(delete("/shops/1/couponactivities/3").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 字段不合法
        responseString = mvc.perform(delete("/shops/1/couponactivities/-1").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
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

        adminToken = jwtHelper.createToken(1L,"admin",2L, 1,3600);
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
        adminToken = jwtHelper.createToken(1L,"admin",3L, 1,3600);
        // 正常
        String responseString = mvc.perform(post("/shops/3/couponactivities/2/onsales/1").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        // 字段不合法
        responseString = mvc.perform(post("/shops/3/couponactivities/-1/onsales/1").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        adminToken = jwtHelper.createToken(1L,"admin",2L, 1,3600);
        // 活动不存在
        responseString = mvc.perform(post("/shops/2/couponactivities/15/onsales/1").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);


        adminToken = jwtHelper.createToken(1L,"admin",3L, 1,3600);
        // onsale不存在
        responseString = mvc.perform(post("/shops/3/couponactivities/2/onsales/3915").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);


        adminToken = jwtHelper.createToken(1L,"admin",2L, 1,3600);

        // 活动和店铺不对应
        responseString = mvc.perform(post("/shops/2/couponactivities/11/onsales/1").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"该优惠活动不属于该商店\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        adminToken = jwtHelper.createToken(1L,"admin",3L, 1, 3600);

        // onsale和店铺不对应
        responseString = mvc.perform(post("/shops/3/couponactivities/2/onsales/2").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"该Onsale不属于该商店\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        adminToken = jwtHelper.createToken(1L,"admin",2L, 1, 3600);

        // 数据库中已经有CouponOnsale表示该onsale已经参与了该活动
        responseString = mvc.perform(post("/shops/2/couponactivities/1/onsales/3912").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":507,\"errmsg\":\"该onsale已经参与了该活动\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        adminToken = jwtHelper.createToken(1L,"admin",2L, 1, 3600);

        // 当前状态不允许
        responseString = mvc.perform(post("/shops/2/couponactivities/1/onsales/2").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testDeleteCouponOnsale() throws Exception {
        adminToken = jwtHelper.createToken(1L,"admin",3L, 1, 3600);
        // 正常
        String responseString = mvc.perform(delete("/shops/3/coupononsale/5").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        adminToken = jwtHelper.createToken(1L,"admin",3L, 1, 3600);
        // 字段不合法
        responseString = mvc.perform(delete("/shops/3/coupononsale/0").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        adminToken = jwtHelper.createToken(1L,"admin",3L, 1, 3600);
        // CouponSale不存在
        responseString = mvc.perform(delete("/shops/3/coupononsale/10000").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        adminToken = jwtHelper.createToken(1L,"admin",3L, 1, 3600);
        // 活动不存在
        responseString = mvc.perform(delete("/shops/3/coupononsale/3347").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        adminToken = jwtHelper.createToken(1L,"admin",1L, 1, 3600);
        // 该CouponOnSale参与的优惠活动不属于该商店
        responseString = mvc.perform(delete("/shops/1/coupononsale/12").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"该CouponOnSale参与的优惠活动不属于该商店\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        adminToken = jwtHelper.createToken(1L,"admin",2L, 1, 3600);
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
        adminToken = jwtHelper.createToken(1L,"admin",2L, 1, 3600);
        // 正常
        String responseString = mvc.perform(put("/shops/2/couponactivities/1/online").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        adminToken = jwtHelper.createToken(1L,"admin",3L, 1, 3600);
        // 字段不合法
        responseString = mvc.perform(put("/shops/3/couponactivities/-1/online").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        adminToken = jwtHelper.createToken(1L,"admin",2L, 1, 3600);
        // 活动不存在
        responseString = mvc.perform(put("/shops/2/couponactivities/15/online").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        adminToken = jwtHelper.createToken(1L,"admin",3L, 1, 3600);
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

        adminToken = jwtHelper.createToken(1L,"admin",3L, 1, 3600);
        // 正常
        String responseString = mvc.perform(put("/shops/3/couponactivities/2/offline").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);


        adminToken = jwtHelper.createToken(1L,"admin",1L, 1, 3600);
        // 字段不合法
        responseString = mvc.perform(put("/shops/1/couponactivities/-1/offline").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        adminToken = jwtHelper.createToken(1L,"admin",2L, 1, 3600);
        // 处于下线态
        responseString = mvc.perform(put("/shops/2/couponactivities/1/offline").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
}
