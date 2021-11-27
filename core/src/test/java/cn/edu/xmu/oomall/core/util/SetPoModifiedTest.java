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

package cn.edu.xmu.oomall.core.util;

import cn.edu.xmu.oomall.shop.model.po.CouponActivityPo;
import cn.edu.xmu.oomall.shop.model.po.Nonepo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class SetPoModifiedTest {
    @Test
    public void testSetPoModified1() {
        CouponActivityPo po = new CouponActivityPo();
        boolean modifyFlag = Common.setPoModifiedFields(po, 2L, "lisi");
        assertEquals(true, modifyFlag);
        assertEquals(2L, po.getModifierId());
        assertEquals("lisi", po.getModifierName());
    }

    @Test
    public void testSetPoModified2() {
        CouponActivityPo po = new CouponActivityPo();
        boolean modifyFlag2 = Common.setPoModifiedFields(po, 3L, "lisi");
        assertEquals(true, modifyFlag2);
        assertEquals(3L, po.getModifierId());
        assertEquals("lisi", po.getModifierName());
    }

    @Test
    public void testSetPoModified3() {
        CouponActivityPo po = new CouponActivityPo();
        boolean modifyFlag3 = Common.setPoModifiedFields(po, 3L, "zhaosi");
        assertEquals(true, modifyFlag3);
        assertEquals(3L, po.getModifierId());
        assertEquals("zhaosi", po.getModifierName());
    }

    @Test
    public void testSetPoModified4() {

        Nonepo po2=new Nonepo();
        boolean modifyFlag4 = Common.setPoModifiedFields(po2, 3L, "zhaosi");
        assertEquals(false, modifyFlag4);
        assertNotEquals(3L, po2.getId());
        assertNotEquals("zhaosi", po2.getName());
    }
}
