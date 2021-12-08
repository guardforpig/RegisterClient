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
public class DepartsRolesTest extends BaseTestOomall {

    private static String GETURL="/privilege/departs/{did}/roles";

    private static String DELURL="/privilege/departs/{did}/roles/{id}";

    private static String PUTURL = "/privilege/departs/{did}/roles/{id}";
    /**
     * 未登录查询角色信息
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(1)
    public void selectRoleTest1() throws Exception {
        this.mallClient.get().uri(GETURL+"?page=1&pageSize=2",0)
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
    @Order(2)
    public void selectRoleTest2() throws Exception {
        this.mallClient.get().uri(GETURL+"?page=1&pageSize=2",0)
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
    @Order(3)
    public void selectRoleTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.mallClient.get().uri(GETURL,10)
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
    @Order(4)
    public void selectRoleTest4() throws Exception {
        String token = this.adminLogin("5961900008", "123456");
        this.mallClient.get().uri(GETURL,10)
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
    @Order(5)
    public void selectRoleTest5() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.mallClient.get().uri(GETURL,10)
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
    @Order(6)
    public void selectRoleTest6() throws Exception {
        String token = this.adminLogin("shop2_adv", "123456");
        this.mallClient.get().uri(GETURL,10)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员删除角色id不存在
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(7)
    public void deleteRoleTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = this.mallClient.delete().uri(DELURL, 1, 1009)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员删除角色id与部门id不匹配
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(8)
    public void deleteRoleTest3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        byte[] responseString = this.mallClient.delete().uri(DELURL, 1, 109)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 未登录删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(9)
    public void deleteRoleTest4() throws Exception {
        byte[] responseString = this.mallClient.delete().uri(DELURL, 1, 109)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 伪造token删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(10)
    public void deleteRoleTest5() throws Exception {
        byte[] responseString = this.mallClient.delete().uri(DELURL, 1, 109)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(11)
    public void deleteRoleTest6() throws Exception {
        //判断用户有权限
        String token1 = this.adminLogin("delrole_user11", "123456");
        this.mallClient.get().uri(GETURL,1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String token = this.adminLogin("13088admin", "123456");
        this.mallClient.delete().uri(DELURL, 1, 109)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        this.mallClient.get().uri(GETURL,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 109)]").doesNotExist()
                .returnResult()
                .getResponseBodyContent();

        this.mallClient.get().uri(GETURL,1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 同店铺管理员删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(12)
    public void deleteRoleTest7() throws Exception {
        //判断用户有权限
        String token1 = this.adminLogin("delrole_user22", "123456");
        this.mallClient.get().uri(GETURL,2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String token = this.adminLogin("2721900002", "123456");
        this.mallClient.delete().uri(DELURL, 2, 110)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        this.mallClient.get().uri(GETURL,2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 110)]").doesNotExist()
                .returnResult()
                .getResponseBodyContent();

        this.mallClient.get().uri(GETURL,2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员修改角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest1() throws Exception {
        String token = adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"testU\",\"name\": \"testU\"}";
        this.mallClient.put().uri(PUTURL, 1, 108)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        this.mallClient.get().uri(GETURL, 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 108 && @.name == \"testU\" && @.departId == 1)]").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员修改角色角色名为空
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest2() throws Exception {
        String token = adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"testU\",\"name\": \"\"}";
        byte[] responseString = this.mallClient.put().uri(PUTURL, 1, 108)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员修改角色id不存在
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest4() throws Exception {
        String token = adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        byte[] responseString = this.mallClient.put().uri(PUTURL, 11, 108)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员修改角色id与部门id不匹配
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest5() throws Exception {
        String token = adminLogin("2721900002", "123456");
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        byte[] responseString = this.mallClient.put().uri(PUTURL, 1, 108)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 未登录修改角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest6() throws Exception {
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        byte[] responseString = this.mallClient.put().uri(PUTURL, 1, 108)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 伪造token修改角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest7() throws Exception {
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        byte[] responseString = this.mallClient.put().uri(PUTURL, 1, 108)
                .header("authorization", "test")
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 无权限修改角色
     *
     */
    @Test
    public void updateRoleTest8() throws Exception {
        String token = adminLogin("shop1_coupon", "123456");
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        byte[] responseString = this.mallClient.put().uri(PUTURL, 1, 108)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


}
