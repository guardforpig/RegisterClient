package cn.edu.xmu.oomall.coupon.service;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.dao.CouponActivityDao;
import cn.edu.xmu.oomall.coupon.microservice.GoodsService;
import cn.edu.xmu.oomall.coupon.microservice.vo.OnsaleVo;
import cn.edu.xmu.oomall.coupon.model.bo.CouponActivity;
import cn.edu.xmu.oomall.coupon.model.bo.CouponOnsale;
import cn.edu.xmu.oomall.coupon.model.bo.Shop;
import cn.edu.xmu.oomall.coupon.model.po.CouponActivityPoExample;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityRetVo;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityVo;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityVoInfo;
import cn.edu.xmu.oomall.coupon.microservice.ShopFeignService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author RenJieZheng 22920192204334
 */
/**
 * @author qingguo Hu 22920192204208
 */
@Service
public class CouponActivityService {
    @Autowired
    CouponActivityDao couponActivityDao;
    
    @Autowired
    private GoodsService goodsService;

    @Resource
    ShopFeignService shopFeignService;



    /**
     * 查看优惠活动模块的所有活动
     * @return List<StateRetVo>list
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<List<Map<String, Object>>> showAllState(){
        return couponActivityDao.showAllState();
    }


    /**
     * 查看所有的上线优惠活动列表
     * @param shopId 店铺id
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param page 页
     * @param pageSize 页大小
     * @return 优惠活动列表
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<PageInfo<VoObject>> showOwnCouponActivities(Long shopId, LocalDateTime beginTime,LocalDateTime endTime, Integer page, Integer pageSize){
        //添加查询条件
        CouponActivityPoExample example = new CouponActivityPoExample();
        CouponActivityPoExample.Criteria criteria = example.createCriteria();
        if(shopId!=null){
            criteria.andShopIdEqualTo(shopId);
        }
        if(beginTime!=null){
            criteria.andBeginTimeGreaterThan(beginTime);
        }
        if(endTime!=null){
            criteria.andBeginTimeLessThan(endTime);
        }
        //上线状态
        criteria.andStateEqualTo(CouponActivity.State.ONLINE.getCode().byteValue());
        return couponActivityDao.showCouponActivitiesByExample(example,page,pageSize);
    }

    /**
     * 查看店铺的所有状态优惠活动列表
     * @param shopId 店铺id
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param state 状态
     * @param page 页
     * @param pageSize 页大小
     * @return 优惠活动列表 List<CouponActivityRetVo>
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<PageInfo<VoObject>> showOwnCouponActivities1(Long shopId,LocalDateTime beginTime,LocalDateTime endTime,Byte state,Integer page,Integer pageSize){
        //查询条件
        CouponActivityPoExample example = new CouponActivityPoExample();
        CouponActivityPoExample.Criteria criteria = example.createCriteria();
        if(shopId!=null){
            criteria.andShopIdEqualTo(shopId);
        }
        if(beginTime!=null){
            criteria.andBeginTimeGreaterThan(beginTime);
        }
        if(endTime!=null){
            criteria.andBeginTimeLessThan(endTime);
        }
        if(state!=null){
            criteria.andStateEqualTo(state);
        }
        criteria.andStateEqualTo(state);
        return couponActivityDao.showCouponActivitiesByExample(example,page,pageSize);
    }

    /**
     * 管理员新建己方优惠活动
     * @param shopId 店铺id
     * @param couponActivityVo 优惠券信息
     * @return 插入结果
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject addCouponActivity(Long userId, String userName, Long shopId, CouponActivityVo couponActivityVo){
        ReturnObject<Shop>returnObject = shopFeignService.getShopById(shopId);
        if(!returnObject.getCode().equals(ReturnNo.OK)){
            return returnObject;
        }
        Shop shop = returnObject.getData();
        CouponActivity couponActivity = (CouponActivity) Common.cloneVo(couponActivityVo,CouponActivity.class);
        couponActivity.setShopId(shopId);
        couponActivity.setShopName(shop.getName());
        // 新建优惠时默认是草稿
        couponActivity.setState(CouponActivity.State.DRAFT.getCode().byteValue());
        Common.setPoCreatedFields(couponActivity,userId,userName);
        return couponActivityDao.addCouponActivity(couponActivity);
    }

    /**
     * 查看店铺所有状态的优惠活动列表
     * @param userId 用户id
     * @param userName 用户名
     * @param shopId 店铺Id
     * @param state 状态
     * @param page 页
     * @param pageSize 页大小
     * @return 优惠活动列表
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<PageInfo<VoObject>> showOwnInvalidCouponActivities(Long userId,String userName,Long shopId,Byte state,Integer page,Integer pageSize){
        CouponActivityPoExample example = new CouponActivityPoExample();
        CouponActivityPoExample.Criteria criteria = example.createCriteria();
        if(shopId !=null){
            criteria.andShopIdEqualTo(shopId);
        }
        if(state != null){
            criteria.andStateEqualTo(state);
        }
        criteria.andCreatorIdEqualTo(userId);
        criteria.andCreatorNameEqualTo(userName);
        return couponActivityDao.showCouponActivitiesByExample(example,page,pageSize);
    }

    /**
     * 查看优惠活动详情
     * @param id 活动id
     * @param shopId 店铺id
     * @return 优惠活动信息
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<CouponActivityVoInfo> showOwnCouponActivityInfo(Long userId,String userName,Long id,Long shopId){
        CouponActivity couponActivity = new CouponActivity();
        couponActivity.setId(id);
        couponActivity.setShopId(shopId);
        Common.setPoCreatedFields(couponActivity,userId,userName);
        return couponActivityDao.showCouponActivityPoStraight(id,couponActivity);
    }

    /**
     * 上传文件url
     * @param id 活动id
     * @param shopId 店铺id
     * @return 上传结果
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject addCouponActivityImageUrl(Long userId,String userName,Long id, Long shopId, MultipartFile multipartFile) {
        CouponActivity couponActivity = new CouponActivity();
        couponActivity.setId(id);
        couponActivity.setShopId(shopId);
        couponActivity.setImageUrl(multipartFile.getResource().getFilename());
        Common.setPoModifiedFields(couponActivity,userId,userName);
        return couponActivityDao.updateImageUrl(id,couponActivity,multipartFile);
    }


/**
 * @author qingguo Hu 22920192204208
 */
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
        ReturnObject<CouponActivity> retCouponActivity = couponActivityDao.getCouponActivityById(couponActivityId);
        if (!retCouponActivity.getCode().equals(ReturnNo.OK)) {
            return retCouponActivity;
        }

        // 根据活动找出活动对应的CouponOnsale列表
        ReturnObject<PageInfo<Object>> retPageInfo =
                couponActivityDao.listCouponOnsaleByActivityId(couponActivityId, pageNumber, pageSize);
        if (!retPageInfo.getCode().equals(ReturnNo.OK)) {
            return retPageInfo;
        }

        // 根据CouponOnsale列表找出每一个CouponOnsale对应的product
        List<Object> couponOnsaleList = retPageInfo.getData().getList();
        List<Object> productVoList = new ArrayList<>();
        for (Object couponOnsale : couponOnsaleList) {
            ReturnObject<OnsaleVo> retOnsaleVo =
                    goodsService.getOnsaleById(((CouponOnsale)couponOnsale).getOnsaleId());
            if (retOnsaleVo.getCode().equals(ReturnNo.OK)) {
                productVoList.add(retOnsaleVo.getData().getProduct());
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
                couponActivityDao.listCouponOnsaleByIdList(onsaleIdList, 1, 0);
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
                couponActivityDao.listCouponActivityByIdList(couponActivityIdList, pageNumber, pageSize);
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
        ReturnObject<CouponActivity> retFormerCouponActivity = couponActivityDao.getCouponActivityById(couponActivityId);
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

            return couponActivityDao.updateCouponActivity(newCouponActivity);
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
                    return couponActivityDao.updateCouponActivity(formerCouponActivity);
                }
                case OFFLINE: {
                    // 修改为Offline，需判断状态是不是在上线态
                    if (!formerCouponActivity.getState().equals(CouponActivity.State.ONLINE.getCode())) {
                        return new ReturnObject<>(ReturnNo.STATENOTALLOW);
                    }
                    formerCouponActivity.setState(CouponActivity.State.OFFLINE.getCode());
                    Common.setPoModifiedFields(formerCouponActivity, userId, userName);
                    ReturnObject returnObject = couponActivityDao.updateCouponActivity(formerCouponActivity);
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
        ReturnObject<CouponActivity> retCouponActivity = couponActivityDao.getCouponActivityById(couponActivityId);
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
        if (!onsaleVo.getShop().getId().equals(shopId)) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "该Onsale不属于该商店");
        }

        // 判断数据库中是否已经有CouponOnsale表示该onsale已经参与了该活动
        ReturnObject<PageInfo<Object>> retPageInfo =
                couponActivityDao.listCouponOnsaleByOnsaleIdAndActivityId(onsaleId, couponActivityId, 1, 10);
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

        return couponActivityDao.insertCouponOnsale(newCouponOnsale);
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
        ReturnObject<CouponActivity> retFormerCouponActivity = couponActivityDao.getCouponActivityById(couponActivityId);
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
                couponActivityDao.listCouponOnsaleByActivityId(couponActivityId, 1, 0);
        if (!retCouponOnsalePageInfo.getCode().equals(ReturnNo.OK)) {
            return retCouponOnsalePageInfo;
        }

        //将优惠活动关联的商品一并删除
        List<CouponOnsale> couponOnsaleList = retCouponOnsalePageInfo.getData().getList();
        for (CouponOnsale couponOnsale : couponOnsaleList) {
            couponActivityDao.deleteCouponOnsaleById(couponOnsale.getId());
        }

        // 将优惠活动删除
        return couponActivityDao.deleteCouponActivityById(couponActivityId);
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
        ReturnObject<CouponOnsale> retCouponOnsale = couponActivityDao.getCouponOnsaleById(couponOnsaleId);
        if (!retCouponOnsale.getCode().equals(ReturnNo.OK)) {
            return retCouponOnsale;
        }

        // 找出CouponOnsale参与的CouponActivity
        ReturnObject<CouponActivity> retCouponActivity =
                couponActivityDao.getCouponActivityById(retCouponOnsale.getData().getActivityId());
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
        return couponActivityDao.deleteCouponOnsaleById(couponOnsaleId);
    }
}
