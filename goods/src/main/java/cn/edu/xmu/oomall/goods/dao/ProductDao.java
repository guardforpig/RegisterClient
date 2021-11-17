package cn.edu.xmu.oomall.goods.dao;

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
    @Autowired
    private ProductPoMapper productPoMapper;
    @Autowired
    private ProductDraftPoMapper productDraftPoMapper;
    @Autowired
    private OnSalePoMapper onSalePoMapper;

    private Byte draft=0;
    private Byte offshelves=1;
    private Byte onshelves=2;
    private Byte prohibit=3;


    public ReturnObject publishById(Long id)
    {
        ProductDraftPo productDraftPo;
        productDraftPo=productDraftPoMapper.selectByPrimaryKey(id);
        if(productDraftPo!=null)
        {
            ProductPo productPo=(ProductPo) cloneVo(productDraftPo,ProductPo.class);
            productPo.setState(offshelves);
            try
            {
                productPoMapper.insert(productPo);
            OnSale onSale=(OnSale)cloneVo(productPo,OnSale.class);
            onSale.setState(OnSale.State.OFFLINE);
            onSalePoMapper.insert((OnSalePo) cloneVo(onSale,OnSalePo.class));
            productDraftPoMapper.deleteByPrimaryKey(id);
            return new ReturnObject((Product)cloneVo(productPo,Product.class));
        }catch(Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        }else
        {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }

    }

    public ReturnObject onshelvesById(Long id)
    {
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
        if(productPo.getState().equals(offshelves))
        {

            productPo.setState(onshelves);
            productPoMapper.updateByPrimaryKey(productPo);
            List<OnSalePo> onSalePoList;
            OnSalePoExample onSalePoExample=new OnSalePoExample();
            OnSalePoExample.Criteria cr=onSalePoExample.createCriteria();
            cr.andProductIdEqualTo(id);
            onSalePoList=onSalePoMapper.selectByExample(onSalePoExample);
            if(onSalePoList==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            for(OnSalePo onSalePo:onSalePoList)
            {
                onSalePo.setState(onshelves);
                onSalePoMapper.updateByPrimaryKey(onSalePo);
            }
            return new ReturnObject(ReturnNo.OK);
        }
        else
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        }catch(Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject offshelvesById(Long id)
    {
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
        if(productPo.getState().equals(onshelves))
        {
            productPo.setState(offshelves);
            productPoMapper.updateByPrimaryKey(productPo);
            List<OnSalePo> onSalePoList;
            OnSalePoExample onSalePoExample=new OnSalePoExample();
            OnSalePoExample.Criteria cr=onSalePoExample.createCriteria();
            cr.andProductIdEqualTo(id);
            onSalePoList=onSalePoMapper.selectByExample(onSalePoExample);
            if(onSalePoList==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            for(OnSalePo onSalePo:onSalePoList)
            {
                onSalePo.setState(offshelves);
                onSalePoMapper.updateByPrimaryKey(onSalePo);
            }
            return new ReturnObject(ReturnNo.OK);
        }
        else
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }catch (Exception e)
    {
        return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
    }

    }

    public ReturnObject allowProductById(Long id)
    {
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
        if(productPo.getState().equals(prohibit))
        {

            productPo.setState(offshelves);
            productPoMapper.updateByPrimaryKey(productPo);
            List<OnSalePo> onSalePoList;
            OnSalePoExample onSalePoExample=new OnSalePoExample();
            OnSalePoExample.Criteria cr=onSalePoExample.createCriteria();
            cr.andProductIdEqualTo(id);
            onSalePoList=onSalePoMapper.selectByExample(onSalePoExample);
            if(onSalePoList==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            for(OnSalePo onSalePo:onSalePoList)
            {
                if(onSalePo.getState().equals(prohibit))
                {
                    onSalePo.setState(offshelves);
                    onSalePoMapper.updateByPrimaryKey(onSalePo);
                }
                else
                {
                    return new ReturnObject(ReturnNo.STATENOTALLOW);
                }
            }
            return new ReturnObject(ReturnNo.OK);
        }
        else
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }catch (Exception e)
    {
        return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
    }
    }

    public ReturnObject prohibitProductById(Long id)
    {
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
        if(productPo.getState().equals(onshelves)||productPo.getState().equals(offshelves))
        {
            productPo.setState(prohibit);
            productPoMapper.updateByPrimaryKey(productPo);
            List<OnSalePo> onSalePoList;
            OnSalePoExample onSalePoExample=new OnSalePoExample();
            OnSalePoExample.Criteria cr=onSalePoExample.createCriteria();
            cr.andProductIdEqualTo(id);
            onSalePoList=onSalePoMapper.selectByExample(onSalePoExample);
            for(OnSalePo onSalePo:onSalePoList)
            {
                if(onSalePo.getState().equals(onshelves)||onSalePo.getState().equals(offshelves))
                {
                    onSalePo.setState(prohibit);
                    onSalePoMapper.updateByPrimaryKey(onSalePo);
                }
                else
                {
                    return new ReturnObject(ReturnNo.STATENOTALLOW);
                }
            }
            return new ReturnObject(ReturnNo.OK);
        }
        else
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }catch (Exception e)
    {
        return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
    }
    }


}
