package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.OnSaleDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import cn.edu.xmu.oomall.goods.model.vo.OnSaleRetVo;
import cn.edu.xmu.oomall.goods.model.vo.ProductVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static cn.edu.xmu.oomall.core.util.Common.cloneVo;


/**
 * @author 黄添悦
 **/
@Service
@Component
public class OnsaleService {

    private Logger logger = LoggerFactory.getLogger(OnsaleService.class);

    @Autowired
    private OnSaleDao onsaleDao;


    @Transactional(rollbackFor = Exception.class,readOnly = true)
    public ReturnObject listOnsalesByProductId(Long shopId,Long productId,Integer pageNumber, Integer pageSize)
    {
        return onsaleDao.listOnSalesByProductId(shopId,productId,pageNumber,pageSize) ;
    }
    @Transactional(rollbackFor = Exception.class,readOnly = true)
    public ReturnObject getOnSalesById(Long shopId,Long id)
    {
        OnSale onSale= (OnSale) onsaleDao.getOnSalesId(shopId,id).getData();
        OnSaleRetVo onSaleRetVo=(OnSaleRetVo) cloneVo(onSale,OnSaleRetVo.class);
        onSaleRetVo.setProductVo((ProductVo) cloneVo(onSale.getProduct(),ProductVo.class));
        return new ReturnObject(onSaleRetVo);
    }

}


