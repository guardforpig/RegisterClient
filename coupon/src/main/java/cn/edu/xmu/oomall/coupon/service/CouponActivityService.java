package cn.edu.xmu.oomall.coupon.service;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
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

    @Value("${oomall.coupon.coupononsalelist.expiretime}")
    private long couponOnsaleListTimeout;


    private Long totalOfApi1;

    private Integer pagesOfApi1;

    private Long totalOfApi2;

    private Integer pagesOfApi2;

    // 活动查couponOnsale的key
    public final static String COUPONONSALELISTKEY1 = "coupononsalelist1_%d";

    // 商品查couponOnsale的key，存的是productId
    public final static String COUPONONSALELISTKEY2 = "coupononsalelist2_%d";



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
    @Transactional(readOnly = true)
    public ReturnObject listProductsByCouponActivityId(Long couponActivityId, Integer pageNumber, Integer pageSize) {
        // 判断活动存在与否，优惠活动会缓存在redis，已降低负载
        ReturnObject<CouponActivity> retCouponActivity = couponActivityDao.getCouponActivityById(couponActivityId);
        if (!retCouponActivity.getCode().equals(ReturnNo.OK)) {
            return retCouponActivity;
        }
        // 判断活动是否是上线态
        if (!retCouponActivity.getData().getState().equals(CouponActivity.State.ONLINE.getCode())) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }

        // 降低负载，在redis中新增key：activityId, value: List<CouponOnsale>的数据
        // 当dao层增删改couponsale时，会删除对应acitivtyId的redis键值对
        // 获得活动对应的couponOnsale列表
        List<CouponOnsale> couponOnsaleList;
        String key = String.format(COUPONONSALELISTKEY1, couponActivityId);
        Serializable serializable = null;
        serializable = redisUtils.get(key);


        List<Long> onsaleIdList;
        // 途径1：先查redis缓存，判断有没有超出上一次的查询总数totalOfApi1
        if (serializable != null && pageNumber * pageSize <= totalOfApi1) {
            couponOnsaleList = (List<CouponOnsale>) serializable;
            Set<Long> onsaleIdSet = new HashSet<>();
            for (CouponOnsale couponOnsale : couponOnsaleList) {
                onsaleIdSet.add(couponOnsale.getOnsaleId());
            }
            // 存在集合里，去重
            onsaleIdList = new ArrayList<>(onsaleIdSet);
        } else {
            ReturnObject retPageInfo =
                    couponActivityDao.listCouponOnsaleByActivityId(couponActivityId,
                            1, ((pageNumber * pageSize) / 100 + 1) * 100);
            if (!retPageInfo.getCode().equals(ReturnNo.OK)) {
                return retPageInfo;
            }

            Map<String, Object> retMap = (Map<String, Object>) retPageInfo.getData();
            couponOnsaleList = (List<CouponOnsale>) retMap.get("list");

            // 获取所有couponOnsale的onsaleId，存在集合里，去重
            Set<Long> onsaleIdSet = new HashSet<>();
            for (CouponOnsale couponOnsale : couponOnsaleList) {
                onsaleIdSet.add(couponOnsale.getOnsaleId());
            }
            onsaleIdList = new ArrayList<>(onsaleIdSet);

            // 对于这种情况，需要存在redis里
            if (pageNumber * pageSize <= 100) {
                // 将couponOnsale列表存在redis里
                redisUtils.set(key, (Serializable) couponOnsaleList, couponOnsaleListTimeout);
                // 由于在redis中插入了新的数据，所以需要手动更新total、pages
                totalOfApi1 =  (long) onsaleIdList.size() ;
                pagesOfApi1 = (onsaleIdList.size()  - 1 ) / pageSize + 1;
            }
        }

        // 由于去重了，因此需要手动分页
        onsaleIdList = onsaleIdList.subList((pageNumber - 1) * pageSize,
                Math.min(pageNumber * pageSize, onsaleIdList.size()));

        // 根据onsaleId查onsale，并获得productVo，这里有redis缓存，已降低负载
        List<Object> productVoList = new ArrayList<>();
        for (Long onsaleId : onsaleIdList) {
            // 用onsaleId去查onsale，goods有redis缓存
            ReturnObject<OnsaleVo> retOnsaleVo =
                    goodsService.getOnsaleById(onsaleId);
            // 获取onsale里的productVo
            if (retOnsaleVo.getCode().equals(ReturnNo.OK)) {
                productVoList.add(retOnsaleVo.getData().getProduct());
            }
        }

        // 这里无法直接使用getPageRetVo封装，需要手动封装PageInfo
        PageInfo<Object> retPageInfo = new PageInfo<>(productVoList);
        retPageInfo.setTotal(totalOfApi1);
        retPageInfo.setPages(pagesOfApi1);
        retPageInfo.setPageSize(pageSize);
        retPageInfo.setPageNum(pageNumber);
        ReturnObject ret = new ReturnObject<>(retPageInfo);
        return Common.getPageRetVo(ret, ProductVo.class);
    }


    @Transactional(readOnly = true)
    public ReturnObject listCouponActivitiesByProductId(Long productId, Integer pageNumber, Integer pageSize) {
        // 降低负载，在redis中新增key：productId, value: List<CouponOnsale>的数据
        String key = String.format(COUPONONSALELISTKEY2, productId);
        Serializable serializable = redisUtils.get(key);

        List<CouponOnsale> couponOnsaleList;
        // 先查redis缓存，判断有没有超出上一次的查询总数totalOfApi2
        if (serializable != null && pageNumber * pageSize <= totalOfApi2) {
           couponOnsaleList = (List<CouponOnsale>) serializable;
        } else {
            // 找到Product对应的所有OnsaleVo，这里goods模块会缓存在redis，已降低负载
            ReturnObject<List<Object>> retOnsaleVoPageInfo = goodsService.listOnsale(productId, 1, ((pageNumber * pageSize) / 100 + 1) * 100);
            if (!retOnsaleVoPageInfo.getCode().equals(ReturnNo.OK)) {
                return retOnsaleVoPageInfo;
            }

            Map<String, Object> retOnsaleMap = (Map<String, Object>) retOnsaleVoPageInfo.getData();
            List<OnsaleVo> onsaleVoList = (List<OnsaleVo>) retOnsaleMap.get("list");
            couponOnsaleList = new ArrayList<>();
            for (OnsaleVo onsaleVo : onsaleVoList) {
                // 根据OnsaleId的，找出所有对应的CouponOnsale
                ReturnObject<PageInfo<CouponOnsale>> retCouponOnsaleListPage =
                        couponActivityDao.listCouponOnsaleByOnsaleId(onsaleVo.getId(), 1, ((pageNumber * pageSize) / 100 + 1) * 100);
                if (retCouponOnsaleListPage.getCode().equals(ReturnNo.OK)) {
                    Map<String, Object> retCouponOnsaleMap = (Map<String, Object>) retCouponOnsaleListPage.getData();
                    List<CouponOnsale> temp = (List<CouponOnsale>) retCouponOnsaleMap.get("list");
                    for (CouponOnsale couponOnsale : temp) {
                        couponOnsaleList.add(couponOnsale);
                    }
                }
            }
            // 将productId对应的couponOnsale列表存在redis里，降低负载
            redisUtils.set(key, (Serializable) couponOnsaleList, couponOnsaleListTimeout);
        }

        // 去重
        Set<Long> couponActivityIdSet = new HashSet<>();
        for (CouponOnsale couponOnsale : couponOnsaleList) {
            couponActivityIdSet.add(couponOnsale.getActivityId());
        }

        // set转为list
        List<Long> couponActivityIdList = new ArrayList<>(couponActivityIdSet);
        // 若活动列表为空，则返回资源不存在
        if (couponActivityIdList.size() == 0) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }

        // 当redis中没有couponOnsaleList数据时，由于找的是上线的CouponActivity，目前的couponActivityIdList还不知道状态
        // 需要用example判断一下，才能获得total、pages
        // 当redis中有数据时，就直接用之前的total、pages就行
        if (serializable == null) {
            // 根据CouponActivityId的列表，找出所有上线的CouponActivity，并分页
            ReturnObject<PageInfo<Object>> retPageInfo =
                    couponActivityDao.listCouponActivityByIdList(couponActivityIdList, pageNumber, pageSize);
            if (!retPageInfo.getCode().equals(ReturnNo.OK)) {
                return retPageInfo;
            }
            Map<String, Object> retMap = (Map<String, Object>) retPageInfo.getData();
            totalOfApi2 = (long) retMap.get("total");
            pagesOfApi2 = (int) retMap.get("pages");
        }

        // 由于couponActivityIdList不是分页的结果，所以需要手动分页
        couponActivityIdList = couponActivityIdList.subList((pageNumber - 1) * pageSize,
                Math.min(pageNumber * pageSize, couponActivityIdList.size()));

        List<Object> couponActivityVoList = new ArrayList<>();
        for (Long couponActivityId : couponActivityIdList) {
            // redis中有缓存，降低了负载
            ReturnObject<CouponActivity> retCouponActivity = couponActivityDao.getCouponActivityById(couponActivityId);
            if (retCouponActivity.getCode().equals(ReturnNo.OK)) {
                CouponActivity couponActivity = retCouponActivity.getData();
                //
                if (couponActivity.getState().equals(CouponActivity.State.ONLINE.getCode())) {
                    couponActivityVoList.add(Common.cloneVo(couponActivity, CouponActivityRetVo.class));
                }
            }
        }

        // 这里无法直接使用getPageRetVo封装，需要手动封装PageInfo
        PageInfo<Object> retPageInfo = new PageInfo<>(couponActivityVoList);
        retPageInfo.setTotal(totalOfApi2);
        retPageInfo.setPages(pagesOfApi2);
        retPageInfo.setPageSize(pageSize);
        retPageInfo.setPageNum(pageNumber);
        ReturnObject ret = new ReturnObject<>(retPageInfo);
        return Common.getPageRetVo(ret, CouponActivityRetVo.class);
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

        ReturnObject returnObject = couponActivityDao.insertCouponOnsale(newCouponOnsale);
        if (!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }

        // 由于插入了couponOnsale，所以需要找到onsale对应的productId
        // 然后删除1-5第二个api建立的redis索引
        ReturnObject<OnsaleVo> retOnsaleVo1 = goodsService.getOnsaleById(onsaleId);
        Long productId = retOnsaleVo1.getData().getProduct().getId();
        redisUtils.del(String.format(COUPONONSALELISTKEY2, productId));

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
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "该优惠活动不属于该商店");
        }
        if (!formerCouponActivity.getState().equals(CouponActivity.State.DRAFT.getCode())) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }

        // 根据活动找出活动对应的CouponOnsale列表
        ReturnObject<PageInfo<CouponOnsale>> retCouponOnsalePageInfo =
                couponActivityDao.listCouponOnsaleByActivityId(couponActivityId, 1, 0);

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
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "该CouponOnSale参与的优惠活动不属于该商店");
        }
        // 判断是不是在下线态，下线态出错
        if (couponActivity.getState().equals(CouponActivity.State.OFFLINE.getCode())) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }

        ReturnObject returnObject = couponActivityDao.deleteCouponOnsaleById(couponOnsaleId);
        if (!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }

        // 由于删除了couponOnsale，所以需要找到onsale对应的productId
        // 然后删除1-5第二个api建立的redis索引
        ReturnObject<OnsaleVo> retOnsaleVo1 = goodsService.getOnsaleById(retCouponOnsale.getData().getOnsaleId());
        Long productId = retOnsaleVo1.getData().getProduct().getId();
        redisUtils.del(String.format(COUPONONSALELISTKEY2, productId));

        return new ReturnObject<>(ReturnNo.OK);
    }
}
