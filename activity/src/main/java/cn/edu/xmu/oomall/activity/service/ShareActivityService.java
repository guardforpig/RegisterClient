package cn.edu.xmu.oomall.activity.service;

import cn.edu.xmu.oomall.activity.dao.ShareActivityDao;
import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.microservice.ShopService;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleSaleInfoVo;
import cn.edu.xmu.oomall.activity.microservice.vo.ShopInfoVo;
import cn.edu.xmu.oomall.activity.model.bo.ShareActivityBo;
import cn.edu.xmu.oomall.activity.model.bo.ShareActivityStatesBo;
import cn.edu.xmu.oomall.activity.model.vo.*;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/11/12 12:55
 */
@Service
public class ShareActivityService {

    @Autowired
    private ShareActivityDao shareActivityDao;

    @Autowired
    private GoodsService goodsService;

    @Resource
    private ShopService shopService;

    /**
     * 获得分享活动的所有状态
     *
     * @return ReturnObject
     */
    public ReturnObject getShareState() {
        List<RetStatesVo> list = new ArrayList<>();
        for (ShareActivityStatesBo value : ShareActivityStatesBo.values()) {
            RetStatesVo retStatesVO = new RetStatesVo(value.getCode(), value.getValue());
            list.add(retStatesVO);
        }
        return new ReturnObject(list);
    }

    /**
     * 显示不同（所有/上线）状态的分享活动
     *
     * @param shopId    店铺Id
     * @param productId 货品
     * @param beginTime 晚于此开始时间
     * @param endTime   早于此结束时间
     * @param state     分享活动状态
     * @param page      页码
     * @param pageSize  每页数目
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getShareByShopId(Long shopId, Long productId, LocalDateTime beginTime,
                                         LocalDateTime endTime, Byte state, Integer page, Integer pageSize) {
        ShareActivityBo bo = new ShareActivityBo();
        bo.setShopId(shopId);
        bo.setBeginTime(beginTime);
        bo.setEndTime(endTime);
        if (state != null) {
            bo.setState(state);
        }
        List<Long> shareActivityIds = new ArrayList<>();
        if (productId != null) {
            //TODO:openfeign获得分享活动id
            InternalReturnObject<Map<String, Object>> onSalesByProductId = goodsService.getOnSales(shopId, productId, null, null, 1, 10);
            if (onSalesByProductId.getData() == null) {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,onSalesByProductId.getErrmsg());
            }
            int total = (int) onSalesByProductId.getData().get("total");
            if (total != 0) {
                onSalesByProductId = goodsService.getOnSales(shopId, productId, null, null, 1, total > 500 ? 500 : total);
                if (onSalesByProductId.getData() == null) {
                    return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,onSalesByProductId.getErrmsg());
                }
                List<SimpleSaleInfoVo> list = (List<SimpleSaleInfoVo>) onSalesByProductId.getData().get("list");
                for (SimpleSaleInfoVo simpleSaleInfoVO : list) {
                    if (simpleSaleInfoVO.getShareActId() != null) {
                        shareActivityIds.add(simpleSaleInfoVO.getShareActId());
                    }
                }
            }else {
                return new ReturnObject(ReturnNo.OK,onSalesByProductId.getErrmsg(),onSalesByProductId.getData());
            }
        }
        ReturnObject shareByShopId = shareActivityDao.getShareByShopId(bo, shareActivityIds, page, pageSize);
        return shareByShopId;
    }


    /**
     * 管理员新增分享活动
     *
     * @param createName      创建者姓名
     * @param createId        创建者id
     * @param shopId          商铺id
     * @param shareActivityVo 新增商铺内容
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject addShareAct(String createName, Long createId,
                                    Long shopId, ShareActivityVo shareActivityVo) {
        ShareActivityBo shareActivityBo = (ShareActivityBo) Common.cloneVo(shareActivityVo, ShareActivityBo.class);
        Common.setPoCreatedFields(shareActivityBo, createId, createName);
        Common.setPoModifiedFields(shareActivityBo, createId, createName);
        shareActivityBo.setState(ShareActivityStatesBo.DRAFT.getCode());
        shareActivityBo.setShopId(shopId);
        //TODO:通过商铺id弄到商铺名称
        InternalReturnObject<ShopInfoVo> shop = shopService.getShop(shopId);
        if (shop.getData() == null) {
            return new ReturnObject<>(ReturnNo.FIELD_NOTVALID, "不存在该商铺");
        }
        String shopName = shop.getData().getName();
        shareActivityBo.setShopName(shopName);

        ReturnObject returnObject = shareActivityDao.addShareAct(shareActivityBo);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        ShareActivityBo shareActivityBo1 = (ShareActivityBo) returnObject.getData();
        RetShareActivityInfoVo retShareActivityInfoVo = (RetShareActivityInfoVo) Common.cloneVo(shareActivityBo1, RetShareActivityInfoVo.class);
        retShareActivityInfoVo.setShop(new ShopVo(shareActivityBo1.getShopId(), shareActivityBo1.getShopName()));
        return new ReturnObject(retShareActivityInfoVo);
    }

    /**
     * 查看分享活动详情 只显示上线状态的分享活动
     *
     * @param id 分享活动Id
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getShareActivityById(Long id) {
        ReturnObject returnObject = shareActivityDao.getShareActivityById(id);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        ShareActivityBo shareActivityBo = (ShareActivityBo) returnObject.getData();
        RetShareActivityInfoVo retShareActivityInfoVo = (RetShareActivityInfoVo) Common.cloneVo(shareActivityBo, RetShareActivityInfoVo.class);
        retShareActivityInfoVo.setShop(new ShopVo(shareActivityBo.getShopId(), shareActivityBo.getShopName()));
        return new ReturnObject(retShareActivityInfoVo);
    }

    /**
     * 查看特定分享活动详情,显示所有状态的分享活动
     *
     * @param shopId 店铺Id
     * @param id     分享活动Id
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getShareActivityByShopIdAndId(Long shopId, Long id) {
        ReturnObject returnObject = shareActivityDao.getShareActivityByShopIdAndId(shopId, id);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        ShareActivityBo shareActivityBo = (ShareActivityBo) returnObject.getData();
        RetShareActivitySpecificInfoVo retShareActivitySpecificInfoVo = (RetShareActivitySpecificInfoVo) Common.cloneVo(shareActivityBo, RetShareActivitySpecificInfoVo.class);
        retShareActivitySpecificInfoVo.setShop(new ShopVo(shareActivityBo.getShopId(), shareActivityBo.getShopName()));
        retShareActivitySpecificInfoVo.setCreatedBy(new SimpleUserRetVo(shareActivityBo.getCreatorId(), shareActivityBo.getCreatorName()));
        retShareActivitySpecificInfoVo.setCreatedBy(new SimpleUserRetVo(shareActivityBo.getModifierId(), shareActivityBo.getModifierName()));
        return new ReturnObject(retShareActivitySpecificInfoVo);
    }
}
