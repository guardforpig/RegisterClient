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

import java.util.Objects;

@SpringBootTest(classes = PublicTestApp.class)
public class GetDepartUsersIdTest extends BaseTestOomall {

    private static String TESTURL ="/privilege/departs/%d/users/%d";

    /***
     * 查找用户
     * @throws Exception
     */
    @Test
    public void findUserById1() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        this.mallClient
                .get()
                .uri(String.format(TESTURL,1,46))
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.userName").isEqualTo("8131600001")
                .jsonPath("$.data.sign").isEqualTo(0)
                .returnResult()
                .getResponseBodyContent();

    }

    /***
     * 查找不存在的用户
     * @throws Exception
     */
    @Test
    public void findUserById2() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        this.mallClient
                .get()
                .uri(String.format(TESTURL,0,23))
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /***
     * 查找签名错误用户信息
     * @throws Exception
     */
    @Test
    public void findUserById3() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        String response = new String(Objects.requireNonNull(this.mallClient
                .get()
                .uri(String.format(TESTURL,1, 17341))
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_FALSIFY.getCode())
                .jsonPath("$.data.name").isEqualTo("签名错误")
                .jsonPath("$.data.sign").isEqualTo(1)
                .returnResult()
                .getResponseBodyContent()));
    }

    /***
     * 查找其他店铺用户信息
     * @throws Exception
     */
    @Test
    public void findUserById4() throws Exception {

        String token = this.adminLogin("2721900002", "123456");

        String response = new String(Objects.requireNonNull(this.mallClient
                .get()
                .uri(String.format(TESTURL,1,17342))
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent()));
    }

}
