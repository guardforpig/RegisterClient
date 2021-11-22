package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.RedisUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.mapper.OnSalePoMapper;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import cn.edu.xmu.oomall.goods.model.po.OnSalePo;
import cn.edu.xmu.oomall.goods.model.po.OnSalePoExample;
import cn.edu.xmu.oomall.goods.model.vo.NewOnSaleRetVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

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

    /**
     * 活动类型
     */
    private final Byte NO_ACTIVITY=0;
    private final Byte SECOND_KILL=1;
    private final Byte GROUPON=2;
    private final Byte ADVANCE_SALE=3;

    @Autowired
    private OnSalePoMapper onSalePoMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${oomall.goods.onsale.expiretime}")
    private Long onSaleTimeout;

    private static final Logger logger = LoggerFactory.getLogger(OnSaleDao.class);

    public ReturnObject selectOnSale(Long id){
        try {
            String onSaleKey="o_"+id;
            OnSale onSaleRedis = (OnSale) redisUtil.get(onSaleKey);
            if(onSaleRedis==null){
                OnSalePo onSalePo=onSalePoMapper.selectByPrimaryKey(id);
                if(onSalePo==null){
                    return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
                }else{
                    OnSale onSale=(OnSale) Common.cloneVo(onSalePo, OnSale.class);
                    redisUtil.set("o_"+onSale.getId(), onSale,onSaleTimeout);
                    return new ReturnObject(onSale);
                }
            }else{
                return new ReturnObject(onSaleRedis);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    private ReturnObject<PageInfo<VoObject>> selectOnsaleByExampleWithPageInfo(OnSalePoExample onSalePoExample,Integer page,Integer pageSize){
        try{
            PageHelper.startPage(page,pageSize);
            List<OnSalePo>onSalePos=onSalePoMapper.selectByExample(onSalePoExample);
            List<VoObject>simpleOnsaleRetVos=new ArrayList<>();
            for(OnSalePo onSalePo:onSalePos){
                NewOnSaleRetVo simpleOnsaleRetVo = (NewOnSaleRetVo) Common.cloneVo(onSalePo, NewOnSaleRetVo.class);
                simpleOnsaleRetVos.add(simpleOnsaleRetVo);
            }
            PageInfo<VoObject> pageInfo = new PageInfo<>(simpleOnsaleRetVos);
            ReturnObject<PageInfo<VoObject>>returnObject=new ReturnObject<PageInfo<VoObject>>(pageInfo);
            return returnObject;
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject<PageInfo<VoObject>> selectCertainOnsale(Long shopId,Long id,Integer page,Integer pageSize){
        try {
            OnSalePoExample onSalePoExample = new OnSalePoExample();
            OnSalePoExample.Criteria criteria = onSalePoExample.createCriteria();
            criteria.andShopIdEqualTo(shopId);
            criteria.andProductIdEqualTo(id);
            List<Byte> types = Arrays.asList(NO_ACTIVITY, SECOND_KILL);
            criteria.andTypeIn(types);
            ReturnObject<PageInfo<VoObject>> returnObject = selectOnsaleByExampleWithPageInfo(onSalePoExample, page, pageSize);
            return returnObject;
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject<PageInfo<VoObject>> selectActivities(Long id,Byte state,Integer page,Integer pageSize){
        try {
            OnSalePoExample onSalePoExample=new OnSalePoExample();
            OnSalePoExample.Criteria criteria=onSalePoExample.createCriteria();
            criteria.andActivityIdEqualTo(id);
            List<Byte>types=Arrays.asList(GROUPON,ADVANCE_SALE);
            criteria.andTypeIn(types);
            if(state!=null){
                criteria.andStateEqualTo(state);
            }
            ReturnObject<PageInfo<VoObject>> returnObject = selectOnsaleByExampleWithPageInfo(onSalePoExample, page, pageSize);
            return returnObject;
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject<PageInfo<VoObject>> selectShareActivities(Long id,Byte state,Integer page,Integer pageSize){
        try {
            OnSalePoExample onSalePoExample=new OnSalePoExample();
            OnSalePoExample.Criteria criteria=onSalePoExample.createCriteria();
            criteria.andShareActIdEqualTo(id);
            if(state!=null){
                criteria.andStateEqualTo(state);
            }
            ReturnObject<PageInfo<VoObject>> returnObject = selectOnsaleByExampleWithPageInfo(onSalePoExample, page, pageSize);
            return returnObject;
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject<PageInfo<VoObject>> selectAnyOnsale(Long id,Integer page,Integer pageSize){
        try {
            OnSalePoExample onSalePoExample=new OnSalePoExample();
            OnSalePoExample.Criteria criteria=onSalePoExample.createCriteria();
            criteria.andProductIdEqualTo(id);
            ReturnObject<PageInfo<VoObject>> returnObject = selectOnsaleByExampleWithPageInfo(onSalePoExample, page, pageSize);
            return returnObject;
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

}
