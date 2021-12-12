package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author Ming Qiu
 * @date Created in 2020/12/18 22:29
 **/
@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepartsRolesTest extends BaseTestOomall {

    private static String GETURL="/privilege/departs/{did}/roles";

    private static String IDURL="/privilege/departs/{did}/roles/{id}";

    /**
     * 未登录查询角色信息
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void selectRoleTest1() throws Exception {
        this.mallClient.get().uri(GETURL+"?page=1&pageSize=2",0)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
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
        this.mallClient.get().uri(GETURL+"?page=1&pageSize=2",0)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }

    /**
     * 平台管理员查询店铺角色信息
     *
     */
    @Test
    @Order(1)
    public void selectRoleTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.mallClient.get().uri(GETURL,10)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == 11)]").exists();
    }

    /**
     * 店铺管理员查询店铺角色信息
     *
     */
    @Test
    @Order(1)
    public void selectRoleTest4() throws Exception {
        String token = this.adminLogin("5961900008", "123456");
        this.mallClient.get().uri(GETURL,10)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == 11)]").exists();
    }

    /**
     * 不同店铺管理员查询店铺角色信息
     *
     */
    @Test
    public void selectRoleTest5() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.mallClient.get().uri(GETURL,10)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 无权限店铺管理员查询店铺角色信息
     */
    @Test
    public void selectRoleTest6() throws Exception {
        String token = this.adminLogin("shop2_adv", "123456");
        this.mallClient.get().uri(GETURL,10)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 管理员删除角色id不存在
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void deleteRoleTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.mallClient.delete().uri(IDURL, 1, 1009)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 管理员删除角色id与部门id不匹配
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void deleteRoleTest3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.mallClient.delete().uri(IDURL, 1, 109)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 未登录删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void deleteRoleTest4() throws Exception {
        this.mallClient.delete().uri(IDURL, 1, 109)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());    }

    /**
     * 伪造token删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void deleteRoleTest5() throws Exception {
        this.mallClient.delete().uri(IDURL, 1, 109)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }

    /**
     * 平台管理员删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(2)
    public void deleteRoleTest6() throws Exception {
        //判断用户有权限
        String token1 = this.adminLogin("delrole_user11", "123456");
        this.mallClient.get().uri(GETURL,1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String token = this.adminLogin("13088admin", "123456");
        this.mallClient.delete().uri(IDURL, 1, 109)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(GETURL,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 109)]").doesNotExist();

        this.mallClient.get().uri(GETURL,1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

    }

    /**
     * 同店铺管理员删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(3)
    public void deleteRoleTest7() throws Exception {
        //判断用户有权限
        String token1 = this.adminLogin("delrole_user22", "123456");
        this.mallClient.get().uri(GETURL,2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String token = this.adminLogin("2721900002", "123456");
        this.mallClient.delete().uri(IDURL, 2, 110)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(GETURL,2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 110)]").doesNotExist();

        this.mallClient.get().uri(GETURL,2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 平台管理员修改角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(4)
    public void updateRoleTest1() throws Exception {
        String token = adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"testU\",\"name\": \"testU\"}";
        this.mallClient.put().uri(IDURL, 1, 108)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(GETURL, 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 108 && @.name == \"testU\" && @.departId == 1)]").isEqualTo(ReturnNo.OK.getCode());
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
        this.mallClient.put().uri(IDURL, 1, 108)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FIELD_NOTVALID.getCode());
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
        this.mallClient.put().uri(IDURL, 11, 108)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
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
        this.mallClient.put().uri(IDURL, 1, 108)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
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
        this.mallClient.put().uri(IDURL, 1, 108)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
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
        this.mallClient.put().uri(IDURL, 1, 108)
                .header("authorization", "test")
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }

    /**
     * 无权限修改角色
     *
     */
    @Test
    public void updateRoleTest8() throws Exception {
        String token = adminLogin("shop1_coupon", "123456");
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        this.mallClient.put().uri(IDURL, 1, 108)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 25
     * 不同部门管理员增加角色
     * Li Zihan
     */
    @Test
    public void insertRoleTest1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        this.mallClient.post().uri(GETURL, 1)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 23
     * 平台管理员新增角色
     * @author Li Zihan
     */
    @Test
    @Order(5)
    public void insertRoleTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"管理员\"}";
        this.mallClient.post().uri(GETURL, 0)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(GETURL, 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == \"管理员\")]").exists();
    }

    /**
     * 24
     * 平台管理员新增部门1角色
     * @author Li Zihan
     */
    @Test
    @Order(6)
    public void insertRoleTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        this.mallClient.post().uri(GETURL,1)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(GETURL, 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == \"hello\")]").exists();
    }

    /**
     * 24
     * 部门管理员新增部门2角色
     * @author Li Zihan
     */
    @Test
    @Order(7)
    public void insertRoleTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        this.mallClient.post().uri(GETURL, 2)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(GETURL, 2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == \"hello\")]").exists();
    }

    /**
     * 25
     * 未登录新增角色
     * Li Zihan
     */
    @Test
    public void insertRoleTest5() throws Exception {
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"\"}";
        this.mallClient.post().uri(GETURL, 0)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 24
     * 无权限管理员新增部门2角色
     */
    @Test
    public void insertRoleTest6() throws Exception {
        String token = this.adminLogin("shop2_adv", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        this.mallClient.post().uri(GETURL, 2)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

    }

}
