package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.freight.model.vo.PieceFreightVo;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PieceFreightControllerTest {
    @Autowired
    private MockMvc mvc;
    private static JwtHelper jwtHelper = new JwtHelper();
    private static String adminToken;

    /**
     * 管理员成功定义件数模板明细
     * @throws Exception
     */
    @Test
    public void createPieceFreightModelTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(1L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(post("/shops/0/freightmodels/2/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":{\"regionId\":1,\"firstItems\":1,\"firstItemFreight\":10,\"additionalItems\":1,\"additionalItemsPrice\":5,\"createdBy\":{\"id\":1,\"name\":\"admin\"},\"modifiedBy\":{\"id\":1,\"name\":\"admin\"}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    /**
     * 运费模板中该地区已经定义
     * @throws Exception
     */
    @Test
    public void createPieceFreightModelTest1() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(10L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(post("/shops/0/freightmodels/2/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":997,\"errmsg\":\"运费模板中该地区已经定义\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 该运费模板类型与内容不符
     * @throws Exception
     */
    @Test
    public void createPieceFreightModelTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(10L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(post("/shops/0/freightmodels/1/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":997,\"errmsg\":\"运费模板中该地区已经定义\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /**
     * shopId不为0
     * @throws Exception
     */
    @Test
    public void createPieceFreightModelTest3() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(10L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(post("/shops/1/freightmodels/1/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"非管理员无权操作\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /**
     * 店家或管理员查询件数运费模板的明细
     * @throws Exception
     */
    @Test
    public void getPieceFreightTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600);
        String responseString = this.mvc.perform(get("/shops/0/freightmodels/2/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":3,\"regionId\":10,\"firstItems\":10,\"firstItemFreight\":10,\"additionalItems\":10,\"additionalItemsPrice\":10,\"createdBy\":{\"id\":111,\"name\":\"admin\"},\"gmtCreate\":\"2021-11-18T22:25:46\",\"gmtModified\":null,\"modifiedBy\":{\"id\":111,\"name\":\"admin\"}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 店家或管理员删掉件数运费模板明细
     * @throws Exception
     */
    @Test
    public void deletePieceFreightTest() throws Exception
    {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600);
        String responseString = this.mvc.perform(delete("/shops/0/pieceItems/3").contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     *
     * @throws Exception
     */

    @Test
    public void modifyPieceFreightModelTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 3600);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(1L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(put("/shops/0/pieceItems/4").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }



}
