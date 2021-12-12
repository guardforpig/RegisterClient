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
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PublicTestApp.class)
public class SelfRolesTest extends BaseTestOomall {

    private static String TESTURL = "/privilege/self/roles";

    /**
     * 查看自己的角色测试1
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserRoleTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = this.mallClient.get().uri(TESTURL).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 1)]").exists()
                .returnResult().getResponseBodyContent();
    }

    /**
     * 查看自己的角色测试2
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserRoleTest2() throws Exception {
        String token = this.adminLogin("8532600003", "123456");
        byte[] responseString = this.mallClient.get().uri(TESTURL).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == 4)]").exists()
                .returnResult().getResponseBodyContent();
    }
}
