package cn.edu.xmu.oomall.coupon.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.mapper.CouponActivityPoMapper;
import cn.edu.xmu.oomall.coupon.mapper.CouponOnsalePoMapper;
import cn.edu.xmu.oomall.coupon.model.bo.CouponActivity;
import cn.edu.xmu.oomall.coupon.model.bo.CouponOnsale;
import cn.edu.xmu.oomall.coupon.model.po.CouponActivityPo;
import cn.edu.xmu.oomall.coupon.model.po.CouponOnsalePo;
import cn.edu.xmu.oomall.coupon.model.po.CouponOnsalePoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qingguo Hu 22920192204208
 */
@Repository
public class CouponDao {

    @Autowired
    private CouponActivityPoMapper couponActivityPoMapper;

    @Autowired
    private CouponOnsalePoMapper couponOnsalePoMapper;

    private Map<Class, Map<String, Object>> boMap;

    private static final Logger logger = LoggerFactory.getLogger(CouponDao.class);

    private void initBoMap() {
        boMap = new HashMap<>() {
            final Map<String, Object> CouponActivityMap = new HashMap<>() {{
                put("PoClass", CouponActivityPo.class);
                put("Mapper", couponActivityPoMapper);
            }};
            final Map<String, Object> CouponOnsaleMap = new HashMap<>() {{
                put("PoClass", CouponOnsalePo.class);
                put("Mapper", couponOnsalePoMapper);
            }};
            {
                put(CouponActivity.class, CouponActivityMap);
                put(CouponOnsale.class, CouponOnsaleMap);
            }};
    }

    public ReturnObject getBoByPrimaryKey(Long id, Class boClass) {
        initBoMap();
        try {
            Method selectMethod = boMap.get(boClass).get("Mapper").getClass().getMethod("selectByPrimaryKey", Long.class);
            Object po = selectMethod.invoke(boMap.get(boClass).get("Mapper"), id);
            if (po == null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, ReturnNo.RESOURCE_ID_NOTEXIST.getMessage());
            }

            return new ReturnObject<>(Common.cloneVo(po, boClass));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    public ReturnObject listBoByExample(Object example, Class boClass, Integer pageNumber, Integer pageSize) {
        initBoMap();
        try {
            PageHelper.startPage(pageNumber, pageSize, true, true, true);
            Method selectMethod = boMap.get(boClass).get("Mapper").getClass().getMethod("selectByExample", example.getClass());
            List<Object> poList = (List<Object>) selectMethod.invoke(boMap.get(boClass).get("Mapper"), example);

            if (poList.size() == 0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, ReturnNo.RESOURCE_ID_NOTEXIST.getMessage());
            }

            var pageInfo = new PageInfo<>(poList);
            List<Object> boList = new ArrayList<>();
            for (Object po : poList) {
                boList.add(Common.cloneVo(po, boClass));
            }

            pageInfo.setList(boList);
            pageInfo.setPageNum(pageNumber);
            pageInfo.setPageSize(pageSize);
            return new ReturnObject<>(pageInfo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject insertBo(Object bo, Class poClass) {
        initBoMap();
        try {
            Object po = Common.cloneVo(bo, poClass);
            Method insertMethod = boMap.get(bo.getClass()).get("Mapper").getClass().getMethod("insert", poClass);
            insertMethod.invoke(boMap.get(bo.getClass()).get("Mapper"), po);
            return new ReturnObject<>(ReturnNo.OK, ReturnNo.OK.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject updateBo(Object bo, Class poClass) {
        initBoMap();
        try {
            Object po = Common.cloneVo(bo, poClass);
            Method updateMethod = boMap.get(bo.getClass()).get("Mapper").getClass().getMethod("updateByPrimaryKeySelective", poClass);
            int flag = (int) updateMethod.invoke(boMap.get(bo.getClass()).get("Mapper"), po);
            if (flag == 0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, ReturnNo.RESOURCE_ID_NOTEXIST.getMessage());
            } else {
                return new ReturnObject<>(ReturnNo.OK, ReturnNo.OK.getMessage());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject deleteBoByPrimaryKey(Long id, Class boClass) {
        initBoMap();
        try {
            Method deleteMethod = boMap.get(boClass).get("Mapper").getClass().getMethod("deleteByPrimaryKey", Long.class);
            int flag = (int) deleteMethod.invoke(boMap.get(boClass).get("Mapper"), id);
            if (flag == 0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, ReturnNo.RESOURCE_ID_NOTEXIST.getMessage());
            } else {
                return new ReturnObject<>(ReturnNo.OK, ReturnNo.OK.getMessage());
            }
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, ReturnNo.INTERNAL_SERVER_ERR.getMessage());
        }
    }

}
