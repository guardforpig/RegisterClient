package cn.edu.xmu.oomall.coupon.service;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.coupon.dao.CouponActivityDao;
import cn.edu.xmu.oomall.coupon.microservice.GoodsService;
import cn.edu.xmu.oomall.coupon.microservice.vo.OnsaleVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.ProductVo;
import cn.edu.xmu.oomall.coupon.model.bo.CouponActivity;
import cn.edu.xmu.oomall.coupon.model.bo.CouponOnsale;
import cn.edu.xmu.oomall.coupon.model.bo.Shop;
import cn.edu.xmu.oomall.coupon.model.po.CouponActivityPoExample;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityRetVo;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityVo;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityVoInfo;
import cn.edu.xmu.oomall.coupon.microservice.ShopFeignService;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

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

    @Autowired
    private RedisUtil redisUtils;

    @Value("${oomall.coupon.list.expiretime}")
    private long listTimeout;

    @Value("${oomall.coupon.list.defaultsize}")
    private Integer listDefaultSize;

    // 活动查productVoList的key，key是activityId
    public final static String PRODUCTVOLISTKEY = "productvolist_%d";

    // 商品查couponActivityList的key，key是productId
    public final static String COUPONACTIVITYLISTKEY = "couponactivitylist_%d";


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
        InternalReturnObject<Shop> returnObject;
        try{
            returnObject = shopFeignService.getShopById(shopId);
        }catch(Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        Shop shop = returnObject.getData();
        CouponActivity couponActivity = (CouponActivity) cloneVo(couponActivityVo,CouponActivity.class);
        couponActivity.setShopId(shopId);
        couponActivity.setShopName(shop.getName());
        // 新建优惠时默认是草稿
        couponActivity.setState(CouponActivity.State.DRAFT.getCode().byteValue());
        setPoCreatedFields(couponActivity,userId,userName);
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
        ReturnObject returnObject = couponActivityDao.showCouponActivityPoStraight(id);
        if(returnObject.getCode()!= ReturnNo.OK){
            return returnObject;
        }
        CouponActivity couponActivity = (CouponActivity) returnObject.getData();
        if(!couponActivity.getShopId().equals(shopId)){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        //下线状态不给返回
        if(couponActivity.getState()==CouponActivity.State.OFFLINE.getCode().byteValue()){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if(!couponActivity.getCreatorId().equals(userId)){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        return new ReturnObject<>(new CouponActivityVoInfo(couponActivity));
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
       setPoModifiedFields(couponActivity,userId,userName);
        return couponActivityDao.updateImageUrl(id,couponActivity,multipartFile);
    }


/**
 * @author qingguo Hu 22920192204208
 */
    @Transactional(readOnly = true)
    public ReturnObject listProductsByCouponActivityId(Long couponActivityId, Integer pageNumber, Integer pageSize) {
        // 判断活动存在与否
        ReturnObject<CouponActivity> retCouponActivity = couponActivityDao.getCouponActivityById(couponActivityId);
        if (!retCouponActivity.getCode().equals(ReturnNo.OK)) {
            return retCouponActivity;
        }
        // 判断活动是否是上线态
        if (!retCouponActivity.getData().getState().equals(CouponActivity.State.ONLINE.getCode())) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }

        String key = String.format(PRODUCTVOLISTKEY, couponActivityId);
        List<ProductVo> productVoList = new ArrayList<>();
        if (redisUtils.hasKey(key) && (long) (pageNumber - 1) * pageSize <= redisUtils.sizeList(key)) {
            long beginIndex = (long) (pageNumber - 1) * pageSize;
            long endIndex = Math.min((long) pageNumber * pageSize, redisUtils.sizeList(key));
            List<Serializable> serializableList = redisUtils.rangeList(key, beginIndex, endIndex);
            for (Serializable serializable : serializableList) {
                productVoList.add((ProductVo) serializable);
            }
        } else {
            redisUtils.del(key);
            ReturnObject retCouponOnsalesPageInfo =
                    couponActivityDao.listCouponOnsalesByActivityId(couponActivityId, 1, ((pageNumber * pageSize) / listDefaultSize + 1) * listDefaultSize);
            if (!retCouponOnsalesPageInfo.getCode().equals(ReturnNo.OK)) {
                return retCouponOnsalesPageInfo;
            }
            Map<String, Object> retMap = (Map<String, Object>) retCouponOnsalesPageInfo.getData();
            List<CouponOnsale> couponOnsaleList = (List<CouponOnsale>) retMap.get("list");
            for (CouponOnsale couponOnsale : couponOnsaleList) {
                InternalReturnObject<OnsaleVo> retOnsaleVo =
                        goodsService.getOnsaleById(couponOnsale.getOnsaleId());
                if (retOnsaleVo.getErrno().equals(ReturnNo.OK.getCode())) {
                    if (retOnsaleVo.getData().getState().equals(OnsaleVo.State.ONLINE.getCode())) {
                        productVoList.add(retOnsaleVo.getData().getProduct());
                        redisUtils.rightPushList(key, retOnsaleVo.getData().getProduct());
                    }
                }
            }
            redisUtils.expire(key, listTimeout, TimeUnit.SECONDS);
            int beginIndex = Math.min((pageNumber - 1) * pageSize, productVoList.size());
            int endIndex = Math.min(pageNumber * pageSize, productVoList.size());
            productVoList = productVoList.subList(beginIndex, endIndex);
        }
        PageInfo<ProductVo> retPageInfo = new PageInfo<>(productVoList);
        retPageInfo.setTotal(redisUtils.sizeList(key));
        retPageInfo.setPages((int) ((redisUtils.sizeList(key) - 1) / pageSize + 1));
        retPageInfo.setPageSize(pageSize);
        retPageInfo.setPageNum(pageNumber);
        ReturnObject ret = new ReturnObject<>(retPageInfo);
        return Common.getPageRetVo(ret, ProductVo.class);
    }


    @Transactional(readOnly = true)
    public ReturnObject listCouponActivitiesByProductId(Long productId, Integer pageNumber, Integer pageSize) {
        String key = String.format(COUPONACTIVITYLISTKEY, productId);
        List<CouponActivity> onlineCouponActivityList = new ArrayList<>();
        if (redisUtils.hasKey(key) &&  (long) (pageNumber - 1) * pageSize <= redisUtils.sizeList(key)) {
            long beginIndex = (long) (pageNumber - 1) * pageSize;
            long endIndex = Math.min((long) pageNumber * pageSize, redisUtils.sizeList(key));
            List<Serializable> serializableList = redisUtils.rangeList(key, beginIndex, endIndex);
            for (Serializable serializable : serializableList) {
                onlineCouponActivityList.add((CouponActivity) serializable);
            }
        } else {
            redisUtils.del(key);
            InternalReturnObject<List<OnsaleVo>> retOnsaleVoPageInfo =
                    goodsService.listOnsale(productId, 1, ((pageNumber * pageSize) / listDefaultSize + 1) * listDefaultSize);
            if (!retOnsaleVoPageInfo.getErrno().equals(ReturnNo.OK.getCode())) {
                return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
            }
            Map<String, Object> retOnsaleMap = (Map<String, Object>) retOnsaleVoPageInfo.getData();
            List<OnsaleVo> onsaleVoList = (List<OnsaleVo>) retOnsaleMap.get("list");
            for (OnsaleVo onsaleVo : onsaleVoList) {
                if (onsaleVo.getState().equals(OnsaleVo.State.ONLINE.getCode())) {
                    ReturnObject<PageInfo<CouponActivity>> retCouponActivityListPage =
                            couponActivityDao.listCouponActivitiesByOnsaleId(onsaleVo.getId(), 1, ((pageNumber * pageSize) / listDefaultSize + 1) * listDefaultSize);
                    if (retCouponActivityListPage.getCode().equals(ReturnNo.OK)) {
                        Map<String, Object> retCouponActivityMap = (Map<String, Object>) retCouponActivityListPage.getData();
                        List<CouponActivity> couponActivityList = (List<CouponActivity>) retCouponActivityMap.get("list");
                        for (CouponActivity couponActivity : couponActivityList) {
                            if (couponActivity.getState().equals(CouponActivity.State.ONLINE.getCode())) {
                                onlineCouponActivityList.add(couponActivity);
                                redisUtils.rightPushList(key, couponActivity);
                            }
                        }
                    }
                }
            }
            redisUtils.expire(key, listTimeout, TimeUnit.SECONDS);
            int beginIndex = Math.min((pageNumber - 1) * pageSize, onlineCouponActivityList.size());
            int endIndex = Math.min(pageNumber * pageSize, onlineCouponActivityList.size());
            onlineCouponActivityList = onlineCouponActivityList.subList(beginIndex, endIndex);
        }
        PageInfo<CouponActivity> retPageInfo = new PageInfo<>(onlineCouponActivityList);
        retPageInfo.setTotal(redisUtils.sizeList(key));
        retPageInfo.setPages((int) ((redisUtils.sizeList(key) - 1) / pageSize + 1));
        retPageInfo.setPageSize(pageSize);
        retPageInfo.setPageNum(pageNumber);
        ReturnObject ret = new ReturnObject<>(retPageInfo);
        return Common.getPageRetVo(ret, CouponActivityRetVo.class);
    }

    @Transactional(readOnly = true)
    public ReturnObject listOnsalesByCouponActivityId(Long shopId, Long couponActivityId, Integer pageNumber, Integer pageSize) {
        // 判断活动存在与否
        ReturnObject<CouponActivity> retCouponActivity = couponActivityDao.getCouponActivityById(couponActivityId);
        if (!retCouponActivity.getCode().equals(ReturnNo.OK)) {
            return retCouponActivity;
        }
        // 判断创建活动的商店Id是否与传入的shopId对应
        CouponActivity couponActivity = retCouponActivity.getData();
        if (!couponActivity.getShopId().equals(shopId)) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        // 判断活动是否是上线态
        if (!retCouponActivity.getData().getState().equals(CouponActivity.State.ONLINE.getCode())) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
        // 活动查CouponOnsale
        ReturnObject retPageInfo = couponActivityDao.listCouponOnsalesByActivityId(couponActivityId, pageNumber, pageSize);
        if (!retPageInfo.getCode().equals(ReturnNo.OK)) {
            return retPageInfo;
        }
        // CouponOnsale查Onsale
        Map<String, Object> retMap = (Map<String, Object>) retPageInfo.getData();
        List<CouponOnsale> couponOnsaleList = (List<CouponOnsale>) retMap.get("list");
        List<OnsaleVo> onsaleVoList = new ArrayList<>();
        for (CouponOnsale couponOnsale : couponOnsaleList) {
            InternalReturnObject<OnsaleVo> retOnsaleVo =
                    goodsService.getOnsaleById(couponOnsale.getOnsaleId());
            if (retOnsaleVo.getErrno().equals(ReturnNo.OK.getCode())) {
                // 所有状态
                onsaleVoList.add(retOnsaleVo.getData());
            }
        }
        retMap.put("list", onsaleVoList);
        return new ReturnObject<>(retPageInfo);
    }


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
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        // 判断是不是修改活动状态
        if (newState == null) {
            // 不修改活动状态，则通过Vo修改活动
            // 判断活动是不是在草稿态
            if (!formerCouponActivity.getState().equals(CouponActivity.State.DRAFT.getCode())) {
                return new ReturnObject<>(ReturnNo.STATENOTALLOW);
            }
            CouponActivity newCouponActivity = (CouponActivity) cloneVo(couponActivityVo, CouponActivity.class);
            newCouponActivity.setId(couponActivityId);
            setPoModifiedFields(newCouponActivity, userId, userName);
            return couponActivityDao.updateCouponActivity(newCouponActivity);
        } else {
            // 修改的是状态
            switch (newState) {
                case ONLINE: {
                    // 修改为Online，需判断状态是不是在下线态
                    if (!formerCouponActivity.getState().equals(CouponActivity.State.OFFLINE.getCode())) {
                        return new ReturnObject<>(ReturnNo.STATENOTALLOW);
                    }
                    formerCouponActivity.setState(CouponActivity.State.ONLINE.getCode());
                    setPoModifiedFields(formerCouponActivity, userId, userName);
                    return couponActivityDao.updateCouponActivity(formerCouponActivity);
                }
                case OFFLINE: {
                    // 修改为Offline，需判断状态是不是在上线态
                    if (!formerCouponActivity.getState().equals(CouponActivity.State.ONLINE.getCode())) {
                        return new ReturnObject<>(ReturnNo.STATENOTALLOW);
                    }
                    formerCouponActivity.setState(CouponActivity.State.OFFLINE.getCode());
                    setPoModifiedFields(formerCouponActivity, userId, userName);
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


    @Transactional(rollbackFor = Exception.class)
    public ReturnObject insertCouponOnsale(Long userId, String userName, Long shopId, Long couponActivityId, Long onsaleId) {
        // 判断CouponActivity是否存在
        ReturnObject<CouponActivity> retCouponActivity = couponActivityDao.getCouponActivityById(couponActivityId);
        if (!retCouponActivity.getCode().equals(ReturnNo.OK)) {
            return retCouponActivity;
        }
        // 判断Onsale是否存在
        InternalReturnObject<OnsaleVo> retOnsaleVo = goodsService.getOnsaleById(onsaleId);
        if (!retOnsaleVo.getErrno().equals(ReturnNo.OK.getCode())) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
        // 判断couponActivity和onSale是否都属于该shop
        CouponActivity couponActivity = retCouponActivity.getData();
        OnsaleVo onsaleVo = retOnsaleVo.getData();
        if (!couponActivity.getShopId().equals(shopId)) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if (!onsaleVo.getShop().getId().equals(shopId)) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        // 判断数据库中是否已经有CouponOnsale表示该onsale已经参与了该活动
        ReturnObject<PageInfo<Object>> retPageInfo =
                couponActivityDao.listCouponOnsalesByOnsaleIdAndActivityId(onsaleId, couponActivityId, 1, 10);
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
        setPoCreatedFields(newCouponOnsale, userId, userName);
        setPoModifiedFields(newCouponOnsale, userId, userName);

        ReturnObject returnObject = couponActivityDao.insertCouponOnsale(newCouponOnsale);
        if (!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        // 插入couponOnsale，需要删除活动查商品这个API的redis中activityId, List<productVo>的缓存数据
        redisUtils.del(String.format(PRODUCTVOLISTKEY, couponActivityId));
        // 插入couponOnsale，需要删除商品查活动这个API的redis中productId, List<activity>的缓存需要删除
        redisUtils.del(String.format(COUPONACTIVITYLISTKEY, retOnsaleVo.getData().getProduct().getId()));

        return new ReturnObject<>(ReturnNo.OK);
    }


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
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if (!formerCouponActivity.getState().equals(CouponActivity.State.DRAFT.getCode())) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
        // 根据活动找出活动对应的CouponOnsale列表
        ReturnObject<PageInfo<CouponOnsale>> retCouponOnsalePageInfo =
                couponActivityDao.listCouponOnsalesByActivityId(couponActivityId, 1, 0);

        if (retCouponOnsalePageInfo.getCode().equals(ReturnNo.OK)) {
            //将优惠活动关联的商品一并删除
            Map<String, Object> retOnsaleMap = (Map<String, Object>) retCouponOnsalePageInfo.getData();
            List<CouponOnsale> couponOnsaleList = (List<CouponOnsale>) retOnsaleMap.get("list");
            for (CouponOnsale couponOnsale : couponOnsaleList) {
                deleteCouponOnsale(userId, userName, shopId, couponOnsale.getId());
            }
        }
        // 将优惠活动删除
        return couponActivityDao.deleteCouponActivityById(couponActivityId);
    }


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
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        // 判断是不是在下线态，下线态出错
        if (couponActivity.getState().equals(CouponActivity.State.OFFLINE.getCode())) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
        ReturnObject returnObject = couponActivityDao.deleteCouponOnsaleById(couponOnsaleId);
        if (!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        // 删除couponOnsale，需要删除活动查商品这个API的redis中activityId, List<productVo>的缓存数据
        redisUtils.del(String.format(PRODUCTVOLISTKEY, retCouponOnsale.getData().getActivityId()));
        // 删除couponOnsale，需要删除商品查活动这个API的redis中productId, List<activity>需要删除，所以需要找到onsale对应的productId
        InternalReturnObject<OnsaleVo> tempOnsaleVo = goodsService.getOnsaleById(retCouponOnsale.getData().getOnsaleId());
        redisUtils.del(String.format(COUPONACTIVITYLISTKEY, tempOnsaleVo.getData().getProduct().getId()));
        return new ReturnObject<>(ReturnNo.OK);
    }
}
