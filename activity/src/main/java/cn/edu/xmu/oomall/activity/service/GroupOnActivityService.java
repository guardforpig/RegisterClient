package cn.edu.xmu.oomall.activity.service;


import cn.edu.xmu.oomall.activity.constant.GroupOnState;
import cn.edu.xmu.oomall.activity.dao.GroupActivityDao;
import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleSaleInfoVo;
import cn.edu.xmu.oomall.activity.model.bo.GroupOnActivity;
import cn.edu.xmu.oomall.activity.model.vo.*;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author jiyuan lin
 * @date 2021/11/14
 */
@Service
public class GroupOnActivityService {

    private Logger logger = LoggerFactory.getLogger(GroupOnActivityService.class);

    @Autowired
    private GroupActivityDao groupActivityDao;

    @Autowired
    private GoodsService goodsService;




    /** 删除商品
     * @param id 商品id
     * @return 删除是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject delGroupon(long id) {
        ReturnObject<GroupOnActivity> groupOnActivity= groupActivityDao.getGroupOnActivity(id);
        if(!groupOnActivity.getCode().equals(ReturnNo.OK))
        {
            return groupOnActivity;
        }
        if(!groupOnActivity.getData().getState().equals(GroupOnState.DRAFT)) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }

        ReturnObject obj = groupActivityDao.deleteGroupon(id);
        if(!obj.getCode().equals(ReturnNo.OK)) {
            return obj;
        }
        ReturnObject result = goodsService.deleteOnsale(id);
        if(!result.getCode().equals(ReturnNo.OK)){
            return result;
        }

        return obj;
    }


    /**
     * 修改团购信息
     * @param id 修改的团购对象id
     * @param groupOnActivityVo 修改商品信息
     * @return 修改后是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject modifyGroupon(long id, GroupOnActivityVo groupOnActivityVo, long shopId,long loginUser,String loginUsername) {
        GroupOnActivity groupOnActivity = (GroupOnActivity) Common.cloneVo(groupOnActivityVo,GroupOnActivity.class);
        groupOnActivity.setId(id);
        groupOnActivity.setShopId(shopId);
        Common.setPoModifiedFields(groupOnActivity,loginUser,loginUsername);


        ReturnObject<GroupOnActivity> findObj = groupActivityDao.getGroupOnActivity(id);
        if(!findObj.getCode().equals(ReturnNo.OK))
        {
            return findObj;
        }
        if(findObj.getData()==null)
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if(!findObj.getData().getState().equals(GroupOnState.DRAFT))
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }

        ReturnObject obj = groupActivityDao.modifyGroupOnActivity(groupOnActivity);
        if(!obj.getCode().equals(ReturnNo.OK))
        {
            return obj;
        }
        LocalDateTime beginTime;
        LocalDateTime endTime;
        OnsaleModifyVo onsaleModifyVo = new OnsaleModifyVo();
        if(groupOnActivity.getBeginTime()!=null) {
            beginTime = groupOnActivity.getBeginTime();
            onsaleModifyVo.setBegintime(beginTime);
        }
        if(groupOnActivity.getEndTime()!=null) {
            endTime = groupOnActivity.getEndTime();
            onsaleModifyVo.setEndtime(endTime);
        }
        ReturnObject<PageVo<OnsaleVo>> retObj = goodsService.getOnsale(groupOnActivity.getId(),1,1,10);
        if(retObj.getCode().equals(ReturnNo.OK)&&retObj.getData().getTotal()>0){
            long onSaleId;
            for(var onSaleObj:retObj.getData().getList())
            {
                onSaleId = onSaleObj.getId();
                ReturnObject result=goodsService.modifyOnsale(onSaleId,onsaleModifyVo);
                if(!result.getCode().equals(ReturnNo.OK))
                {
                    return result;
                }
            }

        }else if(!retObj.getCode().equals(ReturnNo.OK)){
            return retObj;
        }
        ReturnObject result = goodsService.modifyOnsale(groupOnActivity.getId(),onsaleModifyVo);
        if(!result.getCode().equals(ReturnNo.OK)){
            return result;
        }

        return obj;
    }

    /**
     * 上线团购活动
     * @param id 修改的团购对象id
     * @param shopId 商铺id
     * @return 修改后是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject onlineGroupOnActivity(long id, long shopId,long loginUser,String loginUsername) {

        ReturnObject<GroupOnActivity> findObj = groupActivityDao.getGroupOnActivity(id);
        if(!findObj.getCode().equals(ReturnNo.OK))
        {
            return findObj;
        }
        if(findObj.getData()==null)
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if(findObj.getData().getState().equals(GroupOnState.ONLINE))
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }

        GroupOnActivity groupOnActivity = new GroupOnActivity();
        Common.setPoModifiedFields(groupOnActivity,loginUser,loginUsername);
        groupOnActivity.setId(id);
        groupOnActivity.setState(GroupOnState.ONLINE);

        ReturnObject obj = groupActivityDao.modifyGroupOnActivity(groupOnActivity);
        if(!obj.getCode().equals(ReturnNo.OK)) {
            return obj;
        }

        ReturnObject result = goodsService.onlineOnsale(id,shopId);
        if(!result.getCode().equals(ReturnNo.OK)){
            return result;
        }
        return obj;
    }

    /**
     * 下线团购活动
     * @param id 修改的团购对象id
     * @param shopId 商铺id
     * @return 修改后是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject offlineGroupOnActivity(long id, long shopId,long loginUser,String loginUsername) {
        ReturnObject<GroupOnActivity> findObj = groupActivityDao.getGroupOnActivity(id);
        if(!findObj.getCode().equals(ReturnNo.OK))
        {
            return findObj;
        }
        if(findObj.getData()==null)
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if(findObj.getData().getState().equals(GroupOnState.OFFLINE))
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }

        GroupOnActivity groupOnActivity = new GroupOnActivity();
        Common.setPoModifiedFields(groupOnActivity,loginUser,loginUsername);
        groupOnActivity.setId(id);
        groupOnActivity.setState(GroupOnState.OFFLINE);

        ReturnObject obj = groupActivityDao.modifyGroupOnActivity(groupOnActivity);
        if(!obj.getCode().equals(ReturnNo.OK)) {
            return obj;
        }

        ReturnObject result = goodsService.offlineOnsale(id,shopId);
        if(!result.getCode().equals(ReturnNo.OK)) {
            return result;
        }
        return obj;
    }


    /**
     * 增加参与团购的商品
     * @param id 修改的团购对象id
     * @param shopId 店铺id
     * @return 修改后是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject addOnsaleToGroupOn(long shopId, long pid, long id,long loginUser,String loginUsername)
    {
        ReturnObject<GroupOnActivity> obj = groupActivityDao.getGroupOnActivity(id);
        if(!obj.getCode().equals(ReturnNo.OK))
        {
            return obj;
        }
        if(obj.getData()==null)
        {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if(!obj.getData().getState().equals(GroupOnState.DRAFT)) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }


        SimpleSaleInfoVo simpleOnSaleInfoVo = new SimpleSaleInfoVo();
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDateTime beginTime = obj.getData().getBeginTime();
        LocalDateTime endTime = obj.getData().getEndTime();
        if(beginTime.isAfter(nowTime)) {
            beginTime = nowTime;
        }
        simpleOnSaleInfoVo.setEndTime(endTime);
        simpleOnSaleInfoVo.setBeginTime(beginTime);
        simpleOnSaleInfoVo.setActivityId(id);
        ReturnObject result = goodsService.addOnsale(shopId,pid,simpleOnSaleInfoVo);
        if(!result.getCode().equals(ReturnNo.OK)) {
            return result;
        }
        return obj;
    }
}
