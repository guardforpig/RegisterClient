package cn.edu.xmu.oomall.coupon.service;


import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.dao.CouponDao;
import cn.edu.xmu.oomall.coupon.microservice.GoodsService;
import cn.edu.xmu.oomall.coupon.microservice.vo.OnsaleVo;
import cn.edu.xmu.oomall.coupon.model.bo.CouponActivity;
import cn.edu.xmu.oomall.coupon.model.bo.CouponOnsale;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityRetVo;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author qingguo Hu 22920192204208
 */
@Service
public class CouponService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private GoodsService goodsService;

    /**
     *
     * @param couponActivityId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject listProductsByCouponActivityId(Long couponActivityId, Integer pageNumber, Integer pageSize) {
        // 判断活动存在与否
        ReturnObject<CouponActivity> retCouponActivity = couponDao.getCouponActivityById(couponActivityId);
        if (!retCouponActivity.getCode().equals(ReturnNo.OK)) {
            return retCouponActivity;
        }

        // 根据活动找出活动对应的CouponOnsale列表
        ReturnObject<PageInfo<Object>> retPageInfo =
                couponDao.listCouponOnsaleByActivityId(couponActivityId, pageNumber, pageSize);
        if (!retPageInfo.getCode().equals(ReturnNo.OK)) {
            return retPageInfo;
        }

        // 根据CouponOnsale列表找出每一个CouponOnsale对应的product
        List<Object> couponOnsaleList = retPageInfo.getData().getList();
        List<Object> productVoList = new ArrayList<>();
        for (Object couponOnsale : couponOnsaleList) {
            ReturnObject<Object> retProductVo =
                    goodsService.getProductByOnsaleId(((CouponOnsale)couponOnsale).getOnsaleId());
            if (retProductVo.getCode().equals(ReturnNo.OK)) {
                productVoList.add(retProductVo.getData());
            }
        }

        retPageInfo.getData().setList(productVoList);
        return retPageInfo;
    }


    /**
     *
     * @param productId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject listCouponActivitiesByProductId(Long productId, Integer pageNumber, Integer pageSize) {
        // 找到Product对应的所有OnsaleVo
        ReturnObject<List<Object>> retOnsaleVoList = goodsService.listOnsalesByProductId(productId);
        if (!retOnsaleVoList.getCode().equals(ReturnNo.OK)) {
            return retOnsaleVoList;
        }

        // 获取所有Onsale的id，存在列表里
        List<Object> onsaleVoList = retOnsaleVoList.getData();
        List<Long> onsaleIdList = new ArrayList<>();
        for (Object onsaleVo : onsaleVoList) {
            onsaleIdList.add(((OnsaleVo) onsaleVo).getId());
        }

        // 根据OnsaleId的列表，找出所有的CouponOnsale

        ReturnObject<PageInfo<CouponOnsale>> retCouponOnsaleListPage =
                couponDao.listCouponOnsaleByIdList(onsaleIdList, 1, 0);
        if (!retCouponOnsaleListPage.getCode().equals(ReturnNo.OK)) {
            return retCouponOnsaleListPage;
        }

        // 根据所有CouponOnsale，找出对应所有的CouponActivityId，存在列表里
        List<CouponOnsale> couponOnsaleList = retCouponOnsaleListPage.getData().getList();
        List<Long> couponActivityIdList = new ArrayList<>();
        for (CouponOnsale couponOnsale : couponOnsaleList) {
            couponActivityIdList.add(couponOnsale.getActivityId());
        }

        // 根据CouponActivityId的列表，找出所有上线的CouponActivity，并分页
        ReturnObject<PageInfo<Object>> retPageInfo =
                couponDao.listCouponActivityByIdList(couponActivityIdList, pageNumber, pageSize);
        if (!retPageInfo.getCode().equals(ReturnNo.OK)) {
            return retPageInfo;
        }

        // 将所有的CouponActivity转成Vo，返回
        List<Object> couponActivityList = retPageInfo.getData().getList();
        List<Object> couponActivityVoList = new ArrayList<>();
        for (Object couponActivity : couponActivityList) {
            couponActivityVoList.add(Common.cloneVo(couponActivity, CouponActivityRetVo.class));
        }

        retPageInfo.getData().setList(couponActivityVoList);
        return retPageInfo;
    }

    /**
     *
     * @param userId
     * @param userName
     * @param shopId
     * @param couponActivityId
     * @param couponActivityVo
     * @param newState
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateCouponActivity(Long userId, String userName, Long shopId, Long couponActivityId, CouponActivityVo couponActivityVo, CouponActivity.State newState) {
        // 判断活动存在与否
        ReturnObject<CouponActivity> retFormerCouponActivity = couponDao.getCouponActivityById(couponActivityId);
        if (!retFormerCouponActivity.getCode().equals(ReturnNo.OK)) {
            return retFormerCouponActivity;
        }

        // 判断创建活动的商店Id是否与传入的shopId对应
        CouponActivity formerCouponActivity = retFormerCouponActivity.getData();
        if (!formerCouponActivity.getShopId().equals(shopId)) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "该优惠活动不属于该商店");
        }

        // 判断是不是修改活动状态
        if (newState == null) {
            // 不修改活动状态，则通过Vo修改活动
            // 判断活动是不是在草稿态
            if (!formerCouponActivity.getState().equals(CouponActivity.State.DRAFT.getCode())) {
                return new ReturnObject<>(ReturnNo.STATENOTALLOW);
            }

            CouponActivity newCouponActivity = (CouponActivity) Common.cloneVo(couponActivityVo, CouponActivity.class);
            newCouponActivity.setId(couponActivityId);
            Common.setPoModifiedFields(newCouponActivity, userId, userName);

            return couponDao.updateCouponActivity(newCouponActivity);
        } else
            // 修改的是状态
        {
            switch (newState) {
                case ONLINE: {
                    // 修改为Online，需判断状态是不是在下线态
                    if (!formerCouponActivity.getState().equals(CouponActivity.State.OFFLINE.getCode())) {
                        return new ReturnObject<>(ReturnNo.STATENOTALLOW);
                    }
                    formerCouponActivity.setState(CouponActivity.State.ONLINE.getCode());
                    Common.setPoModifiedFields(formerCouponActivity, userId, userName);

                    return couponDao.updateCouponActivity(formerCouponActivity);
                }
                case OFFLINE: {
                    // 修改为Offline，需判断状态是不是在上线态
                    if (!formerCouponActivity.getState().equals(CouponActivity.State.ONLINE.getCode())) {
                        return new ReturnObject<>(ReturnNo.STATENOTALLOW);
                    }
                    formerCouponActivity.setState(CouponActivity.State.OFFLINE.getCode());
                    Common.setPoModifiedFields(formerCouponActivity, userId, userName);

                    ReturnObject returnObject = couponDao.updateCouponActivity(formerCouponActivity);
                    return returnObject;
                    // TODO: 将已发行未用的优惠卷一并下线
                    // 数据库好像没有优惠券，暂时先放着
                }
                default:
                    return new ReturnObject<>(ReturnNo.STATENOTALLOW);
            }
        }
    }

    /**
     *
     * @param userId
     * @param userName
     * @param shopId
     * @param couponActivityId
     * @param onsaleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject insertCouponOnsale(Long userId, String userName, Long shopId, Long couponActivityId, Long onsaleId) {
        // 判断CouponActivity是否存在
        ReturnObject<CouponActivity> retCouponActivity = couponDao.getCouponActivityById(couponActivityId);
        if (!retCouponActivity.getCode().equals(ReturnNo.OK)) {
            return retCouponActivity;
        }

        // 判断Onsale是否存在
        ReturnObject<OnsaleVo> retOnsaleVo = goodsService.getOnsaleById(onsaleId);
        if (!retOnsaleVo.getCode().equals(ReturnNo.OK)) {
            return retOnsaleVo;
        }

        // 判断couponActivity和onSale是否都属于该shop
        CouponActivity couponActivity = retCouponActivity.getData();
        OnsaleVo onsaleVo = retOnsaleVo.getData();
        if (!couponActivity.getShopId().equals(shopId)) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "该优惠活动不属于该商店");
        }
        if (!onsaleVo.getShopId().equals(shopId)) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "该Onsale不属于该商店");
        }

        // 判断数据库中是否已经有CouponOnsale表示该onsale已经~参与了该活动
        ReturnObject<PageInfo<Object>> retPageInfo =
                couponDao.listCouponOnsaleByOnsaleIdAndActivityId(onsaleId, couponActivityId, 1, 10);
        if (!retPageInfo.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW, "该onsale已经参与了该活动");
        }

        // 判断该活动是不是下线态，下线态出错
        if (couponActivity.getState().equals(CouponActivity.State.OFFLINE.getCode())) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }

        // 通过CouponActivityId和OnsaleId，创建一个CouponOnsale
        CouponOnsale newCouponOnsale = new CouponOnsale();
        newCouponOnsale.setActivityId(couponActivityId);
        newCouponOnsale.setOnsaleId(onsaleId);
        Common.setPoCreatedFields(newCouponOnsale, userId, userName);
        Common.setPoModifiedFields(newCouponOnsale, userId, userName);

        return couponDao.insertCouponOnsale(newCouponOnsale);
    }

    /**
     *
     * @param userId
     * @param userName
     * @param shopId
     * @param couponActivityId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteCouponActivity(Long userId, String userName, Long shopId, Long couponActivityId) {
        // 判断CouponActivity是否存在
        ReturnObject<CouponActivity> retFormerCouponActivity = couponDao.getCouponActivityById(couponActivityId);
        if (!retFormerCouponActivity.getCode().equals(ReturnNo.OK)) {
            return retFormerCouponActivity;
        }

        // 判断创建活动的商店Id是否与传入的shopId对应
        CouponActivity formerCouponActivity = retFormerCouponActivity.getData();
        if (!formerCouponActivity.getShopId().equals(shopId)) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "该优惠活动不属于该商店");
        }
        if (!formerCouponActivity.getState().equals(CouponActivity.State.DRAFT.getCode())) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }

        // 根据活动找出活动对应的CouponOnsale列表
        ReturnObject<PageInfo<CouponOnsale>> retCouponOnsalePageInfo =
                couponDao.listCouponOnsaleByActivityId(couponActivityId, 1, 0);
        if (!retCouponOnsalePageInfo.getCode().equals(ReturnNo.OK)) {
            return retCouponOnsalePageInfo;
        }

        //将优惠活动关联的商品一并删除
        List<CouponOnsale> couponOnsaleList = retCouponOnsalePageInfo.getData().getList();
        for (CouponOnsale couponOnsale : couponOnsaleList) {
            couponDao.deleteCouponOnsaleById(couponOnsale.getId());
        }

        // 将优惠活动删除
        return couponDao.deleteCouponActivityById(couponActivityId);
    }

    /**
     *
     * @param userId
     * @param userName
     * @param shopId
     * @param couponOnsaleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteCouponOnsale(Long userId, String userName, Long shopId, Long couponOnsaleId) {
        // 判断CouponOnsale是否存在
        ReturnObject<CouponOnsale> retCouponOnsale = couponDao.getCouponOnsaleById(couponOnsaleId);
        if (!retCouponOnsale.getCode().equals(ReturnNo.OK)) {
            return retCouponOnsale;
        }

        // 找出CouponOnsale参与的CouponActivity
        ReturnObject<CouponActivity> retCouponActivity =
                couponDao.getCouponActivityById(retCouponOnsale.getData().getActivityId());
        if (!retCouponActivity.getCode().equals(ReturnNo.OK)) {
            return retCouponActivity;
        }

        // 判断活动的商店Id是否与传入的shopId对应
        CouponActivity couponActivity = retCouponActivity.getData();
        if (!couponActivity.getShopId().equals(shopId)) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "该CouponOnSale参与的优惠活动不属于该商店");
        }
        // 判断是不是在下线态，下线态出错
        if (couponActivity.getState().equals(CouponActivity.State.OFFLINE.getCode())) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
        return couponDao.deleteCouponOnsaleById(couponOnsaleId);
    }
}
