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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PublicTestApp.class)
public class StatesTest extends BaseTestOomall {

    private static String GROUPURL ="/privilege/groups/sates";
    private static String ROLEURL ="/privilege/roles/sates";
    private static String USERURL ="/privilege/users/sates";
    private static String PRIVURL ="/privilege/privileges/sates";

    /**
     * 28 获得管理员用户的所有状态
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void findPrivilegeState() throws Exception {
        this.mallClient.get().uri(PRIVURL)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2);
    }
    
    /**
     * 28 获得管理员用户的所有状态
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void findAdminUserState() throws Exception {
        this.mallClient.get().uri(USERURL)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(4);
    }

    /**
     * 28 获得管理员用户的所有状态
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void findRoleState() throws Exception {
        this.mallClient.get().uri(ROLEURL)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2);
    }

    /**
     * 28 获得管理员用户的所有状态
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void findGroupState() throws Exception {
        this.mallClient.get().uri(GROUPURL)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2);
    }
}
