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

    public static ReturnObject<ProductVo> createProductVo(Long id) {
        ProductVo productVo = new ProductVo();
        productVo.setId(id);
        return new ReturnObject<>(productVo);
    }

    public static ReturnObject<List<OnsaleVo>> createOnsaleVoList1() {
        List<OnsaleVo> onSaleVoList = new ArrayList<>();
        OnsaleVo onsaleVo1 = new OnsaleVo();
        onsaleVo1.setId(1L);
        onSaleVoList.add(onsaleVo1);
        OnsaleVo onsaleVo2 = new OnsaleVo();
        onsaleVo2.setId(2L);
        onSaleVoList.add(onsaleVo2);
        OnsaleVo onsaleVo3 = new OnsaleVo();
        onsaleVo3.setId(3L);
        onSaleVoList.add(onsaleVo3);
        OnsaleVo onsaleVo4 = new OnsaleVo();
        onsaleVo4.setId(4L);
        onSaleVoList.add(onsaleVo4);
        OnsaleVo onsaleVo5 = new OnsaleVo();
        onsaleVo5.setId(5L);
        onSaleVoList.add(onsaleVo5);
        OnsaleVo onsaleVo6 = new OnsaleVo();
        onsaleVo6.setId(6L);
        onSaleVoList.add(onsaleVo6);
        OnsaleVo onsaleVo7 = new OnsaleVo();
        onsaleVo7.setId(7L);
        onSaleVoList.add(onsaleVo7);
        OnsaleVo onsaleVo8 = new OnsaleVo();
        onsaleVo8.setId(8L);
        onSaleVoList.add(onsaleVo8);
        OnsaleVo onsaleVo9 = new OnsaleVo();
        onsaleVo9.setId(9L);
        onSaleVoList.add(onsaleVo9);
        OnsaleVo onsaleVo10 = new OnsaleVo();
        onsaleVo10.setId(10L);
        onSaleVoList.add(onsaleVo10);
        OnsaleVo onsaleVo11 = new OnsaleVo();
        onsaleVo11.setId(11L);
        onSaleVoList.add(onsaleVo11);
        OnsaleVo onsaleVo12 = new OnsaleVo();
        onsaleVo12.setId(12L);
        onSaleVoList.add(onsaleVo12);

        return new ReturnObject<>(onSaleVoList);
    }

    public static ReturnObject<List<OnsaleVo>> createOnsaleVoList2() {
        return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
    }


    public static ReturnObject<List<OnsaleVo>> createOnsaleVoList3() {
        List<OnsaleVo> onSaleVoList = new ArrayList<>();
        OnsaleVo onsaleVo1 = new OnsaleVo();
        onsaleVo1.setId(3913L);
        onSaleVoList.add(onsaleVo1);
        return new ReturnObject<>(onSaleVoList);
    }

    public static ReturnObject<List<OnsaleVo>> createOnsaleVoList4() {
        List<OnsaleVo> onSaleVoList = new ArrayList<>();
        OnsaleVo onsaleVo1 = new OnsaleVo();
        onsaleVo1.setId(3914L);
        onSaleVoList.add(onsaleVo1);
        return new ReturnObject<>(onSaleVoList);
    }

    public static ReturnObject<OnsaleVo> createOnsaleVo1() {
        return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, ReturnNo.RESOURCE_ID_NOTEXIST.getMessage());
    }

    public static ReturnObject<OnsaleVo> createOnsaleVo2() {
        OnsaleVo onsaleVo = new OnsaleVo();
        onsaleVo.setId(1L);
        onsaleVo.setShopId(3L);
        return new ReturnObject<>(onsaleVo);
    }

    public static ReturnObject<OnsaleVo> createOnsaleVo3() {
        OnsaleVo onsaleVo = new OnsaleVo();
        onsaleVo.setId(2L);
        onsaleVo.setShopId(2L);
        return new ReturnObject<>(onsaleVo);
    }

    public static ReturnObject<OnsaleVo> createOnsaleVo4() {
        OnsaleVo onsaleVo = new OnsaleVo();
        onsaleVo.setId(3912L);
        onsaleVo.setShopId(2L);
        return new ReturnObject<>(onsaleVo);
    }
}
