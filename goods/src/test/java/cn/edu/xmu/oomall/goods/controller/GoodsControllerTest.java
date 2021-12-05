package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.goods.GoodsApplication;
import cn.edu.xmu.oomall.goods.microservice.ShopService;
import cn.edu.xmu.oomall.goods.microservice.vo.CategoryVo;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleShopVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.ResultActions;
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


    @MockBean
    private ShopService shopService;
    @BeforeEach
    public void init() {
        CategoryVo categoryVo1 = new CategoryVo();
        categoryVo1.setId(1L);
        categoryVo1.setPid(null);
        CategoryVo categoryVo2 = new CategoryVo();
        categoryVo2.setId(266L);
        categoryVo2.setPid(16L);
        SimpleShopVo simpleShopVo = new SimpleShopVo();
        simpleShopVo.setId(0L);
        simpleShopVo.setName("");


        Mockito.when(shopService.getCategoryById(1)).thenReturn(new InternalReturnObject(0, "", categoryVo1));
        Mockito.when(shopService.getCategoryById(266)).thenReturn(new InternalReturnObject(0, "", categoryVo2));
        Mockito.when(shopService.getCategoryById(3)).thenReturn(new InternalReturnObject(1, "", null));
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject(1, "", List.of(simpleShopVo)));
        Mockito.when(shopService.getShopInfo(2L)).thenReturn(new InternalReturnObject(1, "", List.of()));
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
    }

    @Test
    @Transactional(readOnly = true)
    public void secondProducts1() throws Exception {
        this.mockMvc.perform(get("/categories/1/products")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.errmsg").value("成功"));
    }
    @Test
    @Transactional(readOnly = true)
    public void secondProducts2() throws Exception {
        String contentAsString = this.mockMvc.perform(get("/categories/266/products")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"total\":1,\"list\":[{\"id\":1561,\"shopId\":1,\"shopName\":\"OOMALL自营商铺\",\"goodsId\":453,\"categoryId\":266,\"freightId\":1,\"skuSn\":null,\"name\":\"奥利奥（桶装）\",\"originalPrice\":69902,\"weight\":55,\"imageUrl\":null,\"barcode\":\"6901668053893\",\"unit\":\"桶\",\"originPlace\":\"江苏\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":3}],\"pageNum\":1,\"pageSize\":10,\"size\":1,\"startRow\":1,\"endRow\":1,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,contentAsString,true);
    }
    @Test
    @Transactional(readOnly = true)
    public void secondProducts3() throws Exception {
        String contentAsString = this.mockMvc.perform(get("/categories/3/products")
                        .header("authorization", adminToken))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":504,\"errmsg\":\"分类id不存在\"}";
        JSONAssert.assertEquals(expected,contentAsString,true);
    }
    @Test
    @Transactional(readOnly = true)
    public void secondShopProducts1() throws Exception {
        String contentAsString = this.mockMvc.perform(get("/shops/1/categories/266/products")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"total\":6,\"list\":[{\"id\":1561,\"shopId\":1,\"shopName\":\"OOMALL自营商铺\",\"goodsId\":453,\"categoryId\":266,\"freightId\":1,\"skuSn\":null,\"name\":\"奥利奥（桶装）\",\"originalPrice\":69902,\"weight\":55,\"imageUrl\":null,\"barcode\":\"6901668053893\",\"unit\":\"桶\",\"originPlace\":\"江苏\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":3},{\"id\":1971,\"shopId\":1,\"shopName\":\"OOMALL自营商铺\",\"goodsId\":281,\"categoryId\":266,\"freightId\":1,\"skuSn\":null,\"name\":\"凯达桂花空气清新剂\",\"originalPrice\":74787,\"weight\":320,\"imageUrl\":null,\"barcode\":\"6901064060082\",\"unit\":\"瓶\",\"originPlace\":\"广东\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":2739,\"shopId\":1,\"shopName\":\"OOMALL自营商铺\",\"goodsId\":66,\"categoryId\":266,\"freightId\":1,\"skuSn\":null,\"name\":\"迎华牌中老年无糖麦\",\"originalPrice\":2403,\"weight\":800,\"imageUrl\":null,\"barcode\":\"6928793900076\",\"unit\":\"\",\"originPlace\":\"\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":3407,\"shopId\":1,\"shopName\":\"OOMALL自营商铺\",\"goodsId\":333,\"categoryId\":266,\"freightId\":1,\"skuSn\":null,\"name\":\"金龙鱼AE营养菜籽油5000\",\"originalPrice\":41072,\"weight\":4,\"imageUrl\":null,\"barcode\":\"6902969887552\",\"unit\":\"桶\",\"originPlace\":\"\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":4560,\"shopId\":1,\"shopName\":\"OOMALL自营商铺\",\"goodsId\":130,\"categoryId\":266,\"freightId\":1,\"skuSn\":null,\"name\":\"400鹰威饼干\",\"originalPrice\":63334,\"weight\":18,\"imageUrl\":null,\"barcode\":\"6921094995314\",\"unit\":\"包\",\"originPlace\":\"\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2},{\"id\":5124,\"shopId\":1,\"shopName\":\"OOMALL自营商铺\",\"goodsId\":180,\"categoryId\":266,\"freightId\":1,\"skuSn\":null,\"name\":\"金顺昌壮乡桂圆糕150\",\"originalPrice\":35653,\"weight\":150,\"imageUrl\":null,\"barcode\":\"6922791100148\",\"unit\":\"盒\",\"originPlace\":\"桂林\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11T13:12:48\",\"gmtModified\":null,\"state\":2}],\"pageNum\":1,\"pageSize\":10,\"size\":6,\"startRow\":1,\"endRow\":6,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,contentAsString,true);
    }
    @Test
    @Transactional(readOnly = true)
    public void secondShopProducts2() throws Exception {
        String contentAsString = this.mockMvc.perform(get("/shops/1/categories/3/products")
                        .header("authorization", adminToken))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":504,\"errmsg\":\"分类id不存在\"}";
        JSONAssert.assertEquals(expected,contentAsString,true);
    }
    @Test
    @Transactional(readOnly = true)
    public void secondShopProducts3() throws Exception {
        this.mockMvc.perform(get("/shops/1/categories/1/products")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.errmsg").value("成功"));
    }

}
