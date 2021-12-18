package cn.edu.xmu.oomall.activity.controller;

import cn.edu.xmu.oomall.activity.ActivityApplication;
import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.microservice.ShopService;
import cn.edu.xmu.oomall.activity.microservice.vo.*;
import cn.edu.xmu.oomall.activity.model.bo.AdvanceSale;
import cn.edu.xmu.oomall.activity.model.vo.SimpleUserRetVo;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Jiawei Zheng
 * @date 2021-11-27
 */

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

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final Locale LOCALE=Locale.CHINA;

    private DateTimeFormatter df;
    private static String adminToken;
    JwtHelper jwtHelper = new JwtHelper();

    List<SimpleOnSaleInfoVo> list1 = new ArrayList<>();
    List<SimpleOnSaleInfoVo> list2 = new ArrayList<>();
    List<FullOnSaleVo> list3 = new ArrayList<>();
    List<FullOnSaleVo> list4 = new ArrayList<>();
    @BeforeEach
    public void init() {
        df = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT, LOCALE);
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);

        SimpleOnSaleInfoVo vo1=new SimpleOnSaleInfoVo();
        vo1.setActivityId(2L);
        vo1.setBeginTime(ZonedDateTime.parse("2021-06-21T17:38:20.000+08:00"));
        vo1.setEndTime(ZonedDateTime.parse("2021-12-29T17:38:20.000+08:00"));
        list1.add(vo1);

        SimpleOnSaleInfoVo vo2=new SimpleOnSaleInfoVo();
        vo2.setId(3L);
        vo2.setActivityId(11L);
        list2.add(vo2);

        FullOnSaleVo vo3=new FullOnSaleVo(3L,new SimpleShopVo(4L,"努力向前"),new ProductVo(1L,"算法书","helloworld"),
                20L,ZonedDateTime.parse("2021-06-21T17:38:20.001+08:00"),
                ZonedDateTime.parse("2021-12-29T17:38:20.001+08:00"),
                10L,(byte)3,1L,1L,new SimpleUserRetVo(1L,"zheng5d"),
                ZonedDateTime.parse("2021-06-21T17:38:20.000+08:00"),ZonedDateTime.parse("2021-06-21T17:38:20.000+08:00"),
                new SimpleUserRetVo(1L,"zheng5d"),(byte)1);
        list3.add(vo3);

        FullOnSaleVo vo4=new FullOnSaleVo(3L,new SimpleShopVo(4L,"努力向前"),new ProductVo(1L,"算法书","helloworld"),
                20L,ZonedDateTime.parse("2021-06-21T17:38:20.000+08:00"),
                ZonedDateTime.parse("2021-12-29T17:38:20.000+08:00"),
                10L,(byte)3,11L,1L,new SimpleUserRetVo(1L,"zheng5d"),
                ZonedDateTime.parse("2021-06-21T17:38:20.000+08:00"),ZonedDateTime.parse("2021-06-21T17:38:20.000+08:00"),
                new SimpleUserRetVo(1L,"zheng5d"),(byte)1);
        list4.add(vo4);
    }

    @Test
    @Transactional
    public void getAdvanceSaleStatesTest() throws Exception {
        String responseString = mvc.perform(get("/advancesales/states"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"草稿\"},{\"code\":1,\"name\":\"上线\"},{\"code\":2,\"name\":\"下线\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    //1.不添加任何查询条件
    @Test
    @Transactional
    public void getAllOnlineAdvanceSaleTest1() throws Exception {
        String responseString = this.mvc.perform(get("/advancesales"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":6,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"预售活动2\"},{\"id\":5,\"name\":\"预售活动5\"},{\"id\":7,\"name\":\"预售活动7\"},{\"id\":8,\"name\":\"预售活动8\"},{\"id\":9,\"name\":\"预售活动9\"},{\"id\":10,\"name\":\"预售活动10\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //2.根据productId,beginTime,endTime查询
    @Test
    @Transactional
    public void getAllOnlineAdvanceSaleTest2() throws Exception {
        InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> listReturnObject = new InternalReturnObject<>(new PageInfo<>(list1));
        Mockito.when(goodsService.getOnSales(null,1L,LocalDateTime.parse("2021-06-21T17:38:20.000Z",df),LocalDateTime.parse("2021-12-29T17:38:20.000Z",df),1,1)).thenReturn(listReturnObject);
        String responseString = mvc.perform(get("/advancesales?productId=1&beginTime=2021-06-21T17:38:20.000+08:00&endTime=2021-12-29T17:38:20.000+08:00")
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isOk()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"预售活动2\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    // 3.根据shopId查询(shopId存在)
    @Test
    @Transactional
    public void getAllOnlineAdvanceSaleTest3() throws Exception {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"OOMALL自营商铺")));
        String responseString = mvc.perform(get("/advancesales?shopId=1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isOk()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":8,\"name\":\"预售活动8\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    // 4.根据shopId查询(shopId不存在)
    @Test
    @Transactional
    public void getAllOnlineAdvanceSaleTest4() throws Exception {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST.getCode(),"找不到该商铺"));
        String responseString = mvc.perform(get("/advancesales?shopId=1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isNotFound()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":504,\"errmsg\":\"不存在该商铺\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //5.输入BeginTime大于endTime
    @Test
    @Transactional
    public void getAllOnlineAdvanceSaleTest5() throws Exception {
        String responseString = mvc.perform(get("/advancesales?beginTime=2021-12-29T17:38:20.000+08:00&endTime=2021-06-21T17:38:20.000+08:00")
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isBadRequest()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //6.根据productId,beginTime,endTime查询，没有查到
    @Test
    @Transactional
    public void getAllOnlineAdvanceSaleTest6() throws Exception {
        InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> listReturnObject = new InternalReturnObject<>(new PageInfo<>(list2));
        Mockito.when(goodsService.getOnSales(null,1L,LocalDateTime.parse("2021-06-21T17:38:20.000Z",df),LocalDateTime.parse("2021-12-29T17:38:20.000Z",df),1,1)).thenReturn(listReturnObject);
        String responseString = mvc.perform(get("/advancesales?productId=1&beginTime=2021-06-21T17:38:20.000+08:00&endTime=2021-12-29T17:38:20.000+08:00")
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isOk()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":10,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 查询上线预售活动的详细信息
     *
     * @throws Exception
     */
    //1.查询activityid为2的预售活动的详细信息，成功查到
    @Test
    @Transactional
    public void getOnlineAdvanceSaleInfoTest1() throws Exception {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(shopService.getShopInfo(5L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(5L,"坚持就是胜利")));
        InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> pageInfoReturnObject=new InternalReturnObject<>(new PageInfo<>(list1));
        Mockito.when(goodsService.getShopOnSaleInfo(5L,2L,null,null,null,1,10)).thenReturn(pageInfoReturnObject);
        InternalReturnObject<FullOnSaleVo> returnObject=new InternalReturnObject<>(list3.get(0));
        Mockito.when(goodsService.getOnSaleById(pageInfoReturnObject.getData().getList().get(0).getId())).thenReturn(returnObject);
        String responseString = mvc.perform(get("/advancesales/2"))
                .andExpect((status().isOk()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"id\":2,\"name\":\"预售活动2\",\"shop\":{\"id\":4,\"name\":\"努力向前\"},\"product\":{\"productId\":1,\"name\":\"算法书\",\"imageUrl\":\"helloworld\"},\"payTime\":\"2021-11-12T15:04:04.000\",\"beginTime\":\"2021-06-21T17:38:20.001\",\"endTime\":\"2021-12-29T17:38:20.001\",\"price\":20,\"quantity\":10,\"advancePayPrice\":100},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //2.查询activityid为1的预售活动的详细信息，OnSale中找不到
    @Test
    @Transactional
    public void getOnlineAdvanceSaleInfoTest2() throws Exception {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        InternalReturnObject<PageInfo<FullOnSaleVo>>pageInfoReturnObject=new InternalReturnObject(new PageInfo<>());
        Mockito.when(goodsService.getShopOnSaleInfo(4L,11L,null,null,null,1,10)).thenReturn(pageInfoReturnObject);
        String responseString = mvc.perform(get("/advancesales/11"))
                .andExpect((status().isNotFound()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":504,\"errmsg\":\"活动不存在\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //3.查询activityid为1的预售活动的详细信息，OnSale表查到，advanceSale表查不到
    @Test
    @Transactional
    public void getOnlineAdvanceSaleInfoTest3() throws Exception {
        InternalReturnObject<PageInfo<FullOnSaleVo>>pageInfoReturnObject=new InternalReturnObject(new PageInfo<>(list4));
        Mockito.when(goodsService.getShopOnSaleInfo(4L,11L,null,null,null,1,10)).thenReturn(pageInfoReturnObject);
        String responseString = mvc.perform(get("/advancesales/11"))
                .andExpect((status().isNotFound()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":504,\"errmsg\":\"活动不存在\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //4.查询activityid为1的预售活动的详细信息，在redis中查到advancesale，该活动已下线
    @Test
    public void getOnlineAdvanceSaleInfoTest4() throws Exception {
        //模拟访问redis拿到一个未上线的预售活动bo
        AdvanceSale advanceSale=new AdvanceSale(11L,4L,"努力向前","预售活动11",ZonedDateTime.parse("2021-06-22T17:38:20.000+08:00"),100L,1L,"zheng5d",1L,"zheng5d",ZonedDateTime.parse("2021-06-22T17:38:20.000+08:00"),ZonedDateTime.parse("2021-06-22T17:38:20.000+08:00"),Byte.valueOf("2"));
        Mockito.when(redisUtil.get("advanceSale_11")).thenReturn(advanceSale);
        InternalReturnObject<PageInfo<FullOnSaleVo>>pageInfoReturnObject=new InternalReturnObject(new PageInfo<>(list3));
        Mockito.when(goodsService.getShopOnSaleInfo(4L,11L,null,null,null,1,10)).thenReturn(pageInfoReturnObject);

        String responseString = mvc.perform(get("/advancesales/11"))
                .andExpect((status().isOk()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":507,\"errmsg\":\"预售活动未上线\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //5.查询activityId为2的预售活动，假设查找OnSale表时找不到
    @Test
    @Transactional
    public void getOnlineAdvanceSaleInfoTest5() throws Exception {
        InternalReturnObject<PageInfo<FullOnSaleVo>>pageInfoReturnObject=new InternalReturnObject(new PageInfo<>());
        Mockito.when(goodsService.getShopOnSaleInfo(5L,2L,null,null,null,1,10)).thenReturn(pageInfoReturnObject);
        String responseString = mvc.perform(get("/advancesales/2"))
                .andExpect((status().isNotFound()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":504,\"errmsg\":\"找不到该预售活动对应的销售信息\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }


    //1.根据shopId,productId,beginTime,endTime查询
    @Test
    @Transactional
    public void getShopAdvanceSaleTest1() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",5L, 1,3600);
        Mockito.when(shopService.getShopInfo(5L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(5L,"坚持就是胜利")));
        InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> listReturnObject = new InternalReturnObject<>(new PageInfo<>(list1));
        Mockito.when(goodsService.getOnSales(5L,1552L,LocalDateTime.parse("2021-06-21T17:38:20.000Z",df),LocalDateTime.parse("2021-12-29T17:38:20.000Z",df),1,1)).thenReturn(listReturnObject);
        String responseString = mvc.perform(get("/shops/5/advancesales?productId=1552&beginTime=2021-06-21T17:38:20.000+08:00&endTime=2021-12-29T17:38:20.000+08:00")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isOk()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"预售活动2\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    // 2.根据shopId查询(shopId不存在)
    @Test
    @Transactional
    public void getShopAdvanceSaleTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 1,3600);
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST.getCode(),"找不到该商铺"));
        String responseString = mvc.perform(get("/shops/1/advancesales")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isNotFound()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":504,\"errmsg\":\"不存在该商铺\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //3.输入BeginTime大于endTime不合法
    @Test
    @Transactional
    public void getShopAdvanceSaleTest3() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 1,3600);
        String responseString = mvc.perform(get("/shops/1/advancesales?beginTime=2021-12-29T17:38:20.000+08:00&endTime=2021-06-21T17:38:20.000+08:00")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isBadRequest()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 管理员新增预售
     */
    //1.商铺销售时间冲突
    @Test
    @Transactional
    public void addAdvanceSaleTest1() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",4L, 1,3600);
        Mockito.when(shopService.getShopInfo(4L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"努力向前")));
        InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> listReturnObject = new InternalReturnObject<>(new PageInfo<>(list1));
        Mockito.when(goodsService.getOnSales(4L,1552L,LocalDateTime.parse("2021-06-21T17:38:20.000Z",df),LocalDateTime.parse("2021-12-29T17:38:20.000Z",df),1,1)).thenReturn(listReturnObject);
        String requestJson="{\"price\": 156,\"beginTime\": \"2021-06-21T17:38:20.000+08:00\",\"endTime\": \"2021-12-29T17:38:20.000+08:00\",\"quantity\": 2,\"name\": \"预售活动11\",\"payTime\": \"2021-06-22T17:38:20.000+08:00\",\"advancePayPrice\": 140}";
        String responseString = mvc.perform(post("/shops/4/products/1552/advanceSale")
                .header("authorization", adminToken)
                .content(requestJson)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isOk()))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":902,\"errmsg\":\"商品销售时间冲突\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //2.新建预售,输入参数不合法
    @Test
    @Transactional
    public void addAdvanceSaleTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",4L, 1,3600);
        String requestJson="{\"price\": -1,\"beginTime\": \"2021-06-21T17:38:20.000+08:00\",\"endTime\": \"2021-12-29T17:38:20.000+08:00\",\"quantity\": 2,\"name\": \"预售活动11\",\"payTime\": \"2021-06-22T17:38:20.000+08:00\",\"advancePayPrice\": 140}";
        String responseString = mvc.perform(post("/shops/4/products/1552/advanceSale")
                .header("authorization", adminToken)
                .content(requestJson)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isBadRequest()))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":503,\"errmsg\":\"must be greater than or equal to 0;\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //3.新建预售,开始时间晚于结束时间
    @Test
    @Transactional
    public void addAdvanceSaleTest3() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",4L, 1,3600);
        String requestJson="{\"price\": 156,\"beginTime\": \"2021-12-29T17:38:20.000+08:00\",\"endTime\":\"2021-06-21T17:38:20.000+08:00\" ,\"quantity\": 2,\"name\": \"预售活动11\",\"payTime\": \"2021-06-22T17:38:20.000+08:00\",\"advancePayPrice\": 140}";
        String responseString = mvc.perform(post("/shops/4/products/1552/advanceSale")
                .header("authorization", adminToken)
                .content(requestJson)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isBadRequest()))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //4.新建预售,支付尾款时间晚于活动结束时间
    @Test
    @Transactional
    public void addAdvanceSaleTest4() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",4L, 1,3600);
        String requestJson="{\"price\": 156,\"beginTime\": \"2021-06-21T17:38:20.000+08:00\",\"endTime\":\"2021-12-29T17:38:20.000+08:00\" ,\"quantity\": 2,\"name\": \"预售活动11\",\"payTime\": \"2023-06-22T17:38:20.000+08:00\",\"advancePayPrice\": 140}";
        String responseString = mvc.perform(post("/shops/4/products/1552/advanceSale")
                .header("authorization", adminToken)
                .content(requestJson)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isBadRequest()))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":948,\"errmsg\":\"尾款支付时间晚于活动结束时间\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //5.新建预售,支付尾款时间晚于活动结束时间
    @Test
    @Transactional
    public void addAdvanceSaleTest5() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",4L, 1,3600);
        String requestJson="{\"price\": 156,\"beginTime\": \"2021-06-21T17:38:20.000+08:00\",\"endTime\":\"2021-12-29T17:38:20.000+08:00\" ,\"quantity\": 2,\"name\": \"预售活动11\",\"payTime\": \"2020-06-22T17:38:20.000+08:00\",\"advancePayPrice\": 140}";
        String responseString = mvc.perform(post("/shops/4/products/1552/advanceSale")
                .header("authorization", adminToken)
                .content(requestJson)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isBadRequest()))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":949,\"errmsg\":\"尾款支付时间早于于活动开始时间\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //6.新增预售活动，但新增到OnSale表时失败
    @Test
    @Transactional
    public void addAdvanceSaleTest6() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",4L, 1,3600);
        Mockito.when(shopService.getShopInfo(4L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"努力向前")));
        InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> listReturnObject = new InternalReturnObject<>(new PageInfo<>(list1));
        Mockito.when(goodsService.addOnSale(4L,1552L,new OnSaleCreatedVo(156L,LocalDateTime.parse("2022-06-21T17:38:20.000Z",df),LocalDateTime.parse("2022-12-29T17:38:20.000Z",df),2L,Byte.valueOf("3"),null))).thenReturn(new InternalReturnObject());
        Mockito.when(goodsService.getOnSales(4L,1552L,LocalDateTime.parse("2022-06-21T17:38:20.000Z",df),LocalDateTime.parse("2022-12-29T17:38:20.000Z",df),1,1)).thenReturn(listReturnObject);
        String requestJson="{\"price\": 156,\"beginTime\": \"2022-06-21T17:38:20.000+08:00\",\"endTime\": \"2022-12-29T17:38:20.000+08:00\",\"quantity\": 2,\"name\": \"预售活动11\",\"payTime\": \"2022-06-22T17:38:20.000+08:00\",\"advancePayPrice\": 140}";
        String responseString = mvc.perform(post("/shops/4/products/1552/advanceSale")
                .header("authorization", adminToken)
                .content(requestJson)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isInternalServerError()))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":500}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    //7.新增预售活动，且新增到Onsale表成功
    @Test
    @Transactional
    public void addAdvanceSaleTest7() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",4L, 1,3600);
        Mockito.when(shopService.getShopInfo(4L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"努力向前")));
        InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> listReturnObject = new InternalReturnObject<>(new PageInfo<>(list1));
        Mockito.when(goodsService.addOnSale(4L,1552L,new OnSaleCreatedVo(156L,LocalDateTime.parse("2022-06-21T17:38:20.000Z",df),LocalDateTime.parse("2022-12-29T17:38:20.000Z",df),2L,Byte.valueOf("3"),null)))
                .thenReturn(new InternalReturnObject(new OnSaleCreatedVo(156L,LocalDateTime.parse("2022-06-21T17:38:20.000Z",df),LocalDateTime.parse("2022-12-29T17:38:20.000Z",df),2L,Byte.valueOf("3"),11L)));
        Mockito.when(goodsService.getOnSales(4L,1552L,LocalDateTime.parse("2022-06-21T17:38:20.000Z",df),LocalDateTime.parse("2022-12-29T17:38:20.000Z",df),1,1)).thenReturn(listReturnObject);
        String requestJson="{\"price\": 156,\"beginTime\": \"2022-06-21T17:38:20.000+08:00\",\"endTime\": \"2022-12-29T17:38:20.000+08:00\",\"quantity\": 2,\"name\": \"预售活动11\",\"payTime\": \"2022-06-22T17:38:20.000+08:00\",\"advancePayPrice\": 140}";
        String responseString = mvc.perform(post("/shops/4/products/1552/advanceSale")
                .header("authorization", adminToken)
                .content(requestJson)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isOk()))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"name\":\"预售活动11\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    //8.新建预售,shop不存在
    @Test
    @Transactional
    public void addAdvanceSaleTest8() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",11L, 1,3600);
        Mockito.when(shopService.getShopInfo(11L)).thenReturn(new InternalReturnObject<>());
        String requestJson="{\"price\": 1,\"beginTime\": \"2021-06-21T17:38:20.000+08:00\",\"endTime\": \"2021-12-29T17:38:20.000+08:00\",\"quantity\": 2,\"name\": \"预售活动11\",\"payTime\": \"2021-06-22T17:38:20.000+08:00\",\"advancePayPrice\": 140}";
        String responseString = mvc.perform(post("/shops/11/products/1552/advanceSale")
                .header("authorization", adminToken)
                .content(requestJson)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isNotFound()))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":504,\"errmsg\":\"不存在该商铺\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 管理员查询商铺的特定预售活动
     * @throws Exception
     */
    //1.查询shopId为4,activityid为1的预售活动，成功查到
    @Test
    public void getShopAdvanceSaleInfoTest1() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",4L, 1,3600);
        Mockito.when(shopService.getShopInfo(4L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"OOMALL自营商铺")));
        InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> pageInfoReturnObject=new InternalReturnObject<>(new PageInfo<>(list1));
        Mockito.when(goodsService.getShopOnSaleInfo(4L,1L,null,null,null,1,10)).thenReturn(pageInfoReturnObject);
        InternalReturnObject<FullOnSaleVo> returnObject=new InternalReturnObject<>(list3.get(0));
        Mockito.when(goodsService.getOnSaleById(pageInfoReturnObject.getData().getList().get(0).getId())).thenReturn(returnObject);
        String responseString = mvc.perform(get("/shops/4/advancesales/1")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isOk()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"id\":1,\"name\":\"预售活动1\",\"shop\":{\"id\":4,\"name\":\"努力向前\"},\"product\":{\"productId\":1,\"name\":\"算法书\",\"imageUrl\":\"helloworld\"},\"payTime\":\"2021-11-12T15:04:04.000+08:00\",\"beginTime\":\"2021-06-21T17:38:20.001+08:00\",\"endTime\":\"2021-12-29T17:38:20.001+08:00\",\"price\":20,\"quantity\":10,\"advancePayPrice\":100,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2021-11-11T15:04:04.000+08:00\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null},\"state\":2},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //2.查询shopId为11,activityid为1的预售活动，shopId为空
    @Test
    public void getShopAdvanceSaleInfoTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",11L, 1,3600);
        Mockito.when(shopService.getShopInfo(11L)).thenReturn(new InternalReturnObject<>());
        String responseString = mvc.perform(get("/shops/11/advancesales/1")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isNotFound()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":504,\"errmsg\":\"不存在该商铺\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //3.查询shopId为4,activityid为2的预售活动，活动id和店铺不匹配，getShopOnsaleInfo查不到
    @Test
    public void getShopAdvanceSaleInfoTest3() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",4L, 1,3600);
        Mockito.when(shopService.getShopInfo(4L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"OOMALL自营商铺")));
        InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> pageInfoReturnObject1=new InternalReturnObject<>(new PageInfo<>());
        Mockito.when(goodsService.getShopOnSaleInfo(4L,2L,null,null,null,1,10)).thenReturn(pageInfoReturnObject1);
        String responseString = mvc.perform(get("/shops/4/advancesales/2")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isNotFound()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":504,\"errmsg\":\"找不到该预售活动对应的销售信息\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    //4.查询shopId为4,activityid为11的预售活动，假设OnSale找得到，AdvanceSale找不到，会在dao层出错
    @Test
    public void getShopAdvanceSaleInfoTest4() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",4L, 1,3600);
        Mockito.when(shopService.getShopInfo(4L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"OOMALL自营商铺")));
        InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> pageInfoReturnObject1=new InternalReturnObject<>(new PageInfo<>(list2));
        Mockito.when(goodsService.getShopOnSaleInfo(4L,11L,null,null,null,1,10)).thenReturn(pageInfoReturnObject1);
        InternalReturnObject<FullOnSaleVo> returnObject=new InternalReturnObject<>(list3.get(0));
        Mockito.when(goodsService.getOnSaleById(pageInfoReturnObject1.getData().getList().get(0).getId())).thenReturn(returnObject);
        String responseString = mvc.perform(get("/shops/4/advancesales/11")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isNotFound()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":504,\"errmsg\":\"活动不存在\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }
}
