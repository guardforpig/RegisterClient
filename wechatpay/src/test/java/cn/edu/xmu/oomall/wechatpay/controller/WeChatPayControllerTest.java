package cn.edu.xmu.oomall.wechatpay.controller;

import cn.edu.xmu.oomall.wechatpay.WeChatPayApplication;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayTransactionVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author ziyi guo
 * @date 2021/12/2
 */
@SpringBootTest(classes = WeChatPayApplication.class)
@AutoConfigureMockMvc
@Transactional
public class WeChatPayControllerTest {

    @Autowired
    private MockMvc mvc;


    @Test
    @Transactional
    public void createTransactionTest1() throws Exception{

        WeChatPayTransactionVo vo = new WeChatPayTransactionVo();
        vo.setAppid("wxd678efh567hg6787");
        vo.setMchid("1230000109");
        vo.setDescription("pay");
        vo.setOutTradeNo("2");
        vo.setAmount(100,"CNY");
        vo.setPayer("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
        vo.setNotifyUrl("/wechat/payment/notify");

        String responseString;
        String expectedResponse;

        for(int i=2; i<=9; i++){
            vo.setOutTradeNo(String.valueOf(i));
            responseString = this.mvc.perform(post("/internal/wechat/pay/transactions/jsapi").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            expectedResponse = "";
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }

    }

    @Test
    @Transactional
    public void createTransactionTest2() throws Exception {

        WeChatPayTransactionVo vo = new WeChatPayTransactionVo();
        vo.setAppid("wxd678efh567hg6787");
        vo.setMchid("1230000109");
        vo.setDescription("pay");
        vo.setOutTradeNo("1");
        vo.setAmount(100, "CNY");
        vo.setPayer("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
        vo.setNotifyUrl("/wechat/payment/notify");

        String responseString = this.mvc.perform(post("/internal/wechat/pay/transactions/jsapi").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

}
