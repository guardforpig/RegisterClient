package cn.edu.xmu.oomall.coupon.util;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.microservice.vo.OnsaleVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.ProductVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qingguo Hu 22920192204208
 */
public class CreateObject {

    public static ReturnObject<VoObject> createProductVo(Long id) {
        ProductVo productVo = new ProductVo();
        productVo.setId(id);
        return new ReturnObject<>(productVo);
    }

    public static ReturnObject<List<VoObject>> createOnsaleVoList1() {
        List<VoObject> onSaleVoList = new ArrayList<>();
        OnsaleVo onsaleVo1 = new OnsaleVo();
        onsaleVo1.setId(1L);
        OnsaleVo onsaleVo2 = new OnsaleVo();
        onsaleVo2.setId(2L);
        onSaleVoList.add(onsaleVo1);
        onSaleVoList.add(onsaleVo2);
        return new ReturnObject<>(onSaleVoList);
    }

    public static ReturnObject<List<VoObject>> createOnsaleVoList2() {
        return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
    }


    public static ReturnObject<List<VoObject>> createOnsaleVoList3() {
        List<VoObject> onSaleVoList = new ArrayList<>();
        OnsaleVo onsaleVo1 = new OnsaleVo();
        onsaleVo1.setId(3913L);
        onSaleVoList.add(onsaleVo1);
        return new ReturnObject<>(onSaleVoList);
    }

    public static ReturnObject<List<VoObject>> createOnsaleVoList4() {
        List<VoObject> onSaleVoList = new ArrayList<>();
        OnsaleVo onsaleVo1 = new OnsaleVo();
        onsaleVo1.setId(3914L);
        onSaleVoList.add(onsaleVo1);
        return new ReturnObject<>(onSaleVoList);
    }

}
