package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.LoginVo;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 用户修改自己信息测试类
 *
 * @author 24320182203175 陈晓如
 * createdBy 陈晓如 2020/11/30 13:42
 * modifiedBy 陈晓如 2020/11/30 13:42
 **/
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SelfTest extends BaseTestOomall {

    private static String USERURL = "/privilege/self/users";

    private static String ROLEURL = "/privilege/self/roles";

    private static String BROLEURL = "/privilege/self/baseroles";

    private static String IMGURL = "/privilege/self/users/uploadImg";

    private static String GROUPURL = "/privilege/self/groups";

    private static String PASSURL = "/privilege/self/password";

    private static String RESETURL = "/privilege/self/password/reset";

    /**
     * 查看自己的角色测试1
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserRoleTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        this.mallClient.get().uri(ROLEURL)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 1)]").exists();
    }

    /**
     * 查看自己的角色测试2
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserRoleTest2() throws Exception {
        String token = this.adminLogin("8532600003", "123456");
        this.mallClient.get().uri(ROLEURL)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 4)]").exists();
    }


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
        this.mallClient.put().uri(USERURL)
                .bodyValue(userJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
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
        this.mallClient.put().uri(USERURL).
                header("authorization", token).
                bodyValue(userJson).
                exchange().
                expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(USERURL).
                header("authorization", token).
                exchange().
                expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("oomall")
                .jsonPath("$.data.idnumber").isEqualTo("123456789")
                .jsonPath("$.data.passportNumber").isEqualTo("12345678");
    }

    /**
     * 查询自己的BaseRole
     * @throws Exception
     */
    @Test
    public void getSelfBaseRoleTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        this.mallClient.get().uri(BROLEURL)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 88)]").exists()
                .jsonPath("$.data.list[?(@.id == 89)]").exists()
                .jsonPath("$.data.list[?(@.id == 90)]").exists()
                .jsonPath("$.data.list[?(@.id == 91)]").exists()
                .jsonPath("$.data.list[?(@.id == 92)]").exists();
    }

    /**
     * 查看自己的用户测试
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserTest1() throws Exception {
        String token = this.adminLogin("8532600003", "123456");
        this.mallClient.get().uri(USERURL)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(48);
    }

    /**
     * 重置密码-与原密码相同
     * @throws Exception
     */
    @Test
    @Order(1)
    public void resetPassTest1() throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName("delrole_user2");
        vo.setPassword("123456");
        String requireJson = JacksonUtil.toJson(vo);
        mallClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());


        Map<String, String> name = new HashMap<>();
        name.put("name","changepass");
        String json = JacksonUtil.toJson(name);

        String ret = new String(Objects.requireNonNull(this.mallClient.put().uri(RESETURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBody()), "UTF-8");

        String captcha = JacksonUtil.parseString(ret, "data");
        name.put("captcha", captcha);
        name.put("newPassword", "123456");
        json = JacksonUtil.toJson(name);
        this.mallClient.put().uri(PASSURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.PASSWORD_SAME.getCode());

        vo = new LoginVo();
        vo.setUserName("delrole_user2");
        vo.setPassword("123456");
        requireJson = JacksonUtil.toJson(vo);
        mallClient.post().uri("/privilege/login")
                .bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 重置密码-用户名不存在
     * @throws Exception
     */
    @Test
    public void resetPassTest2() throws Exception {
        Map<String, String> name = new HashMap<>();
        name.put("name","changepassnoexist");
        String json = JacksonUtil.toJson(name);

        this.mallClient.put().uri(RESETURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_ID_NOTEXIST.getCode());
    }

    /**
     * 重置密码-用户不对
     * @throws Exception
     */
    @Test
    public void resetPassTest3() throws Exception {
        Map<String, String> name = new HashMap<>();
        name.put("name","delrole_user2");
        String json = JacksonUtil.toJson(name);

        String ret = new String(Objects.requireNonNull(this.mallClient.put().uri(RESETURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBody()), "UTF-8");

        String captcha = JacksonUtil.parseString(ret, "data");
        name.put("name","delrole_user1");
        name.put("captcha", captcha);
        name.put("newPassword", "123456");
        json = JacksonUtil.toJson(name);
        this.mallClient.put().uri(PASSURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_ACCOUNT.getCode());

        LoginVo vo = new LoginVo();
        vo.setUserName("delrole_user2");
        vo.setPassword("123456");
        String requireJson = JacksonUtil.toJson(vo);
        mallClient.post().uri("/privilege/login")
                .bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        vo = new LoginVo();
        vo.setUserName("delrole_user1");
        vo.setPassword("123456");
        requireJson = JacksonUtil.toJson(vo);
        mallClient.post().uri("/privilege/login")
                .bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }


    /**
     * 重置密码 - 超时
     * @throws Exception
     */
    @Test
    public void resetPassTest4() throws Exception {

        Map<String, String> name = new HashMap<>();
        name.put("name","delrole_user2");
        String json = JacksonUtil.toJson(name);

        String ret = new String(Objects.requireNonNull(this.mallClient.put().uri(RESETURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBody()), "UTF-8");

        String captcha = JacksonUtil.parseString(ret, "data");
        name.put("captcha", captcha);
        name.put("newPassword", "223344");
        json = JacksonUtil.toJson(name);
        Thread.sleep(31000);
        this.mallClient.put().uri(PASSURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_ID_NOTEXIST.getCode());

        LoginVo vo = new LoginVo();
        vo.setUserName("delrole_user2");
        vo.setPassword("123456");
        String requireJson = JacksonUtil.toJson(vo);
        mallClient.post().uri("/privilege/login")
                .bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

    }


    /**
     * 重置密码- 用用户名
     * @throws Exception
     */
    @Test
    @Order(5)
    public void resetPassTest5() throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName("changepass");
        vo.setPassword("123456");
        String requireJson = JacksonUtil.toJson(vo);
        mallClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());


        Map<String, String> name = new HashMap<>();
        name.put("name","changepass");
        String json = JacksonUtil.toJson(name);

        String ret = new String(Objects.requireNonNull(this.mallClient.put().uri(RESETURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBody()), "UTF-8");

        String captcha = JacksonUtil.parseString(ret, "data");
        name.put("captcha", captcha);
        name.put("newPassword", "223344");
        json = JacksonUtil.toJson(name);
        this.mallClient.put().uri(PASSURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        vo = new LoginVo();
        vo.setUserName("changepass");
        vo.setPassword("223344");
        requireJson = JacksonUtil.toJson(vo);
        mallClient.post().uri("/privilege/login")
                .bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

    }

    /**
     * 重置密码- 用邮箱
     * @throws Exception
     */
    @Test
    @Order(5)
    public void resetPassTest6() throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName("2235d@1245f");
        vo.setPassword("123456");
        String requireJson = JacksonUtil.toJson(vo);
        mallClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());


        Map<String, String> name = new HashMap<>();
        name.put("name","2235d@1245f");
        String json = JacksonUtil.toJson(name);

        String ret = new String(Objects.requireNonNull(this.mallClient.put().uri(RESETURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBody()), "UTF-8");

        String captcha = JacksonUtil.parseString(ret, "data");
        name.put("captcha", captcha);
        name.put("newPassword", "223344");
        json = JacksonUtil.toJson(name);
        this.mallClient.put().uri(PASSURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        vo = new LoginVo();
        vo.setUserName("changepass");
        vo.setPassword("223344");
        requireJson = JacksonUtil.toJson(vo);
        mallClient.post().uri("/privilege/login")
                .bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

    }

}

