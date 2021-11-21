package cn.edu.xmu.oomall.activity.controller;

import cn.edu.xmu.oomall.activity.ActivityApplication;
import cn.edu.xmu.oomall.activity.mapper.GroupOnActivityPoMapper;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleSaleInfoVO;
import cn.edu.xmu.oomall.activity.model.po.GroupOnActivityPo;
import cn.edu.xmu.oomall.activity.model.vo.GroupOnActivityVo;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = ActivityApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc    //配置模拟的MVC，这样可以不启动服务器测试
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ActivityTestController {

    @Autowired
    private MockMvc mvc;


    @MockBean(name = "cn.edu.xmu.oomall.activity.microservice.OnSaleService")
    private OnSaleService onSaleService;

    @MockBean(name = "cn.edu.xmu.oomall.activity.mapper.groupOnActivityPoMapper")
    private GroupOnActivityPoMapper groupOnActivityPoMapper;



    @Test  //测试下线成功
    @Transactional
    public void offlineGroupOnActivityTest() throws Exception {
        long shopId = 4;
        long id = 4;
        LocalDateTime beginTime = LocalDateTime.now();
        SimpleSaleInfoVO simpleSaleInfoVO = new SimpleSaleInfoVO();
        simpleSaleInfoVO.setBeginTime(beginTime);
        ReturnObject ret = new ReturnObject(ReturnNo.OK,"成功");
        Mockito.when(onSaleService.offlineOnsale(Mockito.anyLong())).thenReturn(new ReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/4/groupons/4/offline").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        System.out.println(responseString);
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test  //测试下线失败
    @Transactional
    public void failureOfflineGroupOnActivityTest() throws Exception {
        long shopId = 4;
        long id = 1;
        LocalDateTime beginTime = LocalDateTime.now();
        LocalDateTime endTime = beginTime.now();
        Mockito.when(onSaleService.offlineOnsale(Mockito.anyLong())).thenReturn(new ReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/4/groupons/1/offline").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        System.out.println(responseString);
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test
    @Transactional
    public void failureOfflineGroupOnActivityTest1() throws Exception {
        long shopId = 4;
        long id = 1;
        Mockito.when(onSaleService.offlineOnsale(Mockito.anyLong())).thenReturn(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST));
        String responseString = this.mvc.perform(put("/shops/4/groupons/4/offline").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        System.out.println(responseString);
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }


    @Test  //测试上线成功
    @Transactional
    public void OnlineGroupOnActivityTest() throws Exception {

        long shopId = 4;
        long id = 1;
        LocalDateTime beginTime = LocalDateTime.now();
        LocalDateTime endTime = beginTime.now();
        Mockito.when(onSaleService.onlineOnsale(Mockito.anyLong())).thenReturn(new ReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/4/groupons/1/online").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test  //测试上线成功
    @Transactional
    public void NotOnlineGroupOnActivityTest() throws Exception {

        long shopId = 4;
        long id = 1;
        LocalDateTime beginTime = LocalDateTime.now();
        LocalDateTime endTime = beginTime.now();
        Mockito.when(onSaleService.onlineOnsale(Mockito.anyLong())).thenReturn(new ReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/4/groupons/4/online").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test  //测试上线成功
    @Transactional
    public void NotOnlineGroupOnActivityTest1() throws Exception {

        LocalDateTime beginTime = LocalDateTime.now();
        LocalDateTime endTime = beginTime.now();
        Mockito.when(onSaleService.onlineOnsale(Mockito.anyLong())).thenReturn(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST));
        String responseString = this.mvc.perform(put("/shops/4/groupons/1/online").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test  //测试上线失败
    @Transactional
    public void failureOnlineGroupOnActivityTest() throws Exception {

        long shopId = 4;
        long id = 4;
        Byte state = 2;
        Mockito.when(onSaleService.onlineOnsale(Mockito.anyLong())).thenReturn(new ReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/4/groupons/4/online").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }


    @Test   //测试删除成功
    public void deleteGroupOnActivityTest() throws Exception {

        Byte state = 1;
        GroupOnActivityPo g1 = new GroupOnActivityPo();
        g1.setState(state);
        Mockito.when(onSaleService.deleteOnSale(Mockito.anyLong())).thenReturn(new ReturnObject(true));
        String responseString = this.mvc.perform(delete("/shops/2/groupons/38"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test   //测试删除成功
    public void deleteGroupOnActivityTestWithStateError() throws Exception {

        Byte state = 1;
        GroupOnActivityPo g1 = new GroupOnActivityPo();
        g1.setState(state);
        Mockito.when(onSaleService.deleteOnSale(Mockito.anyLong())).thenReturn(new ReturnObject(true));
        String responseString = this.mvc.perform(delete("/shops/2/groupons/24"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }


    @Test   //测试删除成功
    public void deleteGroupOnActivityTest3() throws Exception {

        Byte state = 1;
        GroupOnActivityPo g1 = new GroupOnActivityPo();
        g1.setState(state);
        Mockito.when(onSaleService.deleteOnSale(Mockito.anyLong())).thenReturn(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST));
        String responseString = this.mvc.perform(delete("/shops/2/groupons/35"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test
    @Transactional
    public void updateGroupOnActivityTest() throws Exception {
        long id = 1;
        GroupOnActivityVo g = new GroupOnActivityVo();
        g.setName("updateTest");
        GroupOnActivityPo g1 = new GroupOnActivityPo();
        g1.setId(id);
        g1.setName("updateTest");

        String requestJson = "{\"name\": \"修改后的名称\"}";
        Mockito.when(onSaleService.updateGrouponOnsale(Mockito.anyLong(),Mockito.any(SimpleSaleInfoVO.class))).thenReturn(new ReturnObject(true));

        String responseString = this.mvc.perform(put("/shops/1/groupons/1").contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);




    }


    @Test
    @Transactional
    public void updateGroupOnActivityTestWithStateError() throws Exception {
        long id = 1;
        GroupOnActivityVo g = new GroupOnActivityVo();
        g.setName("updateTest");
        GroupOnActivityPo g1 = new GroupOnActivityPo();
        g1.setId(id);
        g1.setName("updateTest");

        String requestJson = "{\"name\": \"修改后的名称\",\"beginTime\":\"2021-11-11T11:11:12\",\"endTime\":\"2021-12-11T11:11:11\"}";
        Mockito.when(onSaleService.updateGrouponOnsale(Mockito.anyLong(),Mockito.any(SimpleSaleInfoVO.class))).thenReturn(new ReturnObject(true));

        String responseString = this.mvc.perform(put("/shops/1/groupons/4").contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);




    }

    @Test
    @Transactional
    public void updateGroupOnActivityTestNotFound() throws Exception {
        long id = 1;
        GroupOnActivityVo g = new GroupOnActivityVo();
        g.setName("updateTest");
        GroupOnActivityPo g1 = new GroupOnActivityPo();
        g1.setId(id);
        g1.setName("updateTest");

        String requestJson = "{\"name\": \"修改后的名称\",\"beginTime\":\"2021-11-11T11:11:11\",\"endTime\":\"2021-12-11T11:11:11\"}";
        Mockito.when(onSaleService.updateGrouponOnsale(Mockito.anyLong(),Mockito.any(SimpleSaleInfoVO.class))).thenReturn(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST));

        String responseString = this.mvc.perform(put("/shops/1/groupons/38").contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);




    }

    @Test
    @Transactional
    public void updateGroupOnActivityTestBothNotFound() throws Exception {
        long id = 1;
        GroupOnActivityVo g = new GroupOnActivityVo();
        g.setName("updateTest");
        GroupOnActivityPo g1 = new GroupOnActivityPo();
        g1.setId(id);
        g1.setName("updateTest");

        String requestJson = "{\"name\": \"修改后的名称\"}";
        Mockito.when(onSaleService.updateGrouponOnsale(Mockito.anyLong(),Mockito.any(SimpleSaleInfoVO.class))).thenReturn(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST));

        String responseString = this.mvc.perform(put("/shops/1/groupons/9").contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);




    }



    @Test
    @Transactional
    public void addGrouponActivityTest() throws Exception {

        long shopId = 1;
        long id = 9;
        Byte state = 1;
        SimpleSaleInfoVO simpleSaleInfoVO = new SimpleSaleInfoVO();
        Mockito.when(onSaleService.addOnsale(Mockito.anyLong(),Mockito.anyLong(),Mockito.any(SimpleSaleInfoVO.class))).thenReturn(new ReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/1/products/1/groupons/9/onsale"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);


    }


    @Test
    @Transactional
    public void addGrouponActivityTestWithStateError() throws Exception {

        long shopId = 1;
        long id = 9;
        Byte state = 1;
        SimpleSaleInfoVO simpleSaleInfoVO = new SimpleSaleInfoVO();
        Mockito.when(onSaleService.addOnsale(Mockito.anyLong(),Mockito.anyLong(),Mockito.any(SimpleSaleInfoVO.class))).thenReturn(new ReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/1/products/1/groupons/10/onsale"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);


    }

    @Test
    @Transactional
    public void addGrouponActivityTestNotFound() throws Exception {

        long shopId = 1;
        long id = 9;
        Byte state = 1;
        Mockito.when(onSaleService.addOnsale(Mockito.anyLong(),Mockito.anyLong(),Mockito.any(SimpleSaleInfoVO.class))).thenReturn(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST));
        String responseString = this.mvc.perform(put("/shops/1/products/1/groupons/9/onsale"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);


    }


}
