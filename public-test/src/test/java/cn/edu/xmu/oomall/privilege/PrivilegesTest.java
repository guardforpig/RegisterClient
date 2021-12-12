/**
 * Copyright School of Informatics Xiamen University
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.oomall.privilege.vo.PrivilegeRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest(classes = PublicTestApp.class)
public class PrivilegesTest extends BaseTestOomall {

    private static String TESTURL = "/privilege/departs/{did}}/privileges";
    private static String IDURL = "/privilege/departs/{did}}/privileges/{id}";

    /**
     * 获取所有权限（第一页）
     *
     * @throws Exception
     */
    @Test
    public void getAllPriv1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.mallClient.get().uri(TESTURL + "?page=1&pageSize=11", 0).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.list.length()").isEqualTo(11)
                .returnResult().getResponseBodyContent();
    }

    /**
     * 获取所有权限（第一页）
     *
     * @throws Exception
     */
    @Test
    public void getAllPriv2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.mallClient.get().uri(TESTURL + "?name=删除用户", 0).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.list[?(@.id == 4 && @.name == \"删除用户\" && @.requestType == 3)]").exists();
    }

    /**
     * 未登录
     *
     * @throws Exception
     */
    @Test
    public void getAllPriv3() throws Exception {
        this.mallClient.get().uri(TESTURL, 0)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.data.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult().getResponseBodyContent();

    }

    /**
     * 获取所有权限（第二页）
     *
     * @throws Exception
     */
    @Test
    public void getAllPriv4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.mallClient.get().uri(TESTURL + "?page=2&pageSize=14", 0).
                header("authorization", token).
                exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(14)
                .returnResult().getResponseBodyContent();
    }

    /**
     * 非平台管理员
     *
     * @throws Exception
     */
    @Test
    public void getAllPriv5() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.mallClient.get().uri(TESTURL + "?page=2&pageSize=10", 0).header("authorization", token).exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 未登录
     *
     * @throws Exception
     */
    @Test
    public void addPrivTest1() throws Exception {
        String privJson = "{\"name\":\"测试改变\", \"url\": \"/adminusers/{id}/testChange\", \"requestType\": \"0\"}";
        this.mallClient.post().uri(TESTURL)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult().getResponseBodyContent();
    }


    /**
     * 非平台管理员
     *
     * @throws Exception
     */
    @Test
    public void addPrivTest2() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String privJson = "{\"name\":\"测试改变\", \"url\": \"/adminusers/{id}/testChange\", \"requestType\": \"0\"}";
        this.mallClient.post().uri(TESTURL)
                .header("authorization", token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 1
     * URL和Type重复
     */
    @Test
    public void addPrivTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String privJson = "{\"name\":\"测试改变\", \"url\": \"/privilege/departs/{id}/users/{id}\", \"requestType\": 0}";
        this.mallClient.post().uri(TESTURL).header("authorization", token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.URL_SAME.getCode());
    }

    /**
     * 权限增加成功
     *
     * @throws Exception
     */
    @Test
    @Order(1)
    public void addPrivTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String privJson = "{\"name\":\"测试改变\", \"url\": \"/adminusers/{id}/testChange\", \"requestType\": \"0\"}";
        this.mallClient.post().uri(TESTURL)
                .header("authorization", token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.url").isEqualTo("/adminusers/{id}/testChange")
                .jsonPath("$.data.requestType").isEqualTo(0);

        this.mallClient.get().uri(TESTURL + "?name=测试改变", 0).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.list[?(@.name == \"测试改变\")]").exists();
    }

    /**
     * 修改成功
     * @throws Exception
     */
    @Test
    @Order(2)
    public void changePrivTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String result = new String(
                this.mallClient.get().uri(TESTURL + "?name=测试改变", 0)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                        .jsonPath("$.data.list[?(@.name == \"测试改变\")]").exists()
                        .returnResult().getResponseBodyContent(), "UTF-8");

        String data = JacksonUtil.parseString(result, "data");
        List<PrivilegeRetVo> list = JacksonUtil.parseObjectList(data, "list", PrivilegeRetVo.class);
        assertEquals(1, list.size());
        Long privId = list.get(0).getId();

        String privJson = "{\"name\":\"测试改变1\", \"url\": \"/users/{id}/testChange\"}";
        this.mallClient.put().uri(IDURL, 0, privId).header("authorization", token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("测试改变")
                .jsonPath("$.data.url").isEqualTo("/users/{id}/testChange");

        this.mallClient.get().uri(TESTURL + "?url=/users/{id}/testChange", 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == \"测试改变1\")]").exists();

    }

    /**
     * 2
     * 修改权限，非平台管理员
     *
     * @throws Exception
     * @author 张湘君 24320182203327
     */
    @Test
    public void changePrivTest2() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String privJson = "{\"url\": \"/privilege/departs/{id}/users/{id}\", \"requestType\": 2}";
        this.mallClient.put().uri(IDURL,0,2)
                .header("authorization", token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 3
     * 修改权限，url为空
     *
     * @throws Exception
     * @author 张湘君 24320182203327
     */
    @Test
    public void changePrivTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String privJson = "{\"url\": null}";
        this.mallClient.put().uri(IDURL,0,2)
                .header("authorization", token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isBadRequest();
    }


    /**
     * 4
     * 修改权限，requestType为空
     *
     * @throws Exception
     * @author 张湘君 24320182203327
     */
    @Test
    public void changePrivTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String privJson = "{\"url\": \"/adminusers/{id}/testChange\", \"requestType\": null}";
        this.mallClient.put().uri(IDURL,0,2)
                .header("authorization", token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * 5
     * 修改权限，requestType数值错误
     *
     * @throws Exception
     * @author 张湘君 24320182203327
     */
    @Test
    public void changePrivTest5() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String privJson = "{\"name\":\"测试修改\", \"url\": \"/adminusers/{id}/testChange\", \"requestType\": \"7\"}";
        this.mallClient.put().uri(IDURL,0,2)
                .header("authorization", token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * 6
     * 修改权限，输入了重复的url和requestType
     *
     * @throws Exception
     * @author 张湘君 24320182203327
     */
    @Test
    public void changePrivTest6() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String privJson = "{\"url\": \"/privilege/departs/{id}/users/{id}\", \"requestType\": 2}";
        this.mallClient.put().uri(IDURL,0,2)
                .header("authorization", token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.URL_SAME.getCode());
    }
}
