package cn.edu.xmu.oomall.activity.service;

import cn.edu.xmu.oomall.activity.constant.TimeFormat;
import cn.edu.xmu.oomall.activity.dao.AdvanceSaleDao;
import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.model.bo.AdvanceSale;
import cn.edu.xmu.oomall.activity.model.po.AdvanceSalePo;
import cn.edu.xmu.oomall.activity.model.vo.*;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.edu.xmu.oomall.activity.microservice.ShopService;
import cn.edu.xmu.oomall.activity.model.bo.AdvanceSaleState;
import cn.edu.xmu.oomall.activity.microservice.vo.*;
import com.github.pagehelper.PageInfo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
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
        ReturnObject r1=advanceSaleDao.selectAdvanceSaleByKey(advancesaleId);
        if(r1.getCode().equals(ReturnNo.INTERNAL_SERVER_ERR)){
            return r1;
        }
        po=(AdvanceSalePo) r1.getData();
        if(po==null){
            returnObject=new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "目标预售活动不存在");
        }else if(!po.getShopId().equals(shopId)){
            returnObject=new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"管理员和预售活动不属于同一个商铺，无权限");
        }else{
            if(po.getState()==(byte)1){
                returnObject=new ReturnObject(ReturnNo.STATENOTALLOW,"当前状态禁止此操作");
            }else{
                po.setState((byte) 1);
               setPoModifiedFields(po,adminId,adminName);
                advanceSaleDao.updateAdvanceSale(po);
                InternalReturnObject retObject=goodsService.onlineOnsale(shopId,advancesaleId);
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
        ReturnObject r1=advanceSaleDao.selectAdvanceSaleByKey(advancesaleId);
        if(r1.getCode().equals(ReturnNo.INTERNAL_SERVER_ERR)){
            return r1;
        }
        po=(AdvanceSalePo) r1.getData();
        if(po==null){
            returnObject=new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "目标预售活动不存在");
        }else if(!po.getShopId().equals(shopId)){
            returnObject=new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"管理员和预售活动不属于同一个商铺，无权限");
        }else{
            if(po.getState()!=1){
                returnObject=new ReturnObject(ReturnNo.STATENOTALLOW,"当前状态禁止此操作");
            }else{
                po.setState((byte) 2);
               setPoModifiedFields(po,adminId,adminName);
                advanceSaleDao.updateAdvanceSale(po);
                InternalReturnObject retObject=goodsService.offlineOnsale(shopId,advancesaleId);
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
        AdvanceSalePo po=null;
        ReturnObject r1=advanceSaleDao.selectAdvanceSaleByKey(advancesaleId);
        if(r1.getCode().equals(ReturnNo.INTERNAL_SERVER_ERR)){
            return r1;
        }
        po=(AdvanceSalePo) r1.getData();
        if(po!=null){
            if(po.getShopId().equals(shopId)){
                if(po.getState()==0){
                    po=(AdvanceSalePo) cloneVo(advanceSaleModifyVo,po.getClass());
                   setPoModifiedFields(po,adminId,adminName);
                    advanceSaleDao.updateAdvanceSale(po);

                    //调用内部API，查onsale信息
                    InternalReturnObject<PageVo<OnsaleVo>> retObj = goodsService.getShopOnSaleInfo(shopId,advancesaleId,(byte)1,null,null,1,10);
                    Long onsaleId=null;
                    //确定有需要修改的onsale目标
                    if(retObj.getErrno()==0&&retObj.getData().getTotal()>0){
                        onsaleId=retObj.getData().getList().get(0).getId();
                        OnsaleModifyVo onsaleModifyVo=(OnsaleModifyVo)cloneVo(advanceSaleModifyVo,OnsaleModifyVo.class);
                        InternalReturnObject result=goodsService.modifyOnsale(shopId,onsaleId,onsaleModifyVo);
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
        AdvanceSalePo po=null;
        ReturnObject r1=advanceSaleDao.selectAdvanceSaleByKey(advancesaleId);
        if(r1.getCode().equals(ReturnNo.INTERNAL_SERVER_ERR)){
            return r1;
        }
        po=(AdvanceSalePo) r1.getData();
        if(po!=null){
            if(po.getShopId().equals(shopId)){
                if(po.getState()==0){
                    advanceSaleDao.deleteAdvanceSale(advancesaleId);
                    //内部API物理删除onsale
                    InternalReturnObject retObj=goodsService.deleteOnsale(shopId,advancesaleId);
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
     * @author Jiawei Zheng
     * @date 2021-11-26
     */


    /**
     * 获得预售活动的所有状态
     * @return
     */
    public ReturnObject getAdvanceSaleState() {
        List<RetStatesVo> list = new ArrayList<>();
        for (AdvanceSaleState value : AdvanceSaleState.values()) {
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
            InternalReturnObject<SimpleShopVo> shopVoReturnObject= shopService.getShopInfo(shopId);
            if (shopVoReturnObject.getData() == null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "不存在该商铺");
            }
        }
        //获取OnSaleList所有的activityId
        List<Long> activityIdList=new ArrayList<>();
        if(productId!=null) {
            //跨模块调接口，根据shopId,productId，beginTime，endTime获取OnSale列表
            InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> onSaleList1 = goodsService.getOnSales(shopId, productId, beginTime, endTime, 1, 1);
            if (onSaleList1.getData().getList() != null) {
                long total = onSaleList1.getData().getTotal();
                InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> onSaleList2 = goodsService.getOnSales(shopId, productId, beginTime, endTime, 1, (int) total);
                activityIdList=onSaleList2.getData().getList().stream().map(SimpleOnSaleInfoVo::getActivityId).collect(Collectors.toList());
            }
        }
        ReturnObject pageInfoReturnObject = advanceSaleDao.getAllAdvanceSale(shopId, state, activityIdList, page, pageSize);
        return pageInfoReturnObject;
    }

    /**
     * 根据条件查询某个上线预售活动的详细信息
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getAdvanceSaleInfo(Long id, AdvanceSaleState state) {
        //先查advanceSale表
        ReturnObject returnObject = advanceSaleDao.getAdvanceSaleInfo(state,id);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        AdvanceSale advanceSaleBo = (AdvanceSale) returnObject.getData();

        //需要先从advanceBo拿到shopId
        Long shopId = advanceSaleBo.getShopId();

        //根据商铺号shopId和预售活动id获得OnSale的信息
        InternalReturnObject internalReturnObject =  getOnSaleInfo(shopId,id);
        if (internalReturnObject.getData() == null) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, internalReturnObject.getErrmsg());
        }

        //这里因为返回的对象需要同时从OnSale表和AdvanceSale表拿数据，所以只能用一次cloneVo
        AdvanceSaleRetVo advanceSaleRetVo = (AdvanceSaleRetVo) cloneVo(advanceSaleBo, AdvanceSaleRetVo.class);

        //将OnSale的字段赋给retVo
        FullOnSaleVo fullOnSaleVo=(FullOnSaleVo) internalReturnObject.getData();
        advanceSaleRetVo.setShop(fullOnSaleVo.getShop());
        advanceSaleRetVo.setBeginTime(fullOnSaleVo.getBeginTime());
        advanceSaleRetVo.setEndTime(fullOnSaleVo.getEndTime());
        advanceSaleRetVo.setProduct(fullOnSaleVo.getProduct());
        advanceSaleRetVo.setPrice(fullOnSaleVo.getPrice());
        advanceSaleRetVo.setQuantity(fullOnSaleVo.getQuantity());
        return new ReturnObject(advanceSaleRetVo);
    }

    /**
     * 根据条件查询商铺某个任意状态的预售活动详细信息,返回的属性包括创建者和修改者的信息
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getShopAdvanceSaleInfo(Long id, Long shopId) {
        //先查advanceSale表
        ReturnObject returnObject = advanceSaleDao.getAdvanceSaleInfo(null,id);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        AdvanceSale advanceSaleBo = (AdvanceSale) returnObject.getData();

        //根据shopId判断商铺是否存在
        InternalReturnObject<SimpleShopVo> shopVoReturnObject= shopService.getShopInfo(shopId);
        if (shopVoReturnObject.getData() == null) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "不存在该商铺");
        }
        //根据商铺号shopId和预售活动id获得OnSale的信息
        InternalReturnObject internalReturnObject =  getOnSaleInfo(shopId,id);
        if (internalReturnObject.getData() == null) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, internalReturnObject.getErrmsg());
        }
        //这里因为返回的对象需要同时从OnSale表和AdvanceSale表拿数据，所以只能用一次cloneVo
        FullAdvanceSaleRetVo fullAdvanceSaleRetVo = (FullAdvanceSaleRetVo) cloneVo(advanceSaleBo, FullAdvanceSaleRetVo.class);
        fullAdvanceSaleRetVo.setCreator(new SimpleUserRetVo(advanceSaleBo.getCreatorId(),advanceSaleBo.getCreatorName()));
        fullAdvanceSaleRetVo.setModifier(new SimpleUserRetVo(advanceSaleBo.getModifierId(),advanceSaleBo.getModifierName()));

        //将OnSale的字段赋给retVo
        FullOnSaleVo fullOnSaleVo=(FullOnSaleVo) internalReturnObject.getData();
        fullAdvanceSaleRetVo.setShop(fullOnSaleVo.getShop());
        fullAdvanceSaleRetVo.setBeginTime(fullOnSaleVo.getBeginTime());
        fullAdvanceSaleRetVo.setEndTime(fullOnSaleVo.getEndTime());
        fullAdvanceSaleRetVo.setProduct(fullOnSaleVo.getProduct());
        fullAdvanceSaleRetVo.setPrice(fullOnSaleVo.getPrice());
        fullAdvanceSaleRetVo.setQuantity(fullOnSaleVo.getQuantity());
        return new ReturnObject(fullAdvanceSaleRetVo);
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

        AdvanceSale advanceSaleBo = (AdvanceSale) cloneVo(advanceSaleVo, AdvanceSale.class);
        advanceSaleBo.setState(AdvanceSaleState.DRAFT.getCode());

        //先判断商铺是否存在
        InternalReturnObject<SimpleShopVo> shopVoReturnObject= shopService.getShopInfo(shopId);
        if (shopVoReturnObject.getData() == null) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "不存在该商铺");
        }
        advanceSaleBo.setShopId(shopId);
        advanceSaleBo.setShopName(shopVoReturnObject.getData().getName());

        //调用goodsService，根据shopId,productId，beginTime，endTime获取OnSale列表,判断要加入的活动的时间是否和已有product的预售活动时间冲突
        InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> onSaleList1=goodsService.getOnSales(shopId,id, TimeFormat.ZonedDateTime2LocalDateTime(advanceSaleVo.getBeginTime()),TimeFormat.ZonedDateTime2LocalDateTime(advanceSaleVo.getEndTime()),1,1);
        List<SimpleOnSaleInfoVo> list=new ArrayList<>();
        if(onSaleList1.getData().getList()!=null) {
            long total = onSaleList1.getData().getTotal();
            InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> onSaleList2 = goodsService.getOnSales(shopId, id, TimeFormat.ZonedDateTime2LocalDateTime(advanceSaleVo.getBeginTime()), TimeFormat.ZonedDateTime2LocalDateTime(advanceSaleVo.getEndTime()), 1, (int) total);
            list=onSaleList2.getData().getList();
        }
        //判断是否有销售时间和预售活动时间冲突的OnSale
        if (advanceSaleVo.getBeginTime() != null && advanceSaleVo.getEndTime() != null) {
            for (SimpleOnSaleInfoVo vo : list) {
                if (!(advanceSaleVo.getEndTime().isBefore(vo.getBeginTime()) || advanceSaleVo.getBeginTime().isAfter(vo.getEndTime()))) {
                    return new ReturnObject<>(ReturnNo.GOODS_PRICE_CONFLICT,"商品销售时间冲突");
                }
            }
        }

        OnSaleCreatedVo onSaleCreatedVo = (OnSaleCreatedVo) cloneVo(advanceSaleVo, OnSaleCreatedVo.class);
        //设置新增的OnSale的type为3,表示预售类型
        onSaleCreatedVo.setType(Byte.valueOf("3"));
        //新增记录到OnSale表
        InternalReturnObject internalReturnObject=goodsService.addOnSale(shopId,id, onSaleCreatedVo);
        //新增OnSale表失败
        if(internalReturnObject.getData()==null){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,internalReturnObject.getErrmsg());
        }

        //新增记录到AdvanceSale表
        ReturnObject returnObject = advanceSaleDao.addAdvanceSale(loginUserId, loginUerName, advanceSaleBo);
        if(returnObject.getData()==null){
            return returnObject;
        }
        SimpleAdvanceSaleRetVo simpleAdvanceSaleRetVo = (SimpleAdvanceSaleRetVo) cloneVo(returnObject.getData(), SimpleAdvanceSaleRetVo.class);
        return new ReturnObject<>(simpleAdvanceSaleRetVo);
    }

    /**
     * 调用GoodsService接口
     * 先根据商铺号shopId和活动id找SimpleOnSaleInfo，获取OnSale的id
     * 再根据OnSale的id查找OnSale的详细信息
     * @param shopId
     * @param id
     * @return
     */
    public InternalReturnObject getOnSaleInfo(Long shopId,Long id) {
        SimpleOnSaleInfoVo simpleOnSaleInfoVo = new SimpleOnSaleInfoVo();
        //根据shopId和预售活动id查找OnSale表，由于预售活动和OnSale是一对一关系，最多只会查到一条OnSale记录
        InternalReturnObject<PageVo<SimpleOnSaleInfoVo>> pageVoInternalReturnObject=goodsService.getOnSale(shopId,id,null,null,null,1,10);
        if (pageVoInternalReturnObject.getErrno()!=0){
            return pageVoInternalReturnObject;
        }
        PageVo<SimpleOnSaleInfoVo> data = pageVoInternalReturnObject.getData();

        if (data.getList() != null) {
            simpleOnSaleInfoVo = pageVoInternalReturnObject.getData().getList().get(0);
        }
        //OnSale表中查不到
        else{
            return new InternalReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST.getCode(), "找不到该预售活动对应的销售信息");
        }
        InternalReturnObject<FullOnSaleVo> returnObject=goodsService.getOnSaleById(simpleOnSaleInfoVo.getId());
        return returnObject;
    }
}
