package cn.edu.xmu.oomall.activity.controller;

import cn.edu.xmu.oomall.activity.ActivityApplication;
import cn.edu.xmu.oomall.activity.mapper.GroupOnActivityPoMapper;
import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleSaleInfoVo;
import cn.edu.xmu.oomall.activity.model.po.GroupOnActivityPo;
import cn.edu.xmu.oomall.activity.model.vo.*;
import cn.edu.xmu.oomall.activity.service.GroupOnActivityService;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = ActivityApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc    //配置模拟的MVC，这样可以不启动服务器测试
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class ActivityTestControllerTest {

    @Autowired
    private MockMvc mvc;

    private static JwtHelper jwtHelper = new JwtHelper();
    private static String adminToken;

    @MockBean(name = "cn.edu.xmu.oomall.activity.microservice.GoodsService")
    private GoodsService goodsService;




    @Test  //测试下线成功
    public void offlineGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.offlineOnsale(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/0/groupons/1/offline").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test  //测试下线失败,非管理员无权操作
    public void noRightOfflineGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.offlineOnsale(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/1/groupons/1/offline").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"非管理员无权操作\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test  //测试下线失败,404错误
    public void notFoundGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.offlineOnsale(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/0/groupons/1000/offline").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test  //测试下线失败,当前状态禁止此操作
    public void failureOfflineGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.offlineOnsale(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/0/groupons/2/offline").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }


    @Test  //测试上线成功
    public void onlineGroupOnActivityTest() throws Exception {

        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.onlineOnsale(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(new InternalReturnObject(ReturnNo.OK));
        String responseString = this.mvc.perform(put("/shops/0/groupons/2/online").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test  //测试上线失败,非管理员无权操作
    public void noRightOnlineGroupOnActivityTest() throws Exception {

        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.onlineOnsale(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(new InternalReturnObject(ReturnNo.OK));
        String responseString = this.mvc.perform(put("/shops/1/groupons/2/online").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"非管理员无权操作\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test  //测试上线失败,当前状态禁止此操作
    public void failureOnlineGroupOnActivityTest() throws Exception {

        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.onlineOnsale(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(new InternalReturnObject(ReturnNo.OK));
        String responseString = this.mvc.perform(put("/shops/0/groupons/1/online").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test   //删除成功
    public void deleteGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.deleteOnsale(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(delete("/shops/0/groupons/3")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test   //删除失败,非管理员无权操作
    public void noRightDeleteGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.deleteOnsale(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(delete("/shops/1/groupons/3")
                .header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"非管理员无权操作\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test   //删除失败，非草稿态
    public void failureDeleteGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.deleteOnsale(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(delete("/shops/0/groupons/1")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test   //删除失败，404错误
    public void notFoundDeleteGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.deleteOnsale(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(delete("/shops/0/groupons/10000")
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test//修改成功
    public void updateGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        String requestJson = "{\"name\": \"修改后的名称\"}";
        OnsaleVo o1 = new OnsaleVo();
        o1.setId(Long.valueOf(1));
        OnsaleVo o2 = new OnsaleVo();
        o2.setId(Long.valueOf(2));
        OnsaleVo o3 = new OnsaleVo();
        o3.setId(Long.valueOf(3));
        List<OnsaleVo> list = new ArrayList<>();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        PageVo<OnsaleVo> po = new PageVo<>(1,5,5,5,list);
        Mockito.when(goodsService.getOnsale(Mockito.anyLong(),Mockito.anyLong(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(new InternalReturnObject<>(po));
        Mockito.when(goodsService.modifyOnsale(Mockito.anyLong(),Mockito.anyLong(),Mockito.any(OnsaleModifyVo.class))).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/0/groupons/3").contentType("application/json;charset=UTF-8").content(requestJson)
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }


    @Test//修改失败,非管理员无权操作
    public void noRightUpdateGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        String requestJson = "{\"name\": \"修改后的名称\"}";
        OnsaleVo o1 = new OnsaleVo();
        o1.setId(Long.valueOf(1));
        OnsaleVo o2 = new OnsaleVo();
        o2.setId(Long.valueOf(2));
        OnsaleVo o3 = new OnsaleVo();
        o3.setId(Long.valueOf(3));
        List<OnsaleVo> list = new ArrayList<>();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        PageVo<OnsaleVo> po = new PageVo<>(1,5,5,5,list);
        Mockito.when(goodsService.getOnsale(Mockito.anyLong(),Mockito.anyLong(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(new InternalReturnObject<>(po));
        Mockito.when(goodsService.modifyOnsale(Mockito.anyLong(),Mockito.anyLong(),Mockito.any(OnsaleModifyVo.class))).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/1/groupons/3").contentType("application/json;charset=UTF-8").content(requestJson)
                .header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"非管理员无权操作\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test//修改失败，非草稿态不可修改
    public void failureUpdateGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        String requestJson = "{\"name\": \"修改后的名称\"}";
        OnsaleVo o1 = new OnsaleVo();
        o1.setId(Long.valueOf(1));
        OnsaleVo o2 = new OnsaleVo();
        o2.setId(Long.valueOf(2));
        OnsaleVo o3 = new OnsaleVo();
        o3.setId(Long.valueOf(3));
        List<OnsaleVo> list = new ArrayList<>();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        PageVo<OnsaleVo> po = new PageVo<>(1,5,5,5,list);
        Mockito.when(goodsService.getOnsale(Mockito.anyLong(),Mockito.anyLong(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(new InternalReturnObject<>(po));
        Mockito.when(goodsService.modifyOnsale(Mockito.anyLong(),Mockito.anyLong(),Mockito.any(OnsaleModifyVo.class))).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/0/groupons/1").contentType("application/json;charset=UTF-8").content(requestJson)
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }


    @Test//修改成功
    public void addOnSaleToGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.addOnsale(Mockito.anyLong(),Mockito.anyLong(),Mockito.any(SimpleSaleInfoVo.class))).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/0/products/2/groupons/3/onsale").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test//修改失败，当前状态禁止此操作
    public void failureAddOnSaleToGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.addOnsale(Mockito.anyLong(),Mockito.anyLong(),Mockito.any(SimpleSaleInfoVo.class))).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/0/products/2/groupons/1/onsale").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }



}
