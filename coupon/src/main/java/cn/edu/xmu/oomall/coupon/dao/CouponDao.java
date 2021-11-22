package cn.edu.xmu.oomall.coupon.dao;

import cn.edu.xmu.oomall.core.util.*;
import cn.edu.xmu.oomall.coupon.mapper.CouponActivityPoMapper;
import cn.edu.xmu.oomall.coupon.mapper.CouponOnsalePoMapper;
import cn.edu.xmu.oomall.coupon.model.bo.CouponActivity;
import cn.edu.xmu.oomall.coupon.model.bo.CouponOnsale;
import cn.edu.xmu.oomall.coupon.model.po.CouponActivityPo;
import cn.edu.xmu.oomall.coupon.model.po.CouponActivityPoExample;
import cn.edu.xmu.oomall.coupon.model.po.CouponOnsalePo;
import cn.edu.xmu.oomall.coupon.model.po.CouponOnsalePoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.net.StandardSocketOptions;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qingguo Hu 22920192204208
 */
@Repository
public class CouponDao {

    @Autowired
    private CouponActivityPoMapper couponActivityPoMapper;

    @Autowired
    private CouponOnsalePoMapper couponOnsalePoMapper;

    @Autowired
    private RedisUtil redisUtils;

    @Value("${coupon.coupononsale.expiretime}")
    private long couponOnsaleTimeout;

    @Value("${coupon.couponactivity.expiretime}")
    private long couponActivityTimeout;

    private static final Logger logger = LoggerFactory.getLogger(CouponDao.class);

    public ReturnObject getCouponActivityById(Long id) {
        try {
            String key = "couponactivity_" + id;
            Serializable serializableBo = redisUtils.get(key);
            if (serializableBo != null) {
                // return new ReturnObject<>((CouponActivity)serializableBo);
                return new ReturnObject<>(JacksonUtil.toObj(serializableBo.toString(), CouponActivity.class));
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
            String key = "coupononsale_" + id;
            Serializable serializableBo = redisUtils.get(key);
            if (serializableBo != null) {
                return new ReturnObject<>(JacksonUtil.toObj(serializableBo.toString(), CouponOnsale.class));
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

            List<Object> boList = new ArrayList<>();
            for (Object po : poList) {
                boList.add(Common.cloneVo(po, CouponOnsale.class));
            }
            var pageInfo = new PageInfo<>(boList);
            var temp = new PageInfo<>(poList);
            pageInfo.setPageNum(temp.getPageNum());
            pageInfo.setPageSize(temp.getPageSize());
            pageInfo.setTotal(temp.getTotal());
            pageInfo.setPages(temp.getPages());
            return new ReturnObject<>(pageInfo);
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

            List<Object> boList = new ArrayList<>();
            for (Object po : poList) {
                boList.add(Common.cloneVo(po, CouponOnsale.class));
            }
            var pageInfo = new PageInfo<>(boList);
            var temp = new PageInfo<>(poList);
            pageInfo.setPageNum(temp.getPageNum());
            pageInfo.setPageSize(temp.getPageSize());
            pageInfo.setTotal(temp.getTotal());
            pageInfo.setPages(temp.getPages());
            return new ReturnObject<>(pageInfo);
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

            List<Object> boList = new ArrayList<>();
            for (Object po : poList) {
                boList.add(Common.cloneVo(po, CouponActivity.class));
            }
            var pageInfo = new PageInfo<>(boList);
            var temp = new PageInfo<>(poList);
            pageInfo.setPageNum(temp.getPageNum());
            pageInfo.setPageSize(temp.getPageSize());
            pageInfo.setTotal(temp.getTotal());
            pageInfo.setPages(temp.getPages());
            return new ReturnObject<>(pageInfo);
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
            List<Object> boList = new ArrayList<>();
            for (Object po : poList) {
                boList.add(Common.cloneVo(po, CouponActivity.class));
            }
            var pageInfo = new PageInfo<>(boList);
            var temp = new PageInfo<>(poList);
            pageInfo.setPageNum(temp.getPageNum());
            pageInfo.setPageSize(temp.getPageSize());
            pageInfo.setTotal(temp.getTotal());
            pageInfo.setPages(temp.getPages());
            return new ReturnObject<>(pageInfo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    public ReturnObject updateCouponActivity(CouponActivity couponActivity) {
        try {
            String key = "couponactivity_" + couponActivity.getId();
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
            String key = "coupononsale_" + id;
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
            String key = "couponactivity_" + id;
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
