package cn.edu.xmu.oomall.coupon.service;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.dao.CouponDao;
import cn.edu.xmu.oomall.coupon.microservice.GoodsService;
import cn.edu.xmu.oomall.coupon.microservice.vo.OnsaleVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.ProductVo;
import cn.edu.xmu.oomall.coupon.model.bo.CouponActivity;
import cn.edu.xmu.oomall.coupon.model.bo.CouponOnsale;
import cn.edu.xmu.oomall.coupon.model.po.CouponActivityPo;
import cn.edu.xmu.oomall.coupon.model.po.CouponActivityPoExample;
import cn.edu.xmu.oomall.coupon.model.po.CouponOnsalePo;
import cn.edu.xmu.oomall.coupon.model.po.CouponOnsalePoExample;
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
        ReturnObject<CouponActivity> retCouponActivity = couponDao.getBoByPrimaryKey(couponActivityId, CouponActivity.class);
        if (!retCouponActivity.getCode().equals(ReturnNo.OK)) {
            return retCouponActivity;
        }

        // 根据活动找出活动对应的CouponOnsale列表
        CouponOnsalePoExample example = new CouponOnsalePoExample();
        example.createCriteria().andActivityIdEqualTo(couponActivityId);
        ReturnObject<PageInfo<CouponOnsale>> retCouponOnsalePageInfo =
                couponDao.listBoByExample(example, CouponOnsale.class, pageNumber, pageSize);
        if (!retCouponOnsalePageInfo.getCode().equals(ReturnNo.OK)) {
            return retCouponOnsalePageInfo;
        }

        // 根据CouponOnsale列表找出每一个CouponOnsale对应的product
        List<CouponOnsale> couponOnsaleList = retCouponOnsalePageInfo.getData().getList();
        List<VoObject> productVoList = new ArrayList<>();
        for (CouponOnsale couponOnsale : couponOnsaleList) {
            ReturnObject<VoObject> retProductVo =
                    goodsService.getProductByOnsaleId(couponOnsale.getOnsaleId());
            if (retProductVo.getCode().equals(ReturnNo.OK)) {
                productVoList.add((ProductVo) retProductVo.getData());
            }
        }

        PageInfo<VoObject> productVoPageInfo = new PageInfo<>(productVoList);
        return new ReturnObject<>(productVoPageInfo);
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
        ReturnObject<List<VoObject>> retOnsaleVoList = goodsService.listOnsalesByProductId(productId);
        if (!retOnsaleVoList.getCode().equals(ReturnNo.OK)) {
            return retOnsaleVoList;
        }

        // 获取所有Onsale的id，存在列表里
        List<VoObject> onsaleVoList = retOnsaleVoList.getData();
        List<Long> onsaleIdList = new ArrayList<>();
        for (VoObject onsaleVo : onsaleVoList) {
            onsaleIdList.add(((OnsaleVo) onsaleVo).getId());
        }

        // 根据OnsaleId的列表，找出所有的CouponOnsale
        CouponOnsalePoExample example1 = new CouponOnsalePoExample();
        example1.createCriteria().andOnsaleIdIn(onsaleIdList);
        ReturnObject<PageInfo<CouponOnsale>> retCouponOnsaleListPage =
                couponDao.listBoByExample(example1, CouponOnsale.class, 1, 0);
        if (!retCouponOnsaleListPage.getCode().equals(ReturnNo.OK)) {
            return retCouponOnsaleListPage;
        }

        // 根据所有CouponOnsale，找出对应所有的CouponActivityId，存在列表里
        List<CouponOnsale> couponOnsaleList = retCouponOnsaleListPage.getData().getList();
        List<Long> couponActivityIdList = new ArrayList<>();
        for (CouponOnsale couponOnsale : couponOnsaleList) {
            couponActivityIdList.add(couponOnsale.getActivityId());
        }

        // 根据CouponActivityId的列表，找出所有的CouponActivity，并分页
        CouponActivityPoExample example2 = new CouponActivityPoExample();
        example2.createCriteria().andIdIn(couponActivityIdList);
        ReturnObject<PageInfo<CouponActivity>> retCouponActivityPageInfo =
                couponDao.listBoByExample(example2, CouponActivity.class, pageNumber, pageSize);
        if (!retCouponActivityPageInfo.getCode().equals(ReturnNo.OK)) {
            return retCouponActivityPageInfo;
        }

        // 将所有的CouponActivity转成Vo，返回
        List<CouponActivity> couponActivityList = retCouponActivityPageInfo.getData().getList();
        List<VoObject> couponActivityVoList = new ArrayList<>();
        for (CouponActivity couponActivity : couponActivityList) {
            couponActivityVoList.add((VoObject) Common.cloneVo(couponActivity, CouponActivityRetVo.class));
        }

        // TODO: pageInfo所处位置的相关问题
        PageInfo<VoObject> couponActivityVoPageInfo = new PageInfo<>(couponActivityVoList);
        return new ReturnObject<>(couponActivityVoPageInfo);
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
        ReturnObject<CouponActivity> retFormerCouponActivity = couponDao.getBoByPrimaryKey(couponActivityId, CouponActivity.class);
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
                return new ReturnObject<>(ReturnNo.STATENOTALLOW, ReturnNo.STATENOTALLOW.getMessage());
            }

            CouponActivity newCouponActivity = (CouponActivity) Common.cloneVo(couponActivityVo, CouponActivity.class);
            newCouponActivity.setId(couponActivityId);
            Common.setPoModifiedFields(newCouponActivity, userId, userName);

            return couponDao.updateBo(newCouponActivity, CouponActivityPo.class);
        } else
            // 修改的是状态
        {
            switch (newState) {
                case ONLINE: {
                    // 修改为Online，需判断状态是不是在下线态
                    if (!formerCouponActivity.getState().equals(CouponActivity.State.OFFLINE.getCode())) {
                        return new ReturnObject<>(ReturnNo.STATENOTALLOW, ReturnNo.STATENOTALLOW.getMessage());
                    }

                    formerCouponActivity.setState(CouponActivity.State.ONLINE.getCode());
                    Common.setPoModifiedFields(formerCouponActivity, userId, userName);

                    return couponDao.updateBo(formerCouponActivity, CouponActivityPo.class);
                }
                case OFFLINE: {
                    // 修改为Offline，需判断状态是不是在上线态
                    if (!formerCouponActivity.getState().equals(CouponActivity.State.ONLINE.getCode())) {
                        return new ReturnObject<>(ReturnNo.STATENOTALLOW, ReturnNo.STATENOTALLOW.getMessage());
                    }

                    formerCouponActivity.setState(CouponActivity.State.OFFLINE.getCode());
                    Common.setPoModifiedFields(formerCouponActivity, userId, userName);

                    ReturnObject returnObject = couponDao.updateBo(formerCouponActivity, CouponActivityPo.class);
                 // if (!returnObject.getCode().equals(ReturnNo.OK))
                    return returnObject;

                    // TODO: 将已发行未用的优惠卷一并下线
                    // 数据库好像没有优惠券，暂时先放着
                }
                default:
                    return new ReturnObject<>(ReturnNo.STATENOTALLOW, ReturnNo.STATENOTALLOW.getMessage());

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
        ReturnObject<CouponActivity> retCouponActivity = couponDao.getBoByPrimaryKey(couponActivityId, CouponActivity.class);
        if (!retCouponActivity.getCode().equals(ReturnNo.OK)) {
            return retCouponActivity;
        }

        // TODO：是否需要判断onsale是否存在
        // TODO: 是否需要判断数据库中已经有CouponOnsale表示该onsale已经参与了该活动

        // 判断创建活动的商店Id是否与传入的shopId对应
        CouponActivity couponActivity = retCouponActivity.getData();
        if (!couponActivity.getShopId().equals(shopId)) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "该优惠活动不属于该商店");
        }
        if (couponActivity.getState().equals(CouponActivity.State.OFFLINE.getCode())) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW, ReturnNo.STATENOTALLOW.getMessage());
        }

        // 通过CouponActivityId和OnsaleId，创建一个CouponOnsale
        CouponOnsale newCouponOnsale = new CouponOnsale();
        newCouponOnsale.setActivityId(couponActivityId);
        newCouponOnsale.setOnsaleId(onsaleId);
        Common.setPoCreatedFields(newCouponOnsale, userId, userName);
        Common.setPoModifiedFields(newCouponOnsale, userId, userName);

        return couponDao.insertBo(newCouponOnsale, CouponOnsalePo.class);
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
        ReturnObject<CouponActivity> retFormerCouponActivity = couponDao.getBoByPrimaryKey(couponActivityId, CouponActivity.class);
        if (!retFormerCouponActivity.getCode().equals(ReturnNo.OK)) {
            return retFormerCouponActivity;
        }

        // 判断创建活动的商店Id是否与传入的shopId对应
        CouponActivity formerCouponActivity = retFormerCouponActivity.getData();
        if (!formerCouponActivity.getShopId().equals(shopId)) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "该优惠活动不属于该商店");
        }
        if (!formerCouponActivity.getState().equals(CouponActivity.State.DRAFT.getCode())) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW, ReturnNo.STATENOTALLOW.getMessage());
        }

        // 根据活动找出活动对应的CouponOnsale列表
        CouponOnsalePoExample example = new CouponOnsalePoExample();
        example.createCriteria().andActivityIdEqualTo(couponActivityId);
        ReturnObject<PageInfo<CouponOnsale>> retCouponOnsalePageInfo =
                couponDao.listBoByExample(example, CouponOnsale.class, 1, 0);
        if (!retCouponOnsalePageInfo.getCode().equals(ReturnNo.OK)) {
            return retCouponOnsalePageInfo;
        }

        //将优惠活动关联的商品一并删除
        List<CouponOnsale> couponOnsaleList = retCouponOnsalePageInfo.getData().getList();
        for (CouponOnsale couponOnsale : couponOnsaleList) {
            couponDao.deleteBoByPrimaryKey(couponOnsale.getId(), CouponOnsale.class);
        }

        // 将优惠活动删除
        return couponDao.deleteBoByPrimaryKey(couponActivityId, CouponActivity.class);
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
        ReturnObject<CouponOnsale> retCouponOnsale = couponDao.getBoByPrimaryKey(couponOnsaleId, CouponOnsale.class);
        if (!retCouponOnsale.getCode().equals(ReturnNo.OK)) {
            return retCouponOnsale;
        }

        // 找出CouponOnsale参与的CouponActivity
        ReturnObject<CouponActivity> retCouponActivity =
                couponDao.getBoByPrimaryKey(retCouponOnsale.getData().getActivityId(), CouponActivity.class);
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
            return new ReturnObject<>(ReturnNo.STATENOTALLOW, ReturnNo.STATENOTALLOW.getMessage());
        }

        return couponDao.deleteBoByPrimaryKey(couponOnsaleId, CouponOnsale.class);
    }
}
