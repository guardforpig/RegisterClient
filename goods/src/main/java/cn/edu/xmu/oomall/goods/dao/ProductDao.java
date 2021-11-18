package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.core.util.RedisUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.mapper.OnSalePoMapper;
import cn.edu.xmu.oomall.goods.mapper.ProductDraftPoMapper;
import cn.edu.xmu.oomall.goods.mapper.ProductPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.po.OnSalePo;
import cn.edu.xmu.oomall.goods.model.po.OnSalePoExample;
import cn.edu.xmu.oomall.goods.model.po.ProductDraftPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cn.edu.xmu.oomall.core.util.Common.cloneVo;


/**
 * @author Huang Tianyue
 * 2021.11.15
 **/
@Repository
public class ProductDao {
    private static final Logger logger = LoggerFactory.getLogger(ProductDao.class);
    @Autowired
    private ProductPoMapper productPoMapper;
    @Autowired
    private ProductDraftPoMapper productDraftPoMapper;
    @Autowired
    private RedisUtil redisUtils;


    private Byte draft=0;
    private Byte offshelves=1;
    private Byte onshelves=2;
    private Byte prohibit=3;


    public ReturnObject publishById(Long shopId,Long id)
    {
        try
        {
        ProductDraftPo productDraftPo;
        productDraftPo=productDraftPoMapper.selectByPrimaryKey(id);
        if(productDraftPo!=null)
        {
            if(!productDraftPo.getShopId().equals(shopId))
            {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            ProductPo productPo=(ProductPo) cloneVo(productDraftPo,ProductPo.class);
            productPo.setState(offshelves);
            productPoMapper.insert(productPo);
            productDraftPoMapper.deleteByPrimaryKey(id);
            redisUtils.del("g_"+productPo.getGoodsId());
            return new ReturnObject<Product>((Product)cloneVo(productPo,Product.class));
        }else
        {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
    }catch(Exception e)
    {
        logger.error(e.getMessage());
        return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
    }

    }

    public ReturnObject onshelvesById(Long shopId,Long id)
    {
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(!productPo.getShopId().equals(shopId))
            {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
        if(productPo.getState().equals(offshelves))
        {

            productPo.setState(onshelves);
            return new ReturnObject(ReturnNo.OK);
        }
        else
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        }catch(Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject offshelvesById(Long shopId,Long id)
    {
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(!productPo.getShopId().equals(shopId))
            {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
        if(productPo.getState().equals(onshelves))
        {
            productPo.setState(offshelves);
            productPoMapper.updateByPrimaryKey(productPo);
            return new ReturnObject(ReturnNo.OK);
        }
        else
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }catch (Exception e)
    {
        logger.error(e.getMessage());
        return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
    }

    }

    public ReturnObject allowProductById(Long shopId,Long id)
    {
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(!productPo.getShopId().equals(shopId))
            {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
        if(productPo.getState().equals(prohibit))
        {

            productPo.setState(offshelves);
            productPoMapper.updateByPrimaryKey(productPo);
            return new ReturnObject(ReturnNo.OK);
        }
        else
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }catch (Exception e)
    {
        logger.error(e.getMessage());
        return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
    }
    }

    public ReturnObject prohibitProductById(Long shopId,Long id)
    {
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(!productPo.getShopId().equals(shopId))
            {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
        if(productPo.getState().equals(onshelves)||productPo.getState().equals(offshelves))
        {
            productPo.setState(prohibit);
            productPoMapper.updateByPrimaryKey(productPo);
            return new ReturnObject(ReturnNo.OK);
        }
        else
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }catch (Exception e)
    {
        logger.error(e.getMessage());
        return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
    }
    }
}
