package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.oomall.privilege.vo.UserProxyRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Li Zihan 24320182203227
 * @date Created in 2020/12/9 12:33
 **/
@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepartsProxiesTest extends BaseTestOomall {

    private static String DEPARTURL ="/privilege/departs/{id}/proxies";
    private static String DEPARTIDURL = "/departs/{did}/proxies/{id}";
    private static String PROXYURL ="/privilege/users/{id}/proxies";
    private static String IDURL ="/privilege/departs/{did}/users/{aid}/proxyusers/{bid}";
    private static String SELFPROXYURL = "/privilege/self/proxies";
    private static String SELFIDURL = "/privilege/self/proxies/{id}";


    /**
     * 1
     * 不登录查询所有用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies1() throws Exception {

        this.mallClient.get().uri(DEPARTURL,1)
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
    @Order(1)
    public void getListProxies2() throws Exception {

        String token = this.adminLogin("13088admin", "123456");
        this.mallClient.get().uri(DEPARTURL,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.proxyUser.id == 17332 && @.user.id == 17337)]").exists();
    }

    /**
     * 3
     * 店铺管理员查询自己部门所有用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    @Order(1)
    public void getListProxies3() throws Exception {

        String token = this.adminLogin("8131600001", "123456");
        this.mallClient.get().uri(DEPARTURL,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.proxyUser.id == 17332 && @.user.id == 17337)]").exists();
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
        this.mallClient.get().uri(DEPARTURL,2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
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
        this.mallClient.get().uri(DEPARTURL,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 7
     * 伪造token查询所有用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies7() throws Exception {
        this.mallClient.get().uri(DEPARTURL,0)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }

    /**
     * 1
     * 不登录查询自己用户代理关系
     *
     */
    @Test
    public void getSelfProxies1() throws Exception {

        this.mallClient.get().uri(SELFPROXYURL)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 2
     * 查询自己的代理关系
     *
     */
    @Test
    @Order(1)
    public void getSelfProxies2() throws Exception {

        String token = this.adminLogin("shop1_proxy", "123456");
        this.mallClient.get().uri(SELFPROXYURL)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.proxyUser.id == 17332 && @.user.id == 17337)]").exists();
    }

    /**
     * 7
     * 伪造token查询代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getSelfProxies3() throws Exception {
        this.mallClient.get().uri(SELFPROXYURL)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }


    /**
     * 1
     * 不登录创建代理关系
     *
     */
    @Test
    public void createProxies1() throws Exception {

        this.mallClient.post().uri(PROXYURL,17330)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 1
     * 代理不同店的用户
     *
     */
    @Test
    public void createProxies2() throws Exception {

        String token = this.adminLogin("proxy_user1", "123456");
        this.mallClient.post().uri(PROXYURL,17333)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 1
     * 代理平台管理员
     *
     */
    @Test
    public void createProxies3() throws Exception {

        String token = this.adminLogin("proxy_user1", "123456");
        this.mallClient.post().uri(PROXYURL,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 1
     * 代理成功
     *
     */
    @Test
    @Order(2)
    public void createProxies4() throws Exception {

        String token = this.adminLogin("proxy_user1", "123456");
        //代理前无权限
        this.mallClient.get().uri(DEPARTURL, 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        String token1 = this.adminLogin("shop1_auth", "123456");
        this.mallClient.post().uri(PROXYURL, 17351)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        //代理后权限
        this.mallClient.get().uri(DEPARTURL, 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 1
     * 不登录创建代理关系
     *
     */
    @Test
    public void createDepartsProxies1() throws Exception {

        this.mallClient.post().uri(IDURL,1,17351,17330)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 1
     * 代理不同店的用户
     *
     */
    @Test
    public void createDepartsProxies2() throws Exception {

        String token = this.adminLogin("shop1_auth", "123456");
        this.mallClient.post().uri(IDURL,2,17353,17333)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 1
     * 代理平台管理员
     *
     */
    @Test
    public void createDepartsProxies3() throws Exception {

        String token = this.adminLogin("shop1_auth", "123456");
        this.mallClient.post().uri(IDURL,1,17351,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 1
     * 无权限
     *
     */
    @Test
    public void createDepartsProxies4() throws Exception {

        String token = this.adminLogin("shop1_coupon", "123456");
        this.mallClient.post().uri(IDURL,1,17351,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 1
     * 代理成功
     *
     */
    @Test
    @Order(3)
    public void createDepartProxies5() throws Exception {

        String token1 = this.adminLogin("proxy_user2", "123456");
        //代理前无权限
        this.mallClient.get().uri(DEPARTURL, 2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        String token = this.adminLogin("shop2_auth", "123456");
        this.mallClient.post().uri(IDURL,2,17353,17333)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        //代理后权限
        this.mallClient.get().uri(DEPARTURL, 2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 1
     * 不登录删除自己用户代理关系
     *
     */
    @Test
    public void delSelfProxies1() throws Exception {

        this.mallClient.delete().uri(SELFIDURL, 6)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 2
     * 删除不是自己的用户代理关系
     *
     */
    @Test
    public void delSelfProxies2() throws Exception {
        String token = this.adminLogin("proxy_user1", "123456");
        this.mallClient.delete().uri(SELFIDURL, 6)
                .header("authorization", token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 2
     * 删除不是自己的用户代理关系, 及时时平台管理员也不行
     *
     */
    @Test
    public void delSelfProxies3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.mallClient.delete().uri(SELFIDURL, 6)
                .header("authorization", token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 7
     * 伪造token删除代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void delSelfProxies4() throws Exception {
        this.mallClient.delete().uri(SELFIDURL)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }


    /**
     * 2
     * 删除自己的用户代理关系
     * 在createProxies4之后运行
     */
    @Test
    @Order(4)
    public void delSelfProxies5() throws Exception {

        String token = this.adminLogin("shop1_auth", "123456");
        //删除前
        String result = new String(Objects.requireNonNull(this.mallClient.get().uri(SELFPROXYURL)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.proxyUser.id == 17351 && @.user.id == 17330)]").exists()
                .returnResult()
                .getResponseBodyContent()), "UTF-8");

        List<UserProxyRetVo> list = JacksonUtil.parseObjectList(result,"data",UserProxyRetVo.class);
        Long id = null;
        for (UserProxyRetVo vo : list){
            if (vo.getCreator().getId().equals(17330)){
                id = vo.getId();
                break;
            }
        }
        assertNotNull(id);

        String token1 = this.adminLogin("proxy_user1", "123456");
        //删除前有权限
        this.mallClient.get().uri(DEPARTURL, 1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());


        this.mallClient.delete().uri(SELFIDURL, id)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //删除后无权限
        this.mallClient.get().uri(DEPARTURL, 1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

    }

    /**
     * 1
     * 不登录删除自己用户代理关系
     *
     */
    @Test
    public void delProxies1() throws Exception {

        this.mallClient.delete().uri(DEPARTIDURL, 2,7)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 2
     * 删除不是同部门的用户代理关系
     *
     */
    @Test
    public void delProxies2() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.mallClient.delete().uri(DEPARTIDURL, 2,7)
                .header("authorization", token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }


    /**
     * 7
     * 伪造token删除代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void delProxies4() throws Exception {
        this.mallClient.delete().uri(DEPARTIDURL, 2,7)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }


    /**
     * 2
     * 删除同部门的用户代理关系
     * 在createDepartProxies5之后运行
     */
    @Test
    @Order(4)
    public void delProxies5() throws Exception {

        String token = this.adminLogin("shop2_auth", "123456");
        //删除前
        String result = new String(Objects.requireNonNull(this.mallClient.get().uri(SELFPROXYURL)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.proxyUser.id == 17352 && @.user.id == 17333)]").exists()
                .returnResult()
                .getResponseBodyContent()), "UTF-8");

        List<UserProxyRetVo> list = JacksonUtil.parseObjectList(result,"data",UserProxyRetVo.class);
        Long id = null;
        for (UserProxyRetVo vo : list){
            if (vo.getCreator().getId().equals(17330)){
                id = vo.getId();
                break;
            }
        }
        assertNotNull(id);

        String token1 = this.adminLogin("proxy_user2", "123456");
        //删除前有权限
        this.mallClient.get().uri(DEPARTURL, 1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());


        String token3 = this.adminLogin("2721900002", "123456");
        this.mallClient.delete().uri(DEPARTIDURL, 2, id)
                .header("authorization", token3)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //删除后无权限
        this.mallClient.get().uri(DEPARTURL, 1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

    }


}