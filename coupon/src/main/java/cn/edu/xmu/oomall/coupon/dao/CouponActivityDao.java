package cn.edu.xmu.oomall.coupon.dao;


import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.*;
import cn.edu.xmu.oomall.coupon.mapper.CouponActivityPoMapper;
import cn.edu.xmu.oomall.coupon.mapper.CouponOnsalePoMapper;
import cn.edu.xmu.oomall.coupon.model.bo.CouponActivity;
import cn.edu.xmu.oomall.coupon.model.bo.CouponOnsale;
import cn.edu.xmu.oomall.coupon.model.po.CouponActivityPo;
import cn.edu.xmu.oomall.coupon.model.po.CouponActivityPoExample;
import cn.edu.xmu.oomall.coupon.model.po.CouponOnsalePo;
import cn.edu.xmu.oomall.coupon.model.po.CouponOnsalePoExample;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityRetVo;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityVoInfo;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RenJieZheng 22920192204334
 */
/**
 * @author qingguo Hu 22920192204208
 */
@Repository
public class CouponActivityDao {
    @Autowired
    CouponActivityPoMapper couponActivityPoMapper;

    @Autowired
    CouponOnsalePoMapper couponOnsalePoMapper;

    @Autowired
    private RedisUtil redisUtils;

    @Value("${webdav.user}")
    String webDavUser;

    @Value("${webdav.password}")
    String webDavPassword;

    @Value("${webdav.baseurl}")
    String baseUrl;

    @Value("${oomall.coupon.coupononsale.expiretime}")
    private long couponOnsaleTimeout;

    @Value("${oomall.coupon.couponactivity.expiretime}")
    private long couponActivityTimeout;

    @Value("${oomall.coupon.productlist.expiretime}")
    private long productListTimeout;

    @Value("${oomall.coupon.couponactivitylist.expiretime}")
    private long couponActivityListTimeout;

    private static final Logger logger = LoggerFactory.getLogger(CouponActivityDao.class);

    public final static String COUPONACTIVITYKEY = "couponactivity_%d";

    public final static String COUPONONSALEKEY = "coupononsale_%d";




    /**
     * 查看优惠活动模块的所有活动
     * @return ReturnObject<List<Map<String, Object>>>
     */
    public ReturnObject<List<Map<String, Object>>> showAllState() {
        List<Map<String, Object>> stateList = new ArrayList<>();
        for (CouponActivity.State states : CouponActivity.State.values()) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("code", states.getCode());
            temp.put("name", states.getDescription());
            stateList.add(temp);
        }
        return new ReturnObject<>(stateList);
    }

    /**
     * 根据查询条件获得分页的结果
     * @param example 查询条件
     * @param page 页
     * @param pageSize 页大小
     * @return 优惠活动列表 List<CouponActivityRetVo>
     */
    public ReturnObject<PageInfo<VoObject>> showCouponActivitiesByExample(CouponActivityPoExample example,Integer page, Integer pageSize){
        try{
            PageHelper.startPage(page,pageSize);
            List<CouponActivityPo>list = couponActivityPoMapper.selectByExample(example);
            List<VoObject>list1 = new ArrayList<>();
            for(CouponActivityPo couponActivityPo:list){
                CouponActivityRetVo couponActivityRetVo = (CouponActivityRetVo) Common.cloneVo(couponActivityPo,CouponActivityRetVo.class);
                list1.add(couponActivityRetVo);
            }
            PageInfo<VoObject> pageInfo = new PageInfo<>(list1);
            return new ReturnObject<>(pageInfo);
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }

    }


    /**
     * 管理员新建己方优惠活动
     * @param couponActivity 优惠活动
     * @return 插入结果
     */
    public ReturnObject<CouponActivityRetVo> addCouponActivity(CouponActivity couponActivity){
        int ret;
        try{
            CouponActivityPo couponActivityPo = (CouponActivityPo) Common.cloneVo(couponActivity,CouponActivityPo.class);
            ret = couponActivityPoMapper.insert(couponActivityPo);
            if (ret == 0) {
                return new ReturnObject<>(ReturnNo.FIELD_NOTVALID);
            } else {
                //插入成功将数据返回
                CouponActivityRetVo couponActivityRetVo = (CouponActivityRetVo) Common.cloneVo(couponActivityPo,CouponActivityRetVo.class);
                return new ReturnObject<>(couponActivityRetVo);
            }
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }

    }



    /**
     * 根据主码直接返回优惠活动信息
     * @param id 优惠活动主码
     * @return CouponActivityVoInfo
     */
    public ReturnObject<CouponActivityVoInfo>showCouponActivityPoStraight(Long id,CouponActivity couponActivity){
        try{
            CouponActivityPo couponActivityPo = couponActivityPoMapper.selectByPrimaryKey(id);
            if (couponActivityPo == null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(!couponActivityPo.getShopId().equals(couponActivity.getShopId())){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            //下线状态不给返回
            if(couponActivityPo.getState()==CouponActivity.State.OFFLINE.getCode().byteValue()){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject<>(new CouponActivityVoInfo(couponActivityPo));
        }catch(Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }

    }

    /**
     * 上传文件url
     * @param id 优惠活动id
     * @param couponActivity 优惠活动
     * @return 上传结果，成功返回旧的ImageUrl
     */
    public ReturnObject<CouponActivityPo> updateImageUrl(Long id, CouponActivity couponActivity, MultipartFile multipartFile){
        int ret;
        try{
           CouponActivityPo couponActivityPo = couponActivityPoMapper.selectByPrimaryKey(id);
            // 资源找不到
            if (couponActivityPo == null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            // 如果根据活动id找不到活动或者活动的店铺不是shopId
            if(!couponActivityPo.getShopId().equals(couponActivity.getShopId())){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            //当前状态不支持
            if(couponActivityPo.getState()!=CouponActivity.State.DRAFT.getCode().byteValue()){
                return new ReturnObject<>(ReturnNo.STATENOTALLOW);
            }
            // 原来的图片Url
            String oldUrl = couponActivityPo.getImageUrl();
            //如果文件不为空，需要将删除ImageHelper中的原来文件
            if(oldUrl!=null){
                ImgHelper.deleteRemoteImg(oldUrl,webDavUser,webDavPassword,baseUrl);
            }
            //将新增的文件上传到dav中
            ReturnObject returnObject1 = ImgHelper.remoteSaveImg(multipartFile,1000000,webDavUser,webDavPassword,baseUrl);
            if(!returnObject1.getCode().equals(ReturnNo.OK)){
                // 返回调用ImageHelper的错误信息
                return returnObject1;
            }
            String newUrl = (String)returnObject1.getData();
            //将更新加入
            couponActivityPo.setImageUrl(newUrl);
            ret = couponActivityPoMapper.updateByPrimaryKeySelective(couponActivityPo);
            if (ret == 0) {
                return new ReturnObject<>(ReturnNo.FIELD_NOTVALID);
            } else {
               return new ReturnObject<>(ReturnNo.OK);
            }
        }catch(Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }




    /**
     * @author qingguo Hu 22920192204208
     */
    public ReturnObject getCouponActivityById(Long id) {
        try {
            String key = String.format(COUPONACTIVITYKEY, id);
            Serializable serializableBo = redisUtils.get(key);
            if (serializableBo != null) {
                return new ReturnObject<>((CouponActivity) serializableBo);
            }
            CouponActivityPo po = couponActivityPoMapper.selectByPrimaryKey(id);
            if (po == null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            CouponActivity couponActivity = (CouponActivity) Common.cloneVo(po, CouponActivity.class);
            redisUtils.set(key, couponActivity, couponActivityTimeout);
            return new ReturnObject<>(couponActivity);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject getCouponOnsaleById(Long id ) {
        try {
            String key = String.format(COUPONONSALEKEY, id);
            Serializable serializableBo = redisUtils.get(key);
            if (serializableBo != null) {
                return new ReturnObject<>((CouponOnsale) serializableBo);
            }
            CouponOnsalePo po = couponOnsalePoMapper.selectByPrimaryKey(id);
            if (po == null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            CouponOnsale couponOnsale = (CouponOnsale) Common.cloneVo(po, CouponOnsale.class);
            redisUtils.set(key, couponOnsale, couponOnsaleTimeout);
            return new ReturnObject<>(couponOnsale);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject listCouponOnsaleByActivityId(Long activityId, Integer pageNumber, Integer pageSize) {
        try {
            PageHelper.startPage(pageNumber, pageSize, true, true, true);
            CouponOnsalePoExample example = new CouponOnsalePoExample();
            example.createCriteria().andActivityIdEqualTo(activityId);
            List<CouponOnsalePo> poList = couponOnsalePoMapper.selectByExample(example);
            if (poList.size() == 0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }

            ReturnObject ret = new ReturnObject<>(new PageInfo<>(poList));
            return Common.getPageRetVo(ret, CouponOnsale.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject listCouponOnsaleByIdList(List<Long> onsaleIdList, Integer pageNumber, Integer pageSize) {
        try {
            PageHelper.startPage(pageNumber, pageSize, true, true, true);
            CouponOnsalePoExample example = new CouponOnsalePoExample();
            example.createCriteria().andOnsaleIdIn(onsaleIdList);
            List<CouponOnsalePo> poList = couponOnsalePoMapper.selectByExample(example);
            if (poList.size() == 0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }

            ReturnObject ret = new ReturnObject<>(new PageInfo<>(poList));
            return Common.getPageRetVo(ret, CouponOnsale.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject listCouponActivityByIdList(List<Long> idList, Integer pageNumber, Integer pageSize) {
        try {
            PageHelper.startPage(pageNumber, pageSize, true, true, true);
            CouponActivityPoExample example = new CouponActivityPoExample();
            example.createCriteria()
                    .andIdIn(idList)
                    .andStateEqualTo(CouponActivity.State.ONLINE.getCode());
            List<CouponActivityPo> poList = couponActivityPoMapper.selectByExample(example);
            if (poList.size() == 0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }

            ReturnObject ret = new ReturnObject<>(new PageInfo<>(poList));
            return Common.getPageRetVo(ret, CouponActivity.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject listCouponOnsaleByOnsaleIdAndActivityId(Long onsaleId, Long activityId, Integer pageNumber, Integer pageSize) {
        try {
            PageHelper.startPage(pageNumber, pageSize, true, true, true);
            CouponOnsalePoExample example = new CouponOnsalePoExample();
            example.createCriteria().andOnsaleIdEqualTo(onsaleId).andActivityIdEqualTo(activityId);
            List<CouponOnsalePo> poList = couponOnsalePoMapper.selectByExample(example);
            if (poList.size() == 0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }

            ReturnObject ret = new ReturnObject<>(new PageInfo<>(poList));
            return Common.getPageRetVo(ret, CouponOnsale.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    public ReturnObject updateCouponActivity(CouponActivity couponActivity) {
        try {
            String key = String.format(COUPONACTIVITYKEY, couponActivity.getId());
            CouponActivityPo couponActivityPo =
                    (CouponActivityPo) Common.cloneVo(couponActivity, CouponActivityPo.class);
            int flag = couponActivityPoMapper.updateByPrimaryKeySelective(couponActivityPo);
            if (flag == 0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                redisUtils.del(key);
                return new ReturnObject<>(ReturnNo.OK);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    public ReturnObject insertCouponOnsale(CouponOnsale couponOnsale) {
        try {
            CouponOnsalePo couponOnsalePo =
                    (CouponOnsalePo) Common.cloneVo(couponOnsale, CouponOnsalePo.class);
            couponOnsalePoMapper.insert(couponOnsalePo);
            return new ReturnObject<>(ReturnNo.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject deleteCouponOnsaleById(Long id) {
        try {
            String key = String.format(COUPONONSALEKEY, id);
            int flag = couponOnsalePoMapper.deleteByPrimaryKey(id);
            if (flag == 0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                redisUtils.del(key);
                return new ReturnObject<>(ReturnNo.OK);
            }
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject deleteCouponActivityById(Long id) {
        try {
            String key = String.format(COUPONACTIVITYKEY, id);
            int flag = couponActivityPoMapper.deleteByPrimaryKey(id);
            if (flag == 0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                redisUtils.del(key);
                return new ReturnObject<>(ReturnNo.OK);
            }
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
}
