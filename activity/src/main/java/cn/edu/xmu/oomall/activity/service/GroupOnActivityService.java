package cn.edu.xmu.oomall.activity.service;


import cn.edu.xmu.oomall.activity.constant.GroupOnState;
import cn.edu.xmu.oomall.activity.dao.GroupActivityDao;
import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.microservice.vo.OnSaleCreatedVo;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleSaleInfoVo;
import cn.edu.xmu.oomall.activity.model.bo.GroupOnActivity;
import cn.edu.xmu.oomall.activity.model.vo.*;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
/**
 * @author Lin Jiyuan
 * @sn 30320192200032
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
    public ReturnObject delGroupon(long shopId,long id) {
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
        InternalReturnObject result = goodsService.deleteOnsale(shopId,id);
        if(result.getErrno()!=0){
            obj=new ReturnObject(ReturnNo.getByCode(result.getErrno()),result.getErrmsg());
        }else{
            obj=new ReturnObject();
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
        GroupOnActivity groupOnActivity = (GroupOnActivity) cloneVo(groupOnActivityVo,GroupOnActivity.class);
        groupOnActivity.setId(id);
        groupOnActivity.setShopId(shopId);
       setPoModifiedFields(groupOnActivity,loginUser,loginUsername);


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
        InternalReturnObject<PageVo<OnsaleVo>> retObj = goodsService.getShopOnSaleInfo(shopId,groupOnActivity.getId(),(byte)1,null,null,1,10);
        if(retObj.getErrno()==0&&retObj.getData().getTotal()>0){
            long onSaleId;
            for(var onSaleObj:retObj.getData().getList())
            {
                onSaleId = onSaleObj.getId();
                InternalReturnObject result=goodsService.modifyOnsale(shopId,onSaleId,onsaleModifyVo);
                if(result.getErrno()!=0){
                    obj=new ReturnObject(ReturnNo.getByCode(result.getErrno()),result.getErrmsg());
                }else{
                    obj=new ReturnObject();
                }
            }

        }else if(retObj.getErrno()!=0){
            obj=new ReturnObject(ReturnNo.getByCode(retObj.getErrno()),retObj.getErrmsg());
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
       setPoModifiedFields(groupOnActivity,loginUser,loginUsername);
        groupOnActivity.setId(id);
        groupOnActivity.setState(GroupOnState.ONLINE);

        ReturnObject obj = groupActivityDao.modifyGroupOnActivity(groupOnActivity);
        if(!obj.getCode().equals(ReturnNo.OK)) {
            return obj;
        }

        InternalReturnObject result = goodsService.onlineOnsale(id,shopId);
        if(result.getErrno()!=0){
            obj=new ReturnObject(ReturnNo.getByCode(result.getErrno()),result.getErrmsg());
        }else{
            obj=new ReturnObject();
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
       setPoModifiedFields(groupOnActivity,loginUser,loginUsername);
        groupOnActivity.setId(id);
        groupOnActivity.setState(GroupOnState.OFFLINE);

        ReturnObject obj = groupActivityDao.modifyGroupOnActivity(groupOnActivity);
        if(!obj.getCode().equals(ReturnNo.OK)) {
            return obj;
        }

        InternalReturnObject result = goodsService.offlineOnsale(id,shopId);
        if(result.getErrno()!=0){
            obj=new ReturnObject(ReturnNo.getByCode(result.getErrno()),result.getErrmsg());
        }else{
            obj=new ReturnObject();
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


        OnSaleCreatedVo simpleOnSaleInfoVo = new OnSaleCreatedVo();
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDateTime beginTime = obj.getData().getBeginTime();
        LocalDateTime endTime = obj.getData().getEndTime();
        if(beginTime.isAfter(nowTime)) {
            beginTime = nowTime;
        }
        simpleOnSaleInfoVo.setEndTime(endTime);
        simpleOnSaleInfoVo.setBeginTime(beginTime);
        simpleOnSaleInfoVo.setActivityId(id);
        InternalReturnObject result = goodsService.addOnSale(shopId,pid,simpleOnSaleInfoVo);
        if(result.getErrno()!=0){
            obj=new ReturnObject(ReturnNo.getByCode(result.getErrno()),result.getErrmsg());
        }else{
            obj=new ReturnObject();
        }
        return obj;
    }
}
