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
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PublicTestApp.class)
public class PostDepartsRoles extends BaseTestOomall {

    private static String TESTURL ="/privilege/departs/%d/roles";

    /**
     * 25
     * 不同部门管理员增加角色
     * Li Zihan
     */
    @Test
    public void insertRoleTest1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        byte[] responseString = this.mallClient.post().uri(String.format(TESTURL, 1))
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
     * 23
     * 平台管理员新增角色
     * @author Li Zihan
     */
    @Test
    @Order(2)
    public void insertRoleTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"管理员\"}";
        byte[] responseString = this.mallClient.post().uri(String.format(TESTURL, 0))
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        this.mallClient.get().uri(String.format(TESTURL, 0))
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == \"管理员\")]").exists()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 24
     * 平台管理员新增部门1角色
     * @author Li Zihan
     */
    @Test
    @Order(3)
    public void insertRoleTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        byte[] responseString = this.mallClient.post().uri(String.format(TESTURL, 1))
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        this.mallClient.get().uri(String.format(TESTURL, 1))
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == \"hello\")]").exists()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 24
     * 部门管理员新增部门2角色
     * @author Li Zihan
     */
    @Test
    @Order(4)
    public void insertRoleTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        byte[] responseString = this.mallClient.post().uri(String.format(TESTURL, 2))
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        this.mallClient.get().uri(String.format(TESTURL, 2))
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == \"hello\")]").exists()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 25
     * 未登录新增角色
     * Li Zihan
     */
    @Test
    public void insertRoleTest5() throws Exception {
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"\"}";
        byte[] responseString = this.mallClient.post().uri(String.format(TESTURL, 0))
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 24
     * 无权限管理员新增部门2角色
     */
    @Test
    public void insertRoleTest6() throws Exception {
        String token = this.adminLogin("shop2_adv", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        byte[] responseString = this.mallClient.post().uri(String.format(TESTURL, 2))
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
