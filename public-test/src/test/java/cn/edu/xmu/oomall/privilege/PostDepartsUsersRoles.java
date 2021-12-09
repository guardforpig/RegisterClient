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
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PublicTestApp.class)
public class PostDepartsUsersRoles extends BaseTestOomall {

    private static String TESTURL ="/privilege/users/%d/roles/%d";

    /**
     * 16
     * 赋予用户角色测试- 平台管理员
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void assignRoleTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.mallClient.post().uri(String.format(TESTURL,17343, 104)).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBodyContent();

        this.mallClient.post().uri("/privilege/users/17343/roles").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 104 && @.sign == 0) ]").exists()
                .returnResult().getResponseBodyContent();
    }

    /**
     * 17
     * 赋予用户角色测试- 同店店铺管理员
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void assignRoleTest2() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.mallClient.post().uri(String.format(TESTURL,17345, 105)).header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult().getResponseBodyContent();

        this.mallClient.post().uri("/privilege/users/17345/roles").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 105 && @.sign == 0) ]").exists()
                .returnResult().getResponseBodyContent();

    }

    /**
     * 18
     * Li Zihan
     * 赋予用户角色测试3- 不同店管理员
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void assignRoleTest3() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        byte[] responseString = this.mallClient.post().uri(String.format(TESTURL,17345, 107)).header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult().getResponseBodyContent();

        this.mallClient.post().uri("/privilege/users/17345/roles").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 107)]").doesNotExist()
                .returnResult().getResponseBodyContent();
    }

}
