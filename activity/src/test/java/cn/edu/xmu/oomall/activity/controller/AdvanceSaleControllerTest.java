package cn.edu.xmu.oomall.activity.controller;

import cn.edu.xmu.oomall.activity.ActivityApplication;
import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.microservice.ShopService;
import cn.edu.xmu.oomall.activity.microservice.vo.OnSaleInfoVo;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleOnSaleInfoVo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.util.JwtHelper;
import cn.edu.xmu.privilegegateway.util.RedisUtil;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ActivityApplication.class)
@AutoConfigureMockMvc
@Rollback(value = true)
public class AdvanceSaleControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RedisUtil redisUtil;

    @MockBean(name = "cn.edu.xmu.oomall.activity.microservice.ShopService")
    private ShopService shopService;

    @MockBean(name = "cn.edu.xmu.oomall.activity.microservice.GoodsService")
    private GoodsService goodsService;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final Locale LOCALE=Locale.CHINA;

    private DateTimeFormatter df;
    private static String adminToken;
    JwtHelper jwtHelper = new JwtHelper();

    @BeforeEach
    public void init() {
        df = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT, LOCALE);
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);

        List<SimpleOnSaleInfoVo> list = new ArrayList<>();
    }

    @Test
    public void getAdvanceSaleStatesTest() throws Exception {
        String responseString = mvc.perform(get("/advancesales/states"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"草稿\"},{\"code\":1,\"name\":\"下线\"},{\"code\":2,\"name\":\"上线\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    //1.不添加任何查询条件
    @Test
    public void getAllOnlineAdvanceSale1() throws Exception {
        String responseString = this.mvc.perform(get("/advancesales"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":10,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"name\":\"预售活动1\"},{\"id\":2,\"name\":\"预售活动2\"},{\"id\":3,\"name\":\"预售活动3\"},{\"id\":4,\"name\":\"预售活动4\"},{\"id\":5,\"name\":\"预售活动5\"},{\"id\":6,\"name\":\"预售活动6\"},{\"id\":7,\"name\":\"预售活动7\"},{\"id\":8,\"name\":\"预售活动8\"},{\"id\":9,\"name\":\"预售活动9\"},{\"id\":10,\"name\":\"预售活动10\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

//    //2.根据productId查询
//    @Test
//    public void getAllOnlineAdvanceSale2() throws Exception {
//        ReturnObject<PageInfo<SimpleOnSaleInfoVo>> listReturnObject = AdvanceSaleCreateObject.createSimpleOnSaleVoByProductAndTime(1L,null,null);
//        Mockito.when(goodsService.getAllOnsale(1L,null,null,null,1,10)).thenReturn(listReturnObject);
//        String responseString = mvc.perform(get("/advancesales")
//                .queryParam("productId", "1")
//                .queryParam("page", "1")
//                .queryParam("pageSize", "10")
//                .contentType("application/json;charset=UTF-8"))
//                .andExpect((status().isOk()))
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expected = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"name\":\"预售活动1\"},{\"id\":2,\"name\":\"预售活动2\"}]},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expected, responseString, true);
//    }

//    //3.根据beginTime,endTime查询
//    @Test
//    public void getAllOnlineAdvanceSale3() throws Exception {
//        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
//        ReturnObject<PageInfo<SimpleOnSaleInfoVo>> listReturnObject = AdvanceSaleCreateObject.createSimpleOnSaleVoByProductAndTime(null,LocalDateTime.parse("2021-06-11 14:38:20.000",df),LocalDateTime.parse("2021-12-30 14:38:20.000",df));
//        Mockito.when(goodsService.getAllOnsale(null,null, LocalDateTime.parse("2021-06-11 14:38:20.000",df),LocalDateTime.parse("2021-12-30 14:38:20.000",df),1,10)).thenReturn(listReturnObject);
//        String responseString = mvc.perform(get("/advancesales")
//                .queryParam("beginTime", "2021-06-11 14:38:20.000")
//                .queryParam("endTime", "2021-12-30 14:38:20.000")
//                .contentType("application/json;charset=UTF-8"))
//                .andExpect((status().isOk()))
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expected = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"name\":\"预售活动1\"},{\"id\":2,\"name\":\"预售活动2\"}]},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expected, responseString, true);
//    }

//    // 4.根据shopId查询
//    @Test
//    public void getAllOnlineAdvanceSale4() throws Exception {
//        ReturnObject<ShopVo> shopVoReturnObject=  AdvanceSaleCreateObject.createShopVo(1L);
//        Mockito.when(shopService.getShop(1L)).thenReturn(shopVoReturnObject);
//        String responseString = mvc.perform(get("/advancesales")
//                .queryParam("shopId", "1")
//                .contentType("application/json;charset=UTF-8"))
//                .andExpect((status().isOk()))
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expected = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":8,\"name\":\"预售活动8\"}]},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expected, responseString, true);
//    }

    /**
     * 查询上线预售活动的详细信息
     *
     * @throws Exception
     */
//    //查询activityid为1的预售活动的详细信息
//    @Test
//    public void getAdvanceSaleDetails() throws Exception {
//        ReturnObject<PageInfo<OnSaleInfoVo>>pageInfoReturnObject=new ReturnObject(new PageInfo(new OnSaleInfoVo()));
//        Mockito.when(goodsService.getShopOnsaleInfo(1L,1L,null,null,null,1,10)).thenReturn(pageInfoReturnObject);
//        String responseString = mvc.perform(get("/advancesales/1"))
//                .andExpect((status().isOk()))
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expected = "{\"errno\":0,\"data\":{\"id\":1,\"name\":\"预售活动1\",\"shop\":{\"shopId\":1,\"name\":\"shop\"},\"product\":{\"productId\":1,\"name\":\"product\",\"imageUrl\":\"image\"},\"payTime\":\"2021-11-12T15:04:04\",\"beginTime\":\"2021-10-11T15:01:02\",\"endTime\":\"2021-12-11T15:01:02\",\"price\":123,\"quantity\":100,\"advancePayPrice\":100},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expected, responseString, true);
//    }

}
