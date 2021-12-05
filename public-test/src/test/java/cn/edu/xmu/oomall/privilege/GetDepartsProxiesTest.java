package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Li Zihan 24320182203227
 * @date Created in 2020/12/9 12:33
 **/
@SpringBootTest(classes = PublicTestApp.class)
public class GetDepartsProxiesTest extends BaseTestOomall {

    private static String TESTURL ="/privilege/departs/%d/proxies";

    /**
     * 1
     * 不登录查询所有用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies1() throws Exception {

        this.mallClient.get().uri(String.format(TESTURL,1))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
    * 2
    * 平台管理员查询任意部门用户代理关系
    *
    * @author 24320182203227 Li Zihan
    */
    @Test
    public void getListProxies2() throws Exception {

        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = this.mallClient.get().uri(String.format(TESTURL,1))
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.proxyUser.id == 17332 && @.user.id == 17337)]").exists()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 3
     * 店铺管理员查询自己部门所有用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies3() throws Exception {

        String token = this.adminLogin("8131600001", "123456");
        byte[] responseString = this.mallClient.get().uri(String.format(TESTURL,1))
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.proxyUser.id == 17332 && @.user.id == 17337)]").exists()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 4
     * 管理员查询非自己部门用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies4() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        byte[] responseString = this.mallClient.get().uri(String.format(TESTURL,2))
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 5
     * 无权限管理员查询用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies5() throws Exception {

        String token = this.adminLogin("norole_user1", "123456");
        byte[] responseString = this.mallClient.get().uri(String.format(TESTURL,1))
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 7
     * 伪造token查询所有用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies7() throws Exception {
        byte[] responseString = this.mallClient.get().uri(String.format(TESTURL,0))
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
}