package cn.edu.xmu.oomall.activity.dao;

import cn.edu.xmu.oomall.activity.mapper.AdvanceSalePoMapper;
import cn.edu.xmu.oomall.activity.model.bo.AdvanceSale;
import cn.edu.xmu.oomall.activity.model.po.AdvanceSalePo;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.edu.xmu.oomall.activity.model.bo.AdvanceSaleStates;
import cn.edu.xmu.oomall.activity.model.po.AdvanceSalePoExample;
import cn.edu.xmu.oomall.activity.model.vo.SimpleAdvanceSaleRetVo;
import cn.edu.xmu.oomall.core.util.JacksonUtil;
import java.util.List;
/**
 * @author GXC 22920192204194
 */
@Repository
@Slf4j
public class AdvanceSaleDao {

    @Autowired
    AdvanceSalePoMapper advanceSalePoMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${oomall.activity.advancesale.expiretime}")
    private long categoryTimeout;

    private static Logger logger = LoggerFactory.getLogger(Common.class);
    /**
     * 根据id查找预售活动
     * @param id
     * @return
     */
    public ReturnObject selectAdvanceSaleByKey(Long id){
        ReturnObject returnObject=null;
        try{
            AdvanceSalePo po=null;
            AdvanceSale bo=(AdvanceSale) redisUtil.get("AdvanceSaleId"+id);
            if(bo!=null) {
                po=(AdvanceSalePo) Common.cloneVo(bo,AdvanceSalePo.class);
            }
            else {
                po=advanceSalePoMapper.selectByPrimaryKey(id);
            }
            returnObject=new ReturnObject(po);
        }catch(Exception e){
            logger.error(e.toString());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
        return returnObject;
    }
    /**
     * 更新预售活动
     * @param po
     * @return
     */
    public ReturnObject updateAdvanceSale(AdvanceSalePo po){
        ReturnObject returnObject=null;
        try{
            redisUtil.del("AdvanceSaleId"+po.getId());
            advanceSalePoMapper.updateByPrimaryKeySelective(po);
            returnObject=new ReturnObject();
        }catch(Exception e){
            logger.error(e.toString());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
        return returnObject;
    }
    /**
     * 物理删除预售活动
     * @param id
     * @return
     */
    public ReturnObject deleteAdvanceSale(Long id){
        ReturnObject returnObject=null;
        try{
            redisUtil.del("AdvanceSaleId"+id);
            advanceSalePoMapper.deleteByPrimaryKey(id);
            returnObject=new ReturnObject();
        }catch(Exception e){
            logger.error(e.toString());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
        return returnObject;
    }

    /**
     * 1-10
     */

    /**
     * 查询所有预售活动
     * 此方法复用：查询所有上线的预售活动，管理员查询特定商铺所有预售活动都可用此方法
     * 查询所有上线的预售活动使用此方法时传入的state为上线
     * @param shopId
     * @param activityIdList
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject getAllAdvanceSale(Long shopId, Byte state, List<Long> activityIdList, Integer page, Integer pageSize){
        AdvanceSalePoExample example = new AdvanceSalePoExample();
        AdvanceSalePoExample.Criteria criteria = example.createCriteria();
        if(shopId!=null) {
            criteria.andShopIdEqualTo(shopId);
        }
        if(state!=null)
        {
            criteria.andStateEqualTo(state);
        }
        if(activityIdList!=null&&!activityIdList.isEmpty())
        {
            criteria.andIdIn(activityIdList);
        }
        try {
            PageHelper.startPage(page, pageSize);
            List<AdvanceSalePo> poList = advanceSalePoMapper.selectByExample(example);
            if(poList.size()==0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "没有满足条件的预售活动");
            }
            PageInfo<AdvanceSalePo> pageInfo = new PageInfo<>(poList);
            ReturnObject returnObject = new ReturnObject(pageInfo);
            return Common.getPageRetVo(returnObject, SimpleAdvanceSaleRetVo.class);

        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    public ReturnObject getOnlineAdvanceSaleInfo(Long id){
        String key = "advanceSale_" + id;
        try {
            //先查redis
            AdvanceSale advanceSale = (AdvanceSale) redisUtil.get(key);
            if(advanceSale!=null) {
                if(!advanceSale.getState().equals(AdvanceSaleStates.ONLINE.getCode())) {
                    return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"该预售活动没有上线");
                }
                return new ReturnObject(advanceSale);
            }
            AdvanceSalePo po = advanceSalePoMapper.selectByPrimaryKey(id);
            if(po==null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"活动不存在");
            }
            if(po.getState().equals(AdvanceSaleStates.ONLINE.getCode())) {
                AdvanceSale advanceSaleBo = (AdvanceSale) Common.cloneVo(po, AdvanceSale.class);
                redisUtil.set(key, JacksonUtil.toJson(advanceSaleBo), categoryTimeout);
                return new ReturnObject(advanceSaleBo);
            }
            else {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"该预售活动没有上线");
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 管理员查询商铺的特定预售活动
     * @param shopId
     * @param activityId
     * @return
     */
    public  ReturnObject getShopAdvanceSale(Long shopId,Long activityId){
        try {
            AdvanceSalePoExample example = new AdvanceSalePoExample();
            AdvanceSalePoExample.Criteria criteria = example.createCriteria();
            criteria.andShopIdEqualTo(shopId);
            criteria.andIdEqualTo(activityId);
            List<AdvanceSalePo> list = advanceSalePoMapper.selectByExample(example);
            if(list.size()==0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"没有满足条件的预售活动");
            }
            //根据预售活动id去查询，因为是一对一的，所以List里只有一个advanceSalePo
            AdvanceSalePo advanceSalePo=list.get(0);
            AdvanceSale advanceSaleBo = (AdvanceSale) Common.cloneVo(advanceSalePo, AdvanceSale.class);
            return new ReturnObject(advanceSaleBo);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 管理员新增预售
     * @param advanceSaleBo
     * @return
     */
    public ReturnObject addAdvanceSale(Long adminId,String adminName,AdvanceSale advanceSaleBo){
        AdvanceSalePo advanceSalePo = (AdvanceSalePo) Common.cloneVo(advanceSaleBo, AdvanceSalePo.class);
        Common.setPoCreatedFields(advanceSalePo,adminId,adminName);
        Common.setPoModifiedFields(advanceSalePo,adminId,adminName);
        try {
            if (advanceSalePoMapper.insert(advanceSalePo) == 1) {
                return new ReturnObject(Common.cloneVo(advanceSalePo, AdvanceSale.class));
            }
            else {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID, "新增预售活动失败");
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }





}
