package cn.edu.xmu.oomall.coupon.controller;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.CouponApplication;
import cn.edu.xmu.oomall.coupon.microservice.GoodsService;
import cn.edu.xmu.oomall.coupon.microservice.vo.ProductRetVo;
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

    @Test
    @Transactional
    public void calculateDiscount() throws Exception {
        ProductRetVo productRetVo1=new ProductRetVo();
        Map<String,Object>category1=new HashMap<>();
        category1.put("id",266L);
        productRetVo1.setCategory(category1);
        productRetVo1.setOnSaleId(12L);

        ProductRetVo productRetVo2=new ProductRetVo();
        Map<String,Object>category2=new HashMap<>();
        category2.put("id",212L);
        productRetVo2.setCategory(category2);
        productRetVo2.setOnSaleId(31L);
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(goodsService.getProductById(1561L)).thenReturn(new ReturnObject<>(productRetVo1));
        Mockito.when(goodsService.getProductById(1580L)).thenReturn(new ReturnObject<>(productRetVo2));
        String requestJson="[{\"productId\": 1561,\"onsaleId\": 12,\"quantity\": 5,\"originalPrice\": 20,\"activityId\": 7}," +
                "{\"productId\": 1580,\"onsaleId\": 31,\"quantity\": 5,\"originalPrice\": 10,\"activityId\": 7}]";
        String responseJson=this.mvc.perform(put("/internal/discountprices")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":[{\"productId\":1561,\"onsaleId\":12,\"discountPrice\":16,\"activityId\":7},{\"productId\":1580,\"onsaleId\":31,\"discountPrice\":8,\"activityId\":7}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }


    @Test
    @Transactional
    public void calculateDiscountBest() throws Exception {
        ProductRetVo productRetVo1=new ProductRetVo();
        Map<String,Object>category1=new HashMap<>();
        category1.put("id",266L);
        productRetVo1.setCategory(category1);
        productRetVo1.setOnSaleId(12L);

        ProductRetVo productRetVo2=new ProductRetVo();
        Map<String,Object>category2=new HashMap<>();
        category2.put("id",212L);
        productRetVo2.setCategory(category2);
        productRetVo2.setOnSaleId(31L);

        ProductRetVo productRetVo3=new ProductRetVo();
        Map<String,Object>category3=new HashMap<>();
        category3.put("id",260L);
        productRetVo3.setCategory(category3);
        productRetVo3.setOnSaleId(160L);

        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(goodsService.getProductById(1561L)).thenReturn(new ReturnObject<>(productRetVo1));
        Mockito.when(goodsService.getProductById(1580L)).thenReturn(new ReturnObject<>(productRetVo2));
        Mockito.when(goodsService.getProductById(1709L)).thenReturn(new ReturnObject<>(productRetVo3));
        String requestJson="[{\"productId\": 1561,\"onsaleId\": 12,\"quantity\": 5,\"originalPrice\": 20,\"activityId\": null}," +
                "{\"productId\": 1580,\"onsaleId\": 31,\"quantity\": 5,\"originalPrice\": 10,\"activityId\": null},"+
                "{\"productId\": 1709,\"onsaleId\": 160,\"quantity\": 5,\"originalPrice\": 41686,\"activityId\": null}]";
        String responseJson=this.mvc.perform(put("/internal/discountprices/best")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":[{\"productId\":1561,\"onsaleId\":12,\"discountPrice\":20,\"activityId\":7},{\"productId\":1580,\"onsaleId\":31,\"discountPrice\":10,\"activityId\":7},{\"productId\":1709,\"onsaleId\":160,\"discountPrice\":41681,\"activityId\":7}],\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }
}
