package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.mapper.OnSalePoMapper;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import cn.edu.xmu.oomall.goods.model.po.OnSalePo;
import cn.edu.xmu.oomall.goods.model.po.OnSalePoExample;
import cn.edu.xmu.oomall.goods.model.vo.NewOnSaleRetVo;
import cn.edu.xmu.oomall.goods.model.vo.SimpleOnSaleRetVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/13 02:49
 **/
@Repository
public class OnSaleGetDao {
    @Autowired
    private OnSalePoMapper onSalePoMapper;

    private static final Logger logger = LoggerFactory.getLogger(OnSaleDao.class);

    public ReturnObject selectOnSale(Long id){
        try {
            OnSalePo onSalePo=onSalePoMapper.selectByPrimaryKey(id);
            if(onSalePo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }else{
                OnSale onSale=(OnSale) Common.cloneVo(onSalePo, OnSale.class);
                return new ReturnObject(onSale);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    private ReturnObject selectOnsaleByExampleWithPageInfo(OnSalePoExample onSalePoExample,Class voClass,
                                                           Integer page,Integer pageSize){
        try{
            PageHelper.startPage(page,pageSize);
            List<OnSalePo>onSalePos=onSalePoMapper.selectByExample(onSalePoExample);
            PageInfo<OnSalePo>pageInfo=new PageInfo<OnSalePo>(onSalePos);
            ReturnObject ret = new ReturnObject(pageInfo);
            return Common.getPageRetVo(ret, voClass);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject selectCertainOnsale(Long shopId,Long id,Integer page,Integer pageSize){
        OnSalePoExample onSalePoExample = new OnSalePoExample();
        OnSalePoExample.Criteria criteria = onSalePoExample.createCriteria();
        criteria.andShopIdEqualTo(shopId);
        criteria.andProductIdEqualTo(id);
        List<Byte> types = Arrays.asList(OnSale.Type.NOACTIVITY.getCode(),OnSale.Type.SECKILL.getCode());
        criteria.andTypeIn(types);
        ReturnObject returnObject = selectOnsaleByExampleWithPageInfo(onSalePoExample,SimpleOnSaleRetVo.class, page, pageSize);
        return returnObject;
    }

    public ReturnObject selectActivities(Long id,Long did,Byte state,
                                         LocalDateTime beginTime,LocalDateTime endTime,
                                         Integer page,Integer pageSize){
        OnSalePoExample onSalePoExample=new OnSalePoExample();
        OnSalePoExample.Criteria criteria=onSalePoExample.createCriteria();
        criteria.andActivityIdEqualTo(id);
        criteria.andShopIdEqualTo(did);
        List<Byte>types=Arrays.asList(OnSale.Type.GROUPON.getCode(),OnSale.Type.PRESALE.getCode());
        criteria.andTypeIn(types);
        if(state!=null){
            criteria.andStateEqualTo(state);
        }
        if(beginTime!=null){
            criteria.andBeginTimeGreaterThanOrEqualTo(beginTime);
        }
        if(endTime!=null){
            criteria.andEndTimeLessThanOrEqualTo(endTime);
        }
        ReturnObject returnObject = selectOnsaleByExampleWithPageInfo(onSalePoExample,SimpleOnSaleRetVo.class, page, pageSize);
        return returnObject;
    }

    public ReturnObject selectShareActivities(Long did,Long id,Byte state,Integer page,Integer pageSize){
        OnSalePoExample onSalePoExample=new OnSalePoExample();
        OnSalePoExample.Criteria criteria=onSalePoExample.createCriteria();
        criteria.andShareActIdEqualTo(id);
        criteria.andShopIdEqualTo(did);
        if(state!=null){
            criteria.andStateEqualTo(state);
        }
        ReturnObject returnObject = selectOnsaleByExampleWithPageInfo(onSalePoExample, SimpleOnSaleRetVo.class,page, pageSize);
        return returnObject;
    }

    public ReturnObject selectAnyOnsale(Long shopId,Long productId,LocalDateTime beginTime,
                                        LocalDateTime endTime,Integer page,Integer pageSize){
        OnSalePoExample onSalePoExample=new OnSalePoExample();
        OnSalePoExample.Criteria criteria=onSalePoExample.createCriteria();
        if(shopId!=null){
            criteria.andShopIdEqualTo(shopId);
        }
        if(productId!=null){
            criteria.andProductIdEqualTo(productId);
        }
        if(beginTime!=null){
            criteria.andBeginTimeGreaterThanOrEqualTo(beginTime);
        }
        if(endTime!=null){
            criteria.andEndTimeLessThanOrEqualTo(endTime);
        }
        ReturnObject returnObject = selectOnsaleByExampleWithPageInfo(onSalePoExample,SimpleOnSaleRetVo.class, page, pageSize);
        return returnObject;
    }

}
