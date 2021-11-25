package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.privilegegateway.util.JwtHelper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/13 06:06
 **/
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OnSaleGetControllerTest {
    private static JwtHelper jwtHelper = new JwtHelper();
    private static String adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);

    @Autowired
    private MockMvc mvc;

    @Test
    @Transactional
    public void selectCertainOnsale() throws Exception {
        //正常情况
        String responseJson=this.mvc.perform(get("/shops/10/products/1550/onsales?page=1&pageSize=10")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"price\":53295,\"beginTime\":\"2021-11-11 14:38:20.000\",\"endTime\":\"2022-02-19 14:38:20.000\",\"quantity\":26,\"activityId\":null,\"shareActId\":null,\"type\":0}]},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void selectOnsale() throws Exception {
        //正常情况
        String responseJson=this.mvc.perform(get("/shops/8/onsales/5")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":{\"id\":5,\"price\":3280,\"beginTime\":\"2021-11-11 14:38:20.000\",\"endTime\":\"2022-02-19 14:38:20.000\",\"quantity\":67},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void selectOnsale_wrongType() throws Exception {
        //正常情况
        String responseJson=this.mvc.perform(get("/shops/5/onsales/6")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":403,\"errmsg\":\"当前类型禁止此操作\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void selectOnsale_noResource() throws Exception {
        String responseJson=this.mvc.perform(get("/shops/8/onsales/5000")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void selectActivities() throws Exception {
        //正常情况
        String responseJson=this.mvc.perform(get("/internal/shops/5/activities/3/onsales?state=1&beginTime=2021-11-10 14:38:20&endTime=2022-02-20 14:38:20&page&pageSize")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":18,\"price\":56179,\"beginTime\":\"2021-11-11 14:38:20.000\",\"endTime\":\"2022-02-19 14:38:20.000\",\"quantity\":96,\"activityId\":3,\"shareActId\":3,\"type\":3}]},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void selectActivities_fieldNotValid() throws Exception {
        //state不合法
        String responseJson=this.mvc.perform(get("/internal/shops/5/activities/3/onsales?state=10&beginTime&endTime&page&pageSize")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void selectActivities_beginLaterEnd() throws Exception {
        //开始时间晚于结束时间
        String responseJson=this.mvc.perform(get("/internal/shops/5/activities/3/onsales")
                .param("state","")
                .param("beginTime","2021-11-11 14:38:20")
                .param("endTime","2021-11-01 14:38:20")
                .param("page","1")
                .param("pageSize","10")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void selectShare() throws Exception {
        //正常情况
        String responseJson=this.mvc.perform(get("/internal/shops/2/shareactivities/7/onsales?state=1&page=1&pageSize=10")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":33,\"price\":6035,\"beginTime\":\"2021-11-11 14:38:20.000\",\"endTime\":\"2022-02-19 14:38:20.000\",\"quantity\":21,\"activityId\":9,\"shareActId\":7,\"type\":3}]},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void selectShare_fieldNotValid() throws Exception {
        //正常情况
        String responseJson=this.mvc.perform(get("/internal/shops/2/shareactivities/9/onsales?state=10&page=1&pageSize=10")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void selectFullOnsales() throws Exception {
        //正常情况
        String responseJson=this.mvc.perform(get("/internal/onsales/1")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":{\"id\":1,\"price\":53295,\"quantity\":26,\"beginTime\":\"2021-11-11T14:38:20\",\"endTime\":\"2022-02-19T14:38:20\",\"type\":0,\"activityId\":null,\"gmtCreate\":\"2021-11-11T14:38:20\",\"gmtModified\":null,\"product\":{\"id\":1550,\"name\":\"欢乐家久宝桃罐头\",\"imageUrl\":null},\"shop\":null,\"shareAct\":null,\"createdBy\":null,\"modifiedBy\":null},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void selectAnyOnsale() throws Exception {
        //正常情况
        String responseJson=this.mvc.perform(get("/internal/onsales?shopId=2&productId=1558&beginTime=2021-11-10 14:38:20&endTime=2022-02-20 14:38:20&page=1&pageSize=10")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":9,\"price\":6985,\"beginTime\":\"2021-11-11 14:38:20.000\",\"endTime\":\"2022-02-19 14:38:20.000\",\"quantity\":78,\"activityId\":4,\"shareActId\":1,\"type\":3}]},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void selectAnyOnsale_beginLaterEnd() throws Exception {
        String responseJson=this.mvc.perform(get("/internal/onsales")
                .param("shopId","")
                .param("productId","")
                .param("beginTime","2021-11-11 14:38:20")
                .param("endTime","2021-11-01 14:38:20")
                .param("page","1")
                .param("pageSize","10")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }
}
