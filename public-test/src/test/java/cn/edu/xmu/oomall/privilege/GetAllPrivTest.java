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
public class GetAllPrivTest extends BaseTestOomall {

    private static String TESTURL ="/departs/%d/privileges";
    /**
     * 获取所有权限（第一页）
     * @throws Exception
     */
    @Test
    public void getAllPriv1() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = this.mallClient.get().uri(String.format(TESTURL, 0)+"?page=1&pageSize=11").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[?(@.list.length() == 11)]").exists()
                .returnResult().getResponseBodyContent();
    }

    @Test
    public void getAllPriv2() throws Exception {
        this.mallClient.get().uri(String.format(TESTURL, 0))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /**
     * 获取所有权限（第二页）
     * @throws Exception
     */
    @Test
    public void getAllPriv3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.mallClient.get().uri(String.format(TESTURL, 0)+"?page=2&pageSize=14").header("authorization", token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.[?(@.list.length() == 14)]").exists()
                .returnResult().getResponseBodyContent();
    }

    /**
     * 非平台管理员
     * @throws Exception
     */
    @Test
    public void getAllPriv4() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.mallClient.get().uri(String.format(TESTURL, 0)+"?page=2&pageSize=10").header("authorization", token).exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult().getResponseBodyContent();
    }

}
