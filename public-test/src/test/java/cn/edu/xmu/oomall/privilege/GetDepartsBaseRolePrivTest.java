package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.oomall.LoginVo;
import cn.edu.xmu.oomall.privilege.vo.RoleRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

/**
 * 权限模块公开测试
 * 测试需添加相应的数据库数据
 * @author 张湘君 24320182203327
 * @date 2020/12/13 20:15
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GetDepartsBaseRolePrivTest extends BaseTestOomall {

    private static String TESTURL = "/privilege/departs/{did}/baseroles/{id}/privileges";
    private static String BASEROLEURL = "/privilege/departs/{did}/baseroles";
    private static String IDURL = "/privilege/departs/{did}/baseroles/{id}/privileges/{pid}";


    /**
     * 7
     * 获得角色所有权限，成功获取
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void getRolePrivsTest1() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        this.mallClient.get().uri(TESTURL,0,88)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 96)]").exists()
                .jsonPath("$.data.list[?(@.id == 95)]").exists()
                .jsonPath("$.data.list[?(@.id == 108)]").exists()
                .jsonPath("$.data.list[?(@.id == 109)]").exists()
                .jsonPath("$.data.list[?(@.id == 111)]").exists()
                .jsonPath("$.data.list[?(@.id == 110)]").exists()
                .jsonPath("$.data.list[?(@.id == 107)]").exists()
                .jsonPath("$.data.list.length()").isEqualTo(7);

    }

    /**
     * 8
     * 获得角色所有权限，id不存在
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void getRolePrivsTest2() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        this.mallClient.get().uri(TESTURL,0,88).header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 9
     * 增加角色的权限，增加成功
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void addRolePrivTest1() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"testU\",\"name\": \"测试11\"}";
        String result = new String(Objects.requireNonNull(this.mallClient.post().uri(BASEROLEURL, 0)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.name").isEqualTo("测试11")
                .returnResult()
                .getResponseBodyContent()), "UTF-8");

        RoleRetVo role = JacksonUtil.parseObject(result, "data", RoleRetVo.class);
        Long roleId = role.getId();

        this.mallClient.post().uri(IDURL,0, roleId, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(TESTURL,0,roleId)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 2)]").exists();
    }

    /**
     * 10
     * 增加角色的权限，roleId不存在
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void addRolePrivTest2() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        this.mallClient.post().uri(IDURL,0, 2000,2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());

    }

    /**
     * 11
     * 增加角色的权限，privilegeId不存在
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void addRolePrivTest3() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        this.mallClient.post().uri(IDURL,0,2,10090)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 12
     * 非平台管理员
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void addRolePrivTest4() throws Exception{
        String token =this.adminLogin("8131600001", "123456");
        this.mallClient.post().uri(IDURL,0, 2, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());

    }

}
