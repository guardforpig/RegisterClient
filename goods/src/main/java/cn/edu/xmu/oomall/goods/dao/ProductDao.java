package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.mapper.OnSalePoMapper;
import cn.edu.xmu.oomall.goods.mapper.ProductDraftPoMapper;
import cn.edu.xmu.oomall.goods.mapper.ProductPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.po.*;
import cn.edu.xmu.oomall.goods.util.RedisUtils;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
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
            Product product=new Product(productDraftPo);
            product.setState(offshelves);
            try
            {
                productPoMapper.insert(product.getProductPo());
            }catch(Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            OnSale onSale=new OnSale(product.getProductPo());
            onSale.setState(offshelves);
            try
            {
                onSalePoMapper.insert(onSale.getOnSalePo());
            }
            catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            return new ReturnObject(ReturnNo.OK);
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
        }catch(Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(productPo.getState().equals(offshelves))
        {
            try
            {
                productPo.setState(onshelves);
                productPoMapper.updateByPrimaryKey(productPo);
            }
            catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
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
                    try
                    {
                        onSalePo.setState(onshelves);
                        onSalePoMapper.updateByPrimaryKey(onSalePo);
                    }
                    catch (Exception e)
                    {
                        return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
                    }

            }
            return new ReturnObject(ReturnNo.OK);
        }
        else
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
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
        }catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(productPo.getState().equals(onshelves))
        {
            try
            {
                productPo.setState(offshelves);
                productPoMapper.updateByPrimaryKey(productPo);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            List<OnSalePo> onSalePoList;
            try
            {
                OnSalePoExample onSalePoExample=new OnSalePoExample();
                OnSalePoExample.Criteria cr=onSalePoExample.createCriteria();
                cr.andProductIdEqualTo(id);
                onSalePoList=onSalePoMapper.selectByExample(onSalePoExample);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            if(onSalePoList==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            for(OnSalePo onSalePo:onSalePoList)
            {
                try
                {
                    onSalePo.setState(offshelves);
                    onSalePoMapper.updateByPrimaryKey(onSalePo);
                }catch (Exception e)
                {
                    return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
                }
            }
            return new ReturnObject(ReturnNo.OK);
        }
        else
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
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
        }catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(productPo.getState().equals(prohibit))
        {
            try
            {
                productPo.setState(offshelves);
                productPoMapper.updateByPrimaryKey(productPo);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            List<OnSalePo> onSalePoList;
            try
            {
                OnSalePoExample onSalePoExample=new OnSalePoExample();
                OnSalePoExample.Criteria cr=onSalePoExample.createCriteria();
                cr.andProductIdEqualTo(id);
                onSalePoList=onSalePoMapper.selectByExample(onSalePoExample);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            if(onSalePoList==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            for(OnSalePo onSalePo:onSalePoList)
            {
                if(onSalePo.getState().equals(prohibit))
                {
                    try
                    {
                        onSalePo.setState(offshelves);
                        onSalePoMapper.updateByPrimaryKey(onSalePo);
                    }catch (Exception e)
                    {
                        return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
                    }
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
        }catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(productPo.getState().equals(onshelves)||productPo.getState().equals(offshelves))
        {
            try
            {
                productPo.setState(prohibit);
                productPoMapper.updateByPrimaryKey(productPo);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            List<OnSalePo> onSalePoList;
            try
            {
                OnSalePoExample onSalePoExample=new OnSalePoExample();
                OnSalePoExample.Criteria cr=onSalePoExample.createCriteria();
                cr.andProductIdEqualTo(id);
                onSalePoList=onSalePoMapper.selectByExample(onSalePoExample);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            for(OnSalePo onSalePo:onSalePoList)
            {
                if(onSalePo.getState().equals(onshelves)||onSalePo.getState().equals(offshelves))
                {
                    try
                    {
                        onSalePo.setState(prohibit);
                        onSalePoMapper.updateByPrimaryKey(onSalePo);
                    }catch (Exception e)
                    {
                        return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
                    }
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
    }


}
