package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用户修改自己信息测试类
 *
 * @author 24320182203175 陈晓如
 * createdBy 陈晓如 2020/11/30 13:42
 * modifiedBy 陈晓如 2020/11/30 13:42
 **/
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
public class PutSelfUsersTest extends BaseTestOomall {

    private static String TESTURL = "/privilege/self/users";

    /**
     * 修改自己的信息测试2: 管理员未登录修改自己的信息
     *
     * @author 24320182203175 陈晓如
     * createdBy 陈晓如 2020/12/01 10:43
     * modifiedBy 陈晓如 2020/12/01 10:43
     */
    @Test
    public void changeMyAdminselfInfo1() throws Exception {
        String userJson = "{\"name\": \"oomall\"," +
                "\"idnumber\": \"123456789\"," +
                "\"passportNumber\": \"12345678\"}";
        byte[] responseString = this.mallClient.put().uri(TESTURL)
                .bodyValue(userJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 正常修改
     *
     * @throws Exception
     */
    @Test
    public void changeMyAdminselfInfo2() throws Exception {
        String token = this.adminLogin("change_user", "123456");
        String userJson = "{\"name\": \"oomall\"," +
                "\"idnumber\": \"123456789\"," +
                "\"passportNumber\": \"12345678\"}";
        this.mallClient.put().uri(TESTURL).
                header("authorization", token).
                bodyValue(userJson).
                exchange().
                expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        this.mallClient.get().uri("/privilege/self/users").
                header("authorization", token).
                exchange().
                expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("oomall")
                .jsonPath("$.data.idnumber").isEqualTo("123456789")
                .jsonPath("$.data.passportNumber").isEqualTo("12345678")
                .returnResult()
                .getResponseBodyContent();

    }
}

