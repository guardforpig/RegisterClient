package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.OnSaleGetDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import cn.edu.xmu.oomall.goods.model.bo.SimpleProductBo;
import cn.edu.xmu.oomall.goods.model.po.OnSalePo;
import cn.edu.xmu.oomall.goods.model.po.OnSalePoExample;
import cn.edu.xmu.oomall.goods.model.vo.NewOnSaleRetVo;
import cn.edu.xmu.oomall.goods.model.vo.OnSaleRetVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/13 02:49
 **/
@Service
public class
OnSaleGetService {
    /**
     * 活动类型
     */
    private final Byte NO_ACTIVITY=0;
    private final Byte SECOND_KILL=1;
    private final Byte GROUPON=2;
    private final Byte ADVANCE_SALE=3;

    @Autowired
    private OnSaleGetDao onSaleDao;

    @Autowired
    private ProductDao productDao;

    /**
     * 管理员查询特定商品的价格浮动
     * @param shopId
     * @param id
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectCertainOnsale(Long shopId, Long id,Integer page,Integer pageSize){
        return onSaleDao.selectCertainOnsale(shopId,id,page,pageSize);
    }

    /**
     * 管理员查询特定价格浮动的详情
     * @param shopId
     * @param id
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectOnsale(Long shopId, Long id){
        ReturnObject returnObject=onSaleDao.selectOnSale(id);
        if(!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        OnSale onSale=(OnSale) returnObject.getData();
        OnSalePo onSalePo=(OnSalePo) Common.cloneVo(onSale,OnSalePo.class);
        if(onSalePo.getType().equals(NO_ACTIVITY)||onSalePo.getType().equals(SECOND_KILL)){
            NewOnSaleRetVo onSaleRetVo=(NewOnSaleRetVo)Common.cloneVo(onSalePo, NewOnSaleRetVo.class);
            return new ReturnObject(onSaleRetVo);
        }else{
            return new ReturnObject(ReturnNo.TYPENOTALLOW);
        }
    }

    /**
     * 查询团购预售活动的所有价格浮动
     * @param id
     * @param state
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectActivities(Long id, Long did, Byte state, LocalDateTime beginTime,
                                         LocalDateTime endTime,Integer page, Integer pageSize){
        return onSaleDao.selectActivities(id,did,state,beginTime,endTime,page,pageSize);
    }

    /**
     * 内部API-查询特定活动的所有价格浮动
     * @param id
     * @param state
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectShareActivities(Long did,Long id,Byte state,Integer page,Integer pageSize){
        return onSaleDao.selectShareActivities(did,id,state,page,pageSize);
    }

    /**
     * 内部API- 查询特定价格浮动的详情
     * @param id
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectFullOnsale(Long id){
        ReturnObject returnObject=onSaleDao.selectOnSale(id);
        if(!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        OnSale onSale=(OnSale) returnObject.getData();
        OnSalePo onSalePo=(OnSalePo) Common.cloneVo(onSale,OnSalePo.class);
        OnSaleRetVo onSaleRetVo=(OnSaleRetVo)Common.cloneVo(onSalePo,OnSaleRetVo.class);
        ReturnObject returnObjectProduct=productDao.getProductInfo(onSalePo.getProductId());
        if(returnObjectProduct.getCode().equals(ReturnNo.OK)){
            SimpleProductBo simpleProductBo=(SimpleProductBo) returnObjectProduct.getData();
            onSaleRetVo.setProduct(simpleProductBo);
        }
        return new ReturnObject(onSaleRetVo);
    }

    /**
     * 管理员查询所有商品的价格浮动
     * @param shopId
     * @param productId
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectAnyOnsale(Long shopId,Long productId,LocalDateTime beginTime,
                                        LocalDateTime endTime,Integer page,Integer pageSize){
        return onSaleDao.selectAnyOnsale(shopId,productId,beginTime,endTime,page,pageSize);
    }
}
