package cn.edu.xmu.oomall.shop.service;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.shop.dao.ShopDao;
import cn.edu.xmu.oomall.shop.microservice.PrivilegeService;
import cn.edu.xmu.oomall.shop.microservice.ReconciliationService;
import cn.edu.xmu.oomall.shop.microservice.vo.RefundDepositVo;
import cn.edu.xmu.oomall.shop.model.bo.Shop;
import cn.edu.xmu.oomall.shop.model.po.ShopAccountPo;
import cn.edu.xmu.oomall.shop.model.po.ShopPo;
import cn.edu.xmu.oomall.shop.model.vo.*;
import cn.edu.xmu.oomall.shop.microservice.PaymentService;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

@Service
public class ShopService {
    @Autowired
    private ShopDao shopDao;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReconciliationService reconciliationService;

    @Autowired
    private PrivilegeService privilegeService;

    private static JwtHelper jwtHelper = new JwtHelper();

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getShopPoByShopId(Long ShopId) {
        return shopDao.getShopPoById(ShopId);
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<PageInfo<Object>> getAllShop(Integer page, Integer pageSize) {
        List<ShopPo> shopPos = (List<ShopPo>) shopDao.getAllShop(page, pageSize).getData();
        List<Object> shopRetVos = new ArrayList<>();
        for (ShopPo po : shopPos) {
            shopRetVos.add(po);
        }
        //分页查询
        PageInfo<Object> shopRetVoPageInfo = PageInfo.of(shopRetVos);
        ShopAllRetVo shopAllRetVo = new ShopAllRetVo();
        shopAllRetVo.setPage(Long.valueOf(page));
        shopAllRetVo.setPageSize(Long.valueOf(pageSize));
        shopAllRetVo.setPages((long) shopRetVoPageInfo.getPages());
        shopAllRetVo.setTotal(shopAllRetVo.getTotal());
        shopAllRetVo.setList(shopRetVos);
        return new ReturnObject<>(shopRetVoPageInfo);
    }


    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getSimpleShopByShopId(Long ShopId) {
        ReturnObject ret = shopDao.getShopPoById(ShopId);
        if (ret.getCode() != ReturnNo.OK) return ret;
        ShopSimpleRetVo vo = (ShopSimpleRetVo) cloneVo(ret.getData(), ShopSimpleRetVo.class);
        return new ReturnObject(vo);
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject newShop(ShopVo shopVo, Long loginUser, String loginUsername) {
        ShopPo po = new ShopPo();
        po.setName(shopVo.getName());
        setPoCreatedFields(po, loginUser, loginUsername);
        ReturnObject ret = shopDao.newShop(po);
        if (ret.getCode().equals(ReturnNo.OK)) {
            ShopSimpleRetVo vo = cloneVo(ret.getData(), ShopSimpleRetVo.class);
            //todo:还有问题
            String adminToken = jwtHelper.createToken(1L, "13008admin", 0L, 0, 3600);
            privilegeService.addToDepart(loginUser, vo.getId(), adminToken);
            ret = new ReturnObject(vo);
        }
        return ret;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getShopStates() {
        return shopDao.getShopState();
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateShop(Long id, ShopVo shopVo, Long loginUser, String loginUsername) {
        Shop shop = new Shop();
        shop.setId(id);
        shop.setName(shopVo.getName());
        setPoModifiedFields(shop, loginUser, loginUsername);

        ReturnObject ret = shopDao.UpdateShop(shop.getId(), shop);
        return ret;
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteShopById(Long id, Long loginUser, String loginUsername) {
        ReturnObject returnObject = getShopPoByShopId(id);
        if (!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        ShopPo shopPo = (ShopPo) returnObject.getData();
        if (shopPo.getState() != Shop.State.OFFLINE.getCode().byteValue()) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
//        InternalReturnObject ret = reconciliationService.isClean(id);
//        if (!ret.getErrno().equals(0)) {
//            return new ReturnObject(ReturnNo.getByCode(ret.getErrno()), ret.getErrmsg());
//        }
//
//        Boolean result = (Boolean) ret.getData();
//        if (!result) {
//            //商铺尚未完成清算
//            return new ReturnObject(ReturnNo.SHOP_NOT_RECON);
//        }
//
//        //商铺已完成清算
//        /*****************************************/
//        //TODO:需要调用Shp[AccountDao获得
//        ShopAccountPo accountPo = new ShopAccountPo();
//        accountPo.setAccount("11111111");
//        accountPo.setType((byte) 0);
//        accountPo.setName("测试");
//        /*******************************************/
//        RefundDepositVo depositVo = (RefundDepositVo) cloneVo(accountPo, RefundDepositVo.class);
//        InternalReturnObject refundRet = paymentService.refund(depositVo);
//        if (!refundRet.getErrno().equals(0)) {
//            return new ReturnObject(ReturnNo.getByCode(refundRet.getErrno()), refundRet.getErrmsg());
//        }

        //退还保证金
        Shop shop = new Shop();
        shop.setId(id.longValue());
        shop.setState(Shop.State.FORBID.getCode().byteValue());
        setPoModifiedFields(shop, loginUser, loginUsername);
        ReturnObject retUpdate = shopDao.updateShopState(shop);
        return retUpdate;

    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject passShop(Long id, ShopConclusionVo conclusion, Long loginUser, String loginUsername) {
        ReturnObject returnObject = getShopPoByShopId(id);
        if (!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        ShopPo shopPo = (ShopPo) returnObject.getData();
        if (shopPo.getState() != Shop.State.EXAME.getCode().byteValue()) {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        Shop shop = new Shop();
        shop.setId(id.longValue());
        setPoModifiedFields(shop, loginUser, loginUsername);
        shop.setState(conclusion.getConclusion() == true ? Shop.State.OFFLINE.getCode().byteValue() : Shop.State.EXAME.getCode().byteValue());
        ReturnObject ret = shopDao.updateShopState(shop);
        return ret;
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject onShelfShop(Long id, Long loginUser, String loginUsername) {
        Shop shop = new Shop();
        shop.setId(id);
        setPoModifiedFields(shop, loginUser, loginUsername);
        shop.setState(Shop.State.ONLINE.getCode().byteValue());
        ReturnObject returnObject = getShopPoByShopId(id);
        if (!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        ShopPo shopPo = (ShopPo) returnObject.getData();

        if (shopPo.getState() == Shop.State.OFFLINE.getCode().byteValue()) {
            ReturnObject ret = shopDao.updateShopState(shop);
            return ret;
        } else {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public ReturnObject offShelfShop(Long id, Long loginUser, String loginUsername) {
        Shop shop = new Shop();
        shop.setId(id.longValue());
        setPoModifiedFields(shop, loginUser, loginUsername);
        shop.setState(Shop.State.OFFLINE.getCode().byteValue());
        ReturnObject returnObject = getShopPoByShopId(id);
        if (!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        ShopPo shopPo = (ShopPo) returnObject.getData();

        if (shopPo.getState() == Shop.State.ONLINE.getCode().byteValue()) {
            ReturnObject ret = shopDao.updateShopState(shop);
            return ret;
        } else {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }
}