package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Objects;

/**
 * @author XQChen
 * @version 创建时间：2020/12/7 下午1:35
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
public class GetDepartUsersTest extends BaseTestOomall {

    private static String TESTURL ="/privilege/departs/{did}/users";

    /***
     * 正确查找用户
     * @throws Exception
     */
    @Test
    public void findAllUsers1() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        this.mallClient
                .get()
                .uri(TESTURL+"?userName=13088admin", 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }

    /***
     * 正确查找用户
     * @throws Exception
     */
    @Test
    public void findAllUsers2() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        this.mallClient
                .get()
                .uri(TESTURL+"?userName=8131600001",1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == \"店铺1超级管理员\" && @.sign == 0)]").exists()
                .jsonPath("$.data.list.length()").isEqualTo(1);
    }

    /***
     * 查找签名错误用户信息
     * @throws Exception
     */
    @Test
    public void findAllUsers3() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        this.mallClient.get()
                .uri(TESTURL+"?userName=wrong_sign",1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_FALSIFY.getCode())
                .jsonPath("$.data[?(@.name == \"签名错误\" && @.sign == 1)]").exists();
    }

    /***
     * 查找其他店铺用户信息
     * @throws Exception
     */
    @Test
    public void findAllUsers4() throws Exception {

        String token = this.adminLogin("2721900002", "123456");

        this.mallClient.get()
                .uri(TESTURL+"?userName=change_user",1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
}
