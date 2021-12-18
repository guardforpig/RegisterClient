package cn.edu.xmu.oomall.coupon.controller;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.CouponApplication;
import cn.edu.xmu.oomall.coupon.microservice.GoodsService;
import cn.edu.xmu.oomall.coupon.microservice.vo.CategoryVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.ProductRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/11 17:06
 **/
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(classes = CouponApplication.class)
public class DiscountControllerTest {
    private static JwtHelper jwtHelper = new JwtHelper();
    private static String adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);

    @Autowired
    private MockMvc mvc;
    @MockBean
    private GoodsService goodsService;
    @MockBean
    private RedisUtil redisUtil;

    CategoryVo category1=new CategoryVo(266L,null);
    ProductRetVo productRetVo1=new ProductRetVo(1561L,null,null,12L,null,null,null,null,null,null,null,null,null,null,null,category1,null,null);

    CategoryVo category2=new CategoryVo(212L,null);
    ProductRetVo productRetVo2=new ProductRetVo(1580L,null,null,31L,null,null,null,null,null,null,null,null,null,null,null,category2,null,null);

    CategoryVo category3=new CategoryVo(260L,null);
    ProductRetVo productRetVo3=new ProductRetVo(1709L,null,null,160L,null,null,null,null,null,null,null,null,null,null,null,category3,null,null);

    CategoryVo category4=new CategoryVo(21L,null);
    ProductRetVo productRetVo4=new ProductRetVo(1570L,null,null,21L,null,null,null,11241L,null,null,null,null,null,null,null,category4,null,null);

    @Test
    @Transactional
    public void calculateDiscount_NoRedis() throws Exception {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(goodsService.getProductById(1561L)).thenReturn(new InternalReturnObject<>(productRetVo1));
        Mockito.when(goodsService.getProductById(1580L)).thenReturn(new InternalReturnObject<>(productRetVo2));

        String requestJson="[{\"productId\": 1561,\"onsaleId\": 12,\"quantity\": 1,\"originalPrice\": 3569,\"activityId\": 3}," +
                "{\"productId\": 1580,\"onsaleId\": 31,\"quantity\": 5,\"originalPrice\": 94,\"activityId\": 3}]";
        String responseJson=this.mvc.perform(put("/internal/discountprices")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":[{\"productId\":1561,\"onsaleId\":12,\"discountPrice\":3216,\"activityId\":3},{\"productId\":1580,\"onsaleId\":31,\"discountPrice\":85,\"activityId\":3}],\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void calculateDiscount_WrongOnsaleId() throws Exception {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(goodsService.getProductById(1561L)).thenReturn(new InternalReturnObject<>(productRetVo1));
        Mockito.when(goodsService.getProductById(1580L)).thenReturn(new InternalReturnObject<>(productRetVo2));

        String requestJson="[{\"productId\": 1561,\"onsaleId\": 10,\"quantity\": 5,\"originalPrice\": 20,\"activityId\": 7}," +
                "{\"productId\": 1580,\"onsaleId\": 31,\"quantity\": 5,\"originalPrice\": 94,\"activityId\": 7}]";
        String responseJson=this.mvc.perform(put("/internal/discountprices")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void calculateDiscount_NoResource() throws Exception {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(goodsService.getProductById(1561L)).thenReturn(new InternalReturnObject<>(productRetVo1));
        Mockito.when(goodsService.getProductById(1580L)).thenReturn(new InternalReturnObject<>(productRetVo2));

        String requestJson="[{\"productId\": 1561,\"onsaleId\": 12,\"quantity\": 5,\"originalPrice\": 20,\"activityId\": 700}," +
                "{\"productId\": 1580,\"onsaleId\": 31,\"quantity\": 5,\"originalPrice\": 94,\"activityId\": 7}]";
        String responseJson=this.mvc.perform(put("/internal/discountprices")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void calculateDiscountBest() throws Exception {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(goodsService.getProductById(1561L)).thenReturn(new InternalReturnObject<>(productRetVo1));
        Mockito.when(goodsService.getProductById(1580L)).thenReturn(new InternalReturnObject<>(productRetVo2));
        Mockito.when(goodsService.getProductById(1709L)).thenReturn(new InternalReturnObject<>(productRetVo3));

        String requestJson="[{\"productId\": 1561,\"onsaleId\": 12,\"quantity\": 1,\"originalPrice\": 3569}," +
                "{\"productId\": 1580,\"onsaleId\": 31,\"quantity\": 5,\"originalPrice\": 94},"+
                "{\"productId\": 1709,\"onsaleId\": 160,\"quantity\": 1,\"originalPrice\": 41686}]";
        String responseJson=this.mvc.perform(put("/internal/discountprices/best")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":[{\"productId\":1561,\"onsaleId\":12,\"discountPrice\":3486,\"activityId\":6},{\"productId\":1580,\"onsaleId\":31,\"discountPrice\":92,\"activityId\":6},{\"productId\":1709,\"onsaleId\":160,\"discountPrice\":40711,\"activityId\":6}],\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }
}
