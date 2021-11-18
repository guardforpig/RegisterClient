package cn.edu.xmu.oomall.comment.controller;

import cn.edu.xmu.oomall.comment.model.vo.CommentConclusionVo;
import cn.edu.xmu.oomall.core.util.JacksonUtil;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CommentControllerTest {
    private static String adminToken = "0";
    private static String shopToken = "0";
    private static String userToken = "0";
    @Autowired
    private MockMvc mvc;


    /**
     * 获取评论状态
     */
    @Test
    @Transactional
    public void getCommentState() throws Exception {
        String responseString = this.mvc.perform(get("/comments/states"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\n" +
                "\t\"errno\": 0,\n" +
                "\t\"data\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"code\": 0,\n" +
                "\t\t\t\"name\": \"未审核\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"code\": 1,\n" +
                "\t\t\t\"name\": \"评论成功\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"code\": 2,\n" +
                "\t\t\t\"name\": \"未通过\"\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 买家新增商品的评论
     */
    @Test
    @Transactional
    public void addGoodCommentGoodType() throws Exception {

        //评论为空
        String requestJSON = "{\"type\":0 ,\"content\":\"\",\"shopId\":\"1\"}";
        String responseString = this.mvc.perform(post("/internal/products/5/comments").contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":503,\"errmsg\":\"评论不能为空;\"}";
        JSONAssert.assertEquals(expected, responseString, true);


        //成功评论
        requestJSON = "{\"type\":0 ,\"content\":\"这个真不错\",\"shopId\":\"1\"}";
        responseString = this.mvc.perform(post("/internal/products/5/comments").contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expected = "{\"errno\":0,\"data\":{\"type\":0,\"content\":\"这个真不错\",\"state\":0},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);

    }

    /**
     * 管理员通过评论
     */
    @Test
    @Transactional
    public void allowComment() throws Exception {
        CommentConclusionVo conclusion = new CommentConclusionVo();
        conclusion.setConclusion(true);
        String requestJSON = JacksonUtil.toJson(conclusion);

        String responseString = this.mvc.perform(put("/shops/0/comments/1/confirm").contentType("application/json;charset=UTF-8").header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);

    }

    /**
     * 管理员不通过评论
     */
    @Test
    @Transactional
    public void passComment() throws Exception {
        CommentConclusionVo conclusion = new CommentConclusionVo();
        conclusion.setConclusion(false);
        String requestJSON = JacksonUtil.toJson(conclusion);

        String responseString = this.mvc.perform(put("/shops/0/comments/1/confirm").contentType("application/json;charset=UTF-8").header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 买家查看自己的评价记录，包括评论状态
     */
    @Test
    @Transactional
    public void getAllCommnetOfUser() throws Exception {
        CommentConclusionVo conclusion = new CommentConclusionVo();
        conclusion.setConclusion(true);
        String requestJSON = JacksonUtil.toJson(conclusion);

        String responseString = this.mvc.perform(get("/comments").contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":2,\"pages\":1,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":1,\"type\":0,\"content\":\"真不错\",\"state\":0},{\"id\":2,\"type\":0,\"content\":\"真不错\",\"state\":0}]}}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    /**
     * 查看评价列表
     */
    @Test
    @Transactional
    public void getAllCommnetOfProduct() throws Exception {
        CommentConclusionVo conclusion = new CommentConclusionVo();
        conclusion.setConclusion(true);
        String requestJSON = JacksonUtil.toJson(conclusion);

        String responseString = this.mvc.perform(put("/shops/0/comments/1/confirm").contentType("application/json;charset=UTF-8").header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);

        responseString = this.mvc.perform(get("/products/1/comments").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        expected = "{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":1,\"pages\":1,\"pageSize\":1,\"page\":1,\"list\":[{\"id\":1,\"type\":0,\"content\":\"真不错\",\"state\":1}]}}";
        JSONAssert.assertEquals(expected, responseString, false);
    }


    /**
     * 管理员查看未核审评论列表
     */
    @Test
    @Transactional
    public void getAllUnauditedComment() throws Exception {
        String responseString = this.mvc.perform(get("/shops/0/newcomments").contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String excepted = "{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":2,\"pages\":1,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":1,\"type\":0,\"content\":\"真不错\",\"state\":0},{\"id\":2,\"type\":0,\"content\":\"真不错\",\"state\":0}]}}";
        JSONAssert.assertEquals(excepted, responseString, false);

    }

    /**
     * 管理员查看自己审核的评论列表
     */
    @Test
    @Transactional
    public void getAllCommentByShopId() throws Exception {
        String responseString = this.mvc.perform(get("/shops/1/comments/").contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String excepted = "{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":2,\"pages\":1,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":1,\"type\":0,\"content\":\"真不错\",\"state\":0},{\"id\":2,\"type\":0,\"content\":\"真不错\",\"state\":0}]}}";
        JSONAssert.assertEquals(excepted, responseString, false);

    }
}
