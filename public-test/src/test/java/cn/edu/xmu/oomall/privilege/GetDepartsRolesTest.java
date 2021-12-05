package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.nio.charset.StandardCharsets;

/**
 * @author Ming Qiu
 * @date Created in 2020/12/18 22:29
 **/
public class GetDepartsRolesTest extends BaseTestOomall {

    private static String TESTURL="/privilege/departs/%d/roles";
    /**
     * 未登录查询角色信息
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void selectRoleTest1() throws Exception {
        this.mallClient.get().uri(String.format(TESTURL,0)+"?page=1&pageSize=2")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 使用伪造token查询角色信息
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void selectRoleTest2() throws Exception {
        this.mallClient.get().uri(String.format(TESTURL,0)+"?page=1&pageSize=2")
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员查询店铺角色信息
     *
     */
    @Test
    public void selectRoleTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.mallClient.get().uri(String.format(TESTURL,10))
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[?(@.list.length() == 1)]").exists()
                .jsonPath("$.data.list[?(@.id == 11)]").exists()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店铺管理员查询店铺角色信息
     *
     */
    @Test
    public void selectRoleTest4() throws Exception {
        String token = this.adminLogin("5961900008", "123456");
        this.mallClient.get().uri(String.format(TESTURL,10))
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[?(@.list.length() == 1)]").exists()
                .jsonPath("$.data.list[?(@.id == 11)]").exists()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 不同店铺管理员查询店铺角色信息
     *
     */
    @Test
    public void selectRoleTest5() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.mallClient.get().uri(String.format(TESTURL,10))
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 无权限店铺管理员查询店铺角色信息
     */
    @Test
    public void selectRoleTest6() throws Exception {
        String token = this.adminLogin("shop2_adv", "123456");
        this.mallClient.get().uri(String.format(TESTURL,10))
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

}
