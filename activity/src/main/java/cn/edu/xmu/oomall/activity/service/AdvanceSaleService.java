package cn.edu.xmu.oomall.activity.service;

import cn.edu.xmu.oomall.activity.dao.AdvanceSaleDao;
import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.model.bo.AdvanceSale;
import cn.edu.xmu.oomall.activity.model.po.AdvanceSalePo;
import cn.edu.xmu.oomall.activity.model.vo.*;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.edu.xmu.oomall.activity.microservice.ShopService;
import cn.edu.xmu.oomall.activity.model.bo.AdvanceSaleStates;
import cn.edu.xmu.oomall.activity.microservice.vo.*;
import com.github.pagehelper.PageInfo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GXC 22920192204194
 */
@Service
public class AdvanceSaleService {
    @Autowired
    AdvanceSaleDao advanceSaleDao;
    @Autowired
    GoodsService goodsService;
    @Autowired
    ShopService shopService;
    /**
     * 商铺管理员上线预售活动
     * @param shopId 商铺id
     * @param advancesaleId 预售活动id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject onlineAdvancesale(Long adminId, String adminName,Long shopId, Long advancesaleId) {
        ReturnObject returnObject=null;
        AdvanceSalePo po=null;
        po=(AdvanceSalePo) advanceSaleDao.selectAdvanceSaleByKey(advancesaleId).getData();
        if(po==null){
            returnObject=new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "目标预售活动不存在");
        }else if(!po.getShopId().equals(shopId)){
            returnObject=new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"管理员和预售活动不属于同一个商铺，无权限");
        }else{
            if(po.getState()==(byte)1){
                returnObject=new ReturnObject(ReturnNo.STATENOTALLOW,"当前状态禁止此操作");
            }else{
                po.setState((byte) 1);
                Common.setPoModifiedFields(po,adminId,adminName);
                advanceSaleDao.updateAdvanceSale(po);
                SimpleReturnObject retObject=goodsService.onlineOnsale(shopId,advancesaleId);
                //抛出异常是为了回滚
                if(retObject.getErrno()!=0){
                    returnObject=new ReturnObject(ReturnNo.getByCode(retObject.getErrno()),retObject.getErrmsg());
                }else{
                    returnObject=new ReturnObject();
                }
            }
        }
        return returnObject;
    }

    /**
     * 商铺管理员下线预售活动
     * @param shopId 商铺id
     * @param advancesaleId 预售活动id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject offlineAdvancesale(Long adminId,String adminName,Long shopId, Long advancesaleId)  {
        ReturnObject returnObject=null;
        AdvanceSalePo po=null;
        po=(AdvanceSalePo) advanceSaleDao.selectAdvanceSaleByKey(advancesaleId).getData();
        if(po==null){
            returnObject=new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "目标预售活动不存在");
        }else if(!po.getShopId().equals(shopId)){
            returnObject=new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"管理员和预售活动不属于同一个商铺，无权限");
        }else{
            if(po.getState()!=1){
                returnObject=new ReturnObject(ReturnNo.STATENOTALLOW,"当前状态禁止此操作");
            }else{
                po.setState((byte) 2);
                Common.setPoModifiedFields(po,adminId,adminName);
                advanceSaleDao.updateAdvanceSale(po);
                SimpleReturnObject retObject=goodsService.offlineOnsale(shopId,advancesaleId);
                if(retObject.getErrno()!=0){
                    returnObject=new ReturnObject(ReturnNo.getByCode(retObject.getErrno()),retObject.getErrmsg());
                }else{
                    returnObject=new ReturnObject();
                }
            }
        }
        return returnObject;
    }

    /**
     * @param adminId
     * @param shopId
     * @param advancesaleId
     * @param advanceSaleModifyVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject modifyAdvancesale(Long adminId, Long shopId, String adminName,Long advancesaleId, AdvanceSaleModifyVo advanceSaleModifyVo) {
        ReturnObject returnObject=null;
        AdvanceSalePo po=(AdvanceSalePo) advanceSaleDao.selectAdvanceSaleByKey(advancesaleId).getData();
        if(po!=null){
            if(po.getShopId().equals(shopId)){
                if(po.getState()==0){
                    po=(AdvanceSalePo) Common.cloneVo(advanceSaleModifyVo,po.getClass());
                    Common.setPoModifiedFields(po,adminId,adminName);
                    advanceSaleDao.updateAdvanceSale(po);

                    //调用内部API，查onsale信息
                    SimpleReturnObject<PageVo<OnsaleVo>> retObj = goodsService.getOnsale(shopId,advancesaleId,1,1,10);
                    Long onsaleId=null;
                    //确定有需要修改的onsale目标
                    if(retObj.getErrno()==0&&retObj.getData().getTotal()>0){
                        onsaleId=retObj.getData().getList().get(0).getId();
                        OnsaleModifyVo onsaleModifyVo=(OnsaleModifyVo)Common.cloneVo(advanceSaleModifyVo,OnsaleModifyVo.class);
                        SimpleReturnObject result=goodsService.modifyOnsale(shopId,onsaleId,onsaleModifyVo);
                        if(result.getErrno()!=0){
                            returnObject=new ReturnObject(ReturnNo.getByCode(result.getErrno()),result.getErrmsg());
                        }else{
                            returnObject=new ReturnObject();
                        }
                    }else if(retObj.getErrno()!=0){
                        //查询就出错了
                        returnObject=new ReturnObject(ReturnNo.getByCode(retObj.getErrno()),retObj.getErrmsg());
                    }else{
                        //查询无结果
                        returnObject=new ReturnObject();
                    }
                }else{
                    returnObject=new ReturnObject(ReturnNo.STATENOTALLOW,"当前状态禁止此操作");
                }
            }else{
                returnObject=new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"管理员和预售活动不属于同一个商铺，无权限");
            }
        }else{
            returnObject=new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"该预售活动不存在");
        }
        return returnObject;
    }

    /**
     *
     * @param adminId
     * @param shopId
     * @param advancesaleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteAdvancesale(Long adminId, Long shopId, Long advancesaleId) {
        ReturnObject returnObject=null;
        AdvanceSalePo po=(AdvanceSalePo) advanceSaleDao.selectAdvanceSaleByKey(advancesaleId).getData();
        if(po!=null){
            if(po.getShopId().equals(shopId)){
                if(po.getState()==0){
                    advanceSaleDao.deleteAdvanceSale(advancesaleId);
                    //内部API物理删除onsale
                    SimpleReturnObject retObj=goodsService.deleteOnsale(shopId,advancesaleId);
                    //预售活动草稿态，那么onsale不是草稿态就是系统的问题，失败只有一种可能就是onsale服务没有运行
                    if(retObj.getErrno()!=0){
                        returnObject=new ReturnObject(ReturnNo.getByCode(retObj.getErrno()),retObj.getErrmsg());
                    }else{
                        returnObject=new ReturnObject();
                    }
                }else{
                    returnObject=new ReturnObject(ReturnNo.STATENOTALLOW,"当前状态禁止此操作");
                }
            }else{
                returnObject=new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"管理员和预售活动不属于同一个商铺，无权限");
            }
        }else{
            returnObject=new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"该预售活动不存在");
        }
        return returnObject;
    }

    /**
     * 获得预售活动的所有状态
     * @return
     */
    public ReturnObject getAdvanceSaleState() {
        List<RetStatesVo> list = new ArrayList<>();
        for (AdvanceSaleStates value : AdvanceSaleStates.values()) {
            RetStatesVo retStatesVO = new RetStatesVo(value.getCode(), value.getValue());
            list.add(retStatesVO);
        }
        return new ReturnObject<>(list);
    }

    /**
     * 根据shopId,productId,state,beginTime,endTime查询所有预售活动
     * @param shopId
     * @param productId
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject getAllAdvanceSale(Long shopId, Long productId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        //判断shop是否存在
        if (shopId != null) {
            ReturnObject<ShopInfoVo> shopVoReturnObject= shopService.getShop(shopId);
            if (shopVoReturnObject.getData() == null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "不存在该商铺");
            }
        }
        //获取OnSaleList所有的activityId
        List<Long> activityIdList=new ArrayList<>();
        if(productId!=null) {
            //跨模块调接口，根据shopId,productId，beginTime，endTime获取OnSale列表
            ReturnObject<PageInfo<SimpleOnSaleInfoVo>> onSaleList1 = goodsService.getAllOnsale(shopId, productId, beginTime, endTime, 1, 1);
            if (onSaleList1.getData().getList() != null) {
                long total = onSaleList1.getData().getTotal();
                ReturnObject<PageInfo<SimpleOnSaleInfoVo>> onSaleList2 = goodsService.getAllOnsale(shopId, productId, beginTime, endTime, 1, (int) total);
                activityIdList=onSaleList2.getData().getList().stream().map(SimpleOnSaleInfoVo::getActivityId).collect(Collectors.toList());
            }
        }
        ReturnObject pageInfoReturnObject = advanceSaleDao.getAllAdvanceSale(shopId, state, activityIdList, page, pageSize);
        return pageInfoReturnObject;
    }

    /**
     * 查询上线预售活动的详细信息
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getOnlineAdvanceSaleInfo(Long id) {
        OnSaleInfoVo onSaleInfoVo=new OnSaleInfoVo();
        //跨模块调接口，根据预售活动id获得相应Onsale
        ReturnObject<PageInfo<OnSaleInfoVo>> pageInfoReturnObject = goodsService.getShopOnsaleInfo(4L,id,null,null,null,1,10);
        if(pageInfoReturnObject.getData().getList()!=null){
            onSaleInfoVo=pageInfoReturnObject.getData().getList().get(0);
        }
        else {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "活动不存在");
        }
        ReturnObject returnObject = advanceSaleDao.getOnlineAdvanceSaleInfo(id);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        AdvanceSale advanceSaleBo = (AdvanceSale) returnObject.getData();
        AdvanceSaleRetVo advanceSaleDetailsRetVo = (AdvanceSaleRetVo) Common.cloneVo(onSaleInfoVo, AdvanceSaleRetVo.class);
        advanceSaleDetailsRetVo.setName(advanceSaleBo.getName());
        advanceSaleDetailsRetVo.setPayTime(advanceSaleBo.getPayTime());
        advanceSaleDetailsRetVo.setAdvancePayPrice(advanceSaleBo.getAdvancePayPrice());
        advanceSaleDetailsRetVo.setState(advanceSaleBo.getState());
        return new ReturnObject(advanceSaleDetailsRetVo);
    }

    /**
     * 管理员新增预售
     * @param loginUserId
     * @param loginUerName
     * @param shopId
     * @param id
     * @param advanceSaleVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject addAdvanceSale(Long loginUserId, String loginUerName, Long shopId, Long id, AdvanceSaleVo advanceSaleVo) {

        AdvanceSale advanceSaleBo = (AdvanceSale) Common.cloneVo(advanceSaleVo, AdvanceSale.class);
        advanceSaleBo.setState(AdvanceSaleStates.DRAFT.getCode());

        //判断商铺是否存在
        ReturnObject<ShopInfoVo> shopVoReturnObject= shopService.getShop(shopId);
        if (shopVoReturnObject.getData() == null) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "不存在该商铺");
        }
        advanceSaleBo.setShop(new ShopVo(shopId,shopVoReturnObject.getData().getName()));

        //调用goodsservice，根据shopId,productId，beginTime，endTime获取OnSale列表,判断要加入的活动的时间是否和已有product的预售活动时间冲突
        ReturnObject<PageInfo<SimpleOnSaleInfoVo>> onSaleList1=goodsService.getAllOnsale(shopId,id,advanceSaleVo.getBeginTime(),advanceSaleVo.getEndTime(),1,1);
        List<SimpleOnSaleInfoVo> list=new ArrayList<>();
        if(onSaleList1.getData().getList()!=null) {
            long total = onSaleList1.getData().getTotal();
            ReturnObject<PageInfo<SimpleOnSaleInfoVo>> onSaleList2 = goodsService.getAllOnsale(shopId, id, advanceSaleVo.getBeginTime(), advanceSaleVo.getEndTime(), 1, (int) total);
            list=onSaleList2.getData().getList();
        }
        if (advanceSaleVo.getBeginTime() != null && advanceSaleVo.getEndTime() != null) {
            for (SimpleOnSaleInfoVo vo : list) {
                //若活动的结束时间早于Onsale的开始时间，或者活动的开始时间晚于Onsale的结束时间，则时间不冲突。否则时间冲突。
                if (!(advanceSaleVo.getEndTime().isBefore(vo.getBeginTime()) || advanceSaleVo.getBeginTime().isAfter(vo.getEndTime()))) {
                    return new ReturnObject<>(ReturnNo.GOODS_PRICE_CONFLICT,"商品销售时间冲突");
                }
            }
        }

        //新增记录到OnSale表
        ReturnObject addOnsaleReturnObject=addOnsale(shopId,id, advanceSaleVo);
        if(addOnsaleReturnObject.getData()==null){
            return addOnsaleReturnObject;
        }

        //新增记录到AdvanceSale表
        ReturnObject returnObject = advanceSaleDao.addAdvanceSale(loginUserId, loginUerName, advanceSaleBo);
        if(returnObject.getData()==null){
            return returnObject;
        }
        SimpleAdvanceSaleRetVo simpleAdvanceSaleRetVo = (SimpleAdvanceSaleRetVo) Common.cloneVo(returnObject.getData(), SimpleAdvanceSaleRetVo.class);
        return new ReturnObject<>(simpleAdvanceSaleRetVo);
    }

    /**
     * 管理员查询商铺的特定预售活动
     * @param shopId
     * @param id
     * @return
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject getShopAdvanceSaleInfo(Long shopId, Long id) {
        ReturnObject<ShopInfoVo> shopVoReturnObject= shopService.getShop(shopId);
        if (shopVoReturnObject.getData() == null) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "不存在该商铺");
        }
        OnSaleInfoVo onSaleInfoVo = (OnSaleInfoVo) getOnSaleInfo(shopId,id).getData();
        if (onSaleInfoVo == null) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "找不到对应的销售信息");
        }
        ReturnObject returnObject = advanceSaleDao.getShopAdvanceSale(shopId, id);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        AdvanceSale advanceSaleBo = (AdvanceSale) returnObject.getData();
        AdvanceSaleRetVo advanceSaleDetailsRetVo = (AdvanceSaleRetVo) Common.cloneVo(onSaleInfoVo, AdvanceSaleRetVo.class);
        advanceSaleDetailsRetVo.setName(advanceSaleBo.getName());
        advanceSaleDetailsRetVo.setPayTime(advanceSaleBo.getPayTime());
        advanceSaleDetailsRetVo.setAdvancePayPrice(advanceSaleBo.getAdvancePayPrice());
        advanceSaleDetailsRetVo.setState(advanceSaleBo.getState());
        return new ReturnObject(advanceSaleDetailsRetVo);
    }

    /**
     * 调用goodsservice
     * 根据商铺号shopId和活动id找onsale
     * @param shopId
     * @param id
     * @return
     */
    public ReturnObject getOnSaleInfo(Long shopId,Long id) {
        SimpleOnSaleInfoVo simpleOnSaleInfoVo = new SimpleOnSaleInfoVo();
        //AdvanceSale与OnSale是一对一关系，所以根据预售活动id只会查到一个OnSale
        ReturnObject<PageInfo<SimpleOnSaleInfoVo>> pageInfoReturnObject=goodsService.getShopOnsaleInfo(shopId,id,null,null,null,1,10);
        if (pageInfoReturnObject.getData().getList() != null) {
            simpleOnSaleInfoVo = pageInfoReturnObject.getData().getList().get(0);
        }
        else{
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "活动不存在");
        }
        ReturnObject<OnSaleInfoVo> returnObject=goodsService.getOnSaleInfo(simpleOnSaleInfoVo.getId());
        return returnObject;
    }

    /**
     * 调用goodsservice新增onsale
     * @param shopId
     * @param productId
     * @param advanceSaleVo
     * @return
     */
    public ReturnObject<SimpleOnSaleInfoVo> addOnsale(Long shopId,Long productId, AdvanceSaleVo advanceSaleVo) {
        SimpleSaleInfoVo simpleSaleInfoVo = (SimpleSaleInfoVo) Common.cloneVo(advanceSaleVo, OnSaleCreatedVo.class);
        //设置Onsale的type为3,表示预售类型
        simpleSaleInfoVo.setType(Byte.valueOf("3"));
        return goodsService.addOnsale(shopId,productId,simpleSaleInfoVo);
    }
}
