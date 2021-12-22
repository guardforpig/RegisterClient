package cn.edu.xmu.oomall.coupon.service;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.coupon.dao.CouponActivityDao;
import cn.edu.xmu.oomall.coupon.microservice.GoodsService;
import cn.edu.xmu.oomall.coupon.microservice.vo.OnsaleVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.PageVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.ProductRetVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.ProductVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.ShopRetVo;
import cn.edu.xmu.oomall.coupon.model.bo.CouponActivity;
import cn.edu.xmu.oomall.coupon.model.bo.CouponOnsale;
import cn.edu.xmu.oomall.coupon.model.bo.OrderItem;
import cn.edu.xmu.oomall.coupon.model.bo.Shop;
import cn.edu.xmu.oomall.coupon.model.bo.strategy.BaseCouponDiscount;
import cn.edu.xmu.oomall.coupon.model.po.CouponActivityPoExample;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityDetailRetVo;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityRetVo;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityVo;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityVoInfo;
import cn.edu.xmu.oomall.coupon.model.vo.*;
import cn.edu.xmu.oomall.coupon.microservice.ShopFeignService;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    public ReturnObject<PageInfo<VoObject>> showOwnCouponActivities(Long shopId, ZonedDateTime beginTime, ZonedDateTime endTime, Integer page, Integer pageSize){
        //添加查询条件
        CouponActivityPoExample example = new CouponActivityPoExample();
        CouponActivityPoExample.Criteria criteria = example.createCriteria();
        if(shopId!=null){
            criteria.andShopIdEqualTo(shopId);
        }
        if(beginTime!=null){
            criteria.andBeginTimeGreaterThan(beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
        }
        if(endTime!=null){
            criteria.andBeginTimeLessThan(endTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
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
    public ReturnObject<PageInfo<VoObject>> showOwnCouponActivities1(Long shopId,ZonedDateTime beginTime,ZonedDateTime endTime,Byte state,Integer page,Integer pageSize){
        //查询条件
        CouponActivityPoExample example = new CouponActivityPoExample();
        CouponActivityPoExample.Criteria criteria = example.createCriteria();
        if(shopId!=null){
            criteria.andShopIdEqualTo(shopId);
        }
        if(beginTime!=null){
            criteria.andBeginTimeGreaterThan(beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
        }
        if(endTime!=null){
            criteria.andBeginTimeLessThan(endTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
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
        InternalReturnObject returnObject;
        try{
            returnObject = shopFeignService.getSimpleShopById(shopId);
        }catch(Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        ShopRetVo shop = (ShopRetVo) returnObject.getData();
        CouponActivity couponActivity = cloneVo(couponActivityVo,CouponActivity.class);
        // TODO: 2021/12/11 改进cloneVo,localDateTime和zonedDateTime互转,一下几行行代码需删除
        //将时区时间转为UTC时间并转成localdatetime
        LocalDateTime couponTime = couponActivityVo.getCouponTime().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();;
        LocalDateTime beginTime = couponActivityVo.getBeginTime().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime endTime = couponActivityVo.getBeginTime().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        couponActivity.setCouponTime(couponTime);
        couponActivity.setBeginTime(beginTime);
        couponActivity.setEndTime(endTime);
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
        // TODO: 2021/12/11 改进cloneVo,localDateTime和zonedDateTime互转
        return new ReturnObject<>(cloneVo(couponActivity,CouponActivityVoInfo.class));
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
            if (redisUtils.hasKey(key) && (long) (pageNumber - 1) * pageSize <= redisUtils.sizeList(key)) {
                long beginIndex = (long) (pageNumber - 1) * pageSize;
                long endIndex = Math.min((long) pageNumber * pageSize, redisUtils.sizeList(key));
                List<Serializable> serializableList = redisUtils.rangeList(key, beginIndex, endIndex);
                for (Serializable serializable : serializableList) {
                    onlineCouponActivityList.add((CouponActivity) serializable);
                }
            } else {
                redisUtils.del(key);
                InternalReturnObject<PageVo<OnsaleVo>> retOnsaleVoPageInfo =
                        goodsService.listOnsale(productId, 1, ((pageNumber * pageSize) / listDefaultSize + 1) * listDefaultSize);
                if (!retOnsaleVoPageInfo.getErrno().equals(ReturnNo.OK.getCode())) {
                    return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
                }
                List<OnsaleVo> onsaleVoList = retOnsaleVoPageInfo.getData().getList();
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

    /**
    * @author jxy
    * @create 2021/12/14 8:31 PM
    */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getCouponActivityById(Long id){
        ReturnObject returnObject = couponActivityDao.showCouponActivityPoStraight(id);
        if(returnObject.getCode()!= ReturnNo.OK){
            return returnObject;
        }
        CouponActivity couponActivity = (CouponActivity) returnObject.getData();

        //下线状态不给返回
        if(couponActivity.getState()==CouponActivity.State.OFFLINE.getCode().byteValue()){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }

        return new ReturnObject(cloneVo(couponActivity, CouponActivityDetailRetVo.class));
    }

    /**
    * @author jxy
    * @create 2021/12/14 10:14 PM
    */
    //todo:可能发生并发读写问题
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject decreaseCoupons(Long id){
        ReturnObject returnObject = couponActivityDao.showCouponActivityPoStraight(id);
        if(returnObject.getCode()!= ReturnNo.OK){
            return returnObject;
        }
        CouponActivity couponActivity = (CouponActivity) returnObject.getData();

        //下线状态不给返回
        if(couponActivity.getState()==CouponActivity.State.OFFLINE.getCode().byteValue()){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        couponActivity.setQuantity(couponActivity.getQuantity()-1);
        return couponActivityDao.updateCouponActivity(couponActivity);

    }






    /**
     * @author Zijun Min 22920192204257
     * 计算商品优惠价格
     */
    public ReturnObject calculateDiscount(Map<Long,List<OrderItem>>itemsMap, List<DiscountRetVo>discountRetVos) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        //计算优惠价格
        for(Long id:itemsMap.keySet()){
            ReturnObject objCouponAct=couponActivityDao.getCouponActivityById(id);
            if(!objCouponAct.getCode().equals(ReturnNo.OK)){
                return objCouponAct;
            }
            CouponActivity couponActivity=(CouponActivity)objCouponAct.getData();
            BaseCouponDiscount baseCouponDiscount= BaseCouponDiscount.getInstance(couponActivity.getStrategy());
            baseCouponDiscount.compute(itemsMap.get(id));
            for(OrderItem orderItem:itemsMap.get(id)){
                DiscountRetVo discountRetVo=cloneVo(orderItem,DiscountRetVo.class);
                discountRetVo.setActivityId(orderItem.getCouponActivityId());
                discountRetVo.setDiscountPrice(orderItem.getDiscount());
                discountRetVos.add(discountRetVo);
            }
        }
        return new ReturnObject(discountRetVos);
    }

    /**
     * @author Zijun Min 22920192204257
     * 根据设定的优惠活动计算当前有效的商品优惠价格
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject calculateActivityDiscount(List<DiscountItemVo>items) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        //activityId和对应的orderItem列表
        Map<Long,List<OrderItem>>itemsMap=new HashMap<Long,List<OrderItem>>();
        List<DiscountRetVo>discountRetVos=new ArrayList<>();

        for(DiscountItemVo item:items){
            OrderItem orderItem=item.getOrderItem();
            //通过product找到categoryId
            InternalReturnObject<ProductRetVo> objProduct=goodsService.getProductById(item.getProductId());
            if(!objProduct.getErrno().equals(0)){
                return new ReturnObject(objProduct);
            }
            ProductRetVo productRetVo=(ProductRetVo) objProduct.getData();
            //product中对应的onsaleId和传入参数的onSaleId不一致
            if(productRetVo.getOnSaleId()==null||!productRetVo.getOnSaleId().equals(item.getOnsaleId())){
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            orderItem.setCategoryId(productRetVo.getCategory().getId());

            //没有优惠活动的商品，直接放入返回值DiscountRetVo的列表中
            if(item.getActivityId()==null){
                DiscountRetVo discountRetVo=cloneVo(item,DiscountRetVo.class);
                discountRetVo.setDiscountPrice(item.getOriginalPrice());
                discountRetVos.add(discountRetVo);
            }
            //有优惠活动的商品，放入map
            else {
                //将orderItem按照优惠活动id分类放在map中
                if (itemsMap.containsKey(item.getActivityId())) {
                    itemsMap.get(item.getActivityId()).add(orderItem);
                } else {
                    List<OrderItem> orderItems = new ArrayList<>();
                    orderItems.add(orderItem);
                    itemsMap.put(item.getActivityId(), orderItems);
                }
            }
        }
        return calculateDiscount(itemsMap,discountRetVos);
    }

    /**
     * @author Zijun Min 22920192204257
     * 有多个优惠活动的onsale，计算最优优惠组合
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject chooseActivitiesBest(Map<Long,List<OrderItem>>actItemsMap) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        //activityId和对应的discount
        Map<Long,Long>actDiscountMap=new HashMap<>();

        //对于每一个活动对应的所有orderItem，分别计算每一种活动的discount，选取优惠最高的活动，将优惠额度记录在itemDiscountMap中
        for(Long actId:actItemsMap.keySet()){
            //计算每一种活动的discount
            ReturnObject objCouponAct=couponActivityDao.getCouponActivityById(actId);
            if(!objCouponAct.getCode().equals(ReturnNo.OK)){
                return objCouponAct;
            }
            CouponActivity couponActivity=(CouponActivity)objCouponAct.getData();
            BaseCouponDiscount baseCouponDiscount= BaseCouponDiscount.getInstance(couponActivity.getStrategy());
            baseCouponDiscount.compute(actItemsMap.get(actId));
            Long discount=0L;
            for(OrderItem orderItem:actItemsMap.get(actId)){
                discount+=orderItem.getPrice()-orderItem.getDiscount();
            }
            actDiscountMap.put(actId,discount);
        }

        //按照优惠金额降序排列，优惠更多的活动id会覆盖优惠低的活动id，以下5行排序代码参考gxc同学
        List<Map.Entry<Long,Long>> actDiscountList = new ArrayList<Map.Entry<Long,Long>>(actDiscountMap.entrySet());
        Collections.sort(actDiscountList, new Comparator<Map.Entry<Long,Long>>(){
            @Override
            public int compare(Map.Entry<Long,Long> o1, Map.Entry<Long,Long> o2) {
                return (o2.getValue().intValue()- o1.getValue().intValue());
            }
        });

        //activityId和对应的orderItems，orderItem无重复
        Map<Long,List<OrderItem>>itemsMap=new HashMap<>();
        //记录已经确定优惠活动的product
        List<Long>calculatedProducts=new ArrayList<>();
        //将orderItem的activityId更新，并插入itemsMap中
        for(int i=0;i<actDiscountList.size();i++){
            Long activityId=actDiscountList.get(i).getKey();
            List<OrderItem>orderItems=actItemsMap.get(activityId);
            for(int j=0;j<orderItems.size();j++){
                OrderItem orderItem=orderItems.get(j);
                if(!calculatedProducts.contains(orderItem.getProductId())) {
                    orderItem.setCouponActivityId(activityId);
                    calculatedProducts.add(orderItem.getProductId());
                    if (itemsMap.containsKey(activityId)) {
                        itemsMap.get(activityId).add(orderItem);
                    } else {
                        List<OrderItem> orderItemList = new ArrayList<>();
                        orderItemList.add(orderItem);
                        itemsMap.put(activityId, orderItemList);
                    }
                }
            }
        }
        return new ReturnObject<>(itemsMap);
    }

    /**
     * @author Zijun Min 22920192204257
     * 计算当前有效的最优优惠活动方案
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject calculateDiscountBest(List<DiscountItemVo>items) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        List<DiscountRetVo>discountRetVos=new ArrayList<>();
        //activityId和对应的orderItem列表，orderItem有重复
        Map<Long,List<OrderItem>>actItemsMap=new HashMap<Long,List<OrderItem>>();

        for(DiscountItemVo item:items) {
            OrderItem orderItem = item.getOrderItem();
            //通过productId找到categoryId
            InternalReturnObject objProduct = goodsService.getProductById(item.getProductId());
            if (!objProduct.getErrno().equals(0)) {
                return new ReturnObject(objProduct);
            }
            ProductRetVo productRetVo = (ProductRetVo) objProduct.getData();
            //product中对应的onsaleId和传入参数的onSaleId不一致
            if (!productRetVo.getOnSaleId().equals(item.getOnsaleId())) {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            orderItem.setCategoryId(productRetVo.getCategory().getId());

            ReturnObject objCouponAct = couponActivityDao.getCouponActivitiesListByOnsaleId(item.getOnsaleId());
            if (!objCouponAct.getCode().equals(ReturnNo.OK)) {
                return objCouponAct;
            }
            List<CouponActivity> couponActivityList = (List<CouponActivity>) objCouponAct.getData();
            //没有任何优惠，直接原价
            if (couponActivityList.isEmpty()) {
                DiscountRetVo discountRetVo = cloneVo(item, DiscountRetVo.class);
                discountRetVo.setDiscountPrice(item.getOriginalPrice());
                discountRetVos.add(discountRetVo);
            } else {
                //把优惠和orderItem加到map中
                for(CouponActivity couponActivity:couponActivityList){
                    if (actItemsMap.containsKey(couponActivity.getId())) {
                        actItemsMap.get(couponActivity.getId()).add(orderItem);
                    } else {
                        List<OrderItem> orderItems = new ArrayList<>();
                        orderItems.add(orderItem);
                        actItemsMap.put(couponActivity.getId(), orderItems);
                    }
                }
            }
        }

        ReturnObject objBest = chooseActivitiesBest(actItemsMap);
        if(!objBest.getCode().equals(ReturnNo.OK)){
            return objBest;
        }
        Map<Long,List<OrderItem>>chooseMap=(Map<Long, List<OrderItem>>) objBest.getData();
        return calculateDiscount(chooseMap,discountRetVos);
    }
}
