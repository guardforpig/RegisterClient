package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.mapper.OnSalePoMapper;
import cn.edu.xmu.oomall.goods.mapper.ProductDraftPoMapper;
import cn.edu.xmu.oomall.goods.mapper.ProductPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import cn.edu.xmu.oomall.goods.model.po.OnSalePo;
import cn.edu.xmu.oomall.goods.model.po.ProductDraftPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;
import cn.edu.xmu.oomall.goods.util.RedisUtils;
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
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(productPo.getState().equals(draft))
        {
            productPoMapper.updateState(id,offshelves);
            ProductDraftPo productDraftPo;
            try
            {
                productDraftPo=productDraftPoMapper.selectByProductId(id);
            }
            catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            try
            {
                productDraftPoMapper.deleteByPrimaryKey(productDraftPo.getId());
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
                return new ReturnObject(ReturnNo.OK);
        }
        else
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
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
                productPoMapper.updateState(id,onshelves);
            }
            catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            OnSalePo onSalePo=onSalePoMapper.selectByProductId(id);
            if(onSalePo==null)
            {
                OnSale onSale=new OnSale(productPo);
                try
                {
                    onSalePoMapper.insert(onSale.getOnSalePo());
                }
                catch (Exception e)
                {
                    return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
                }
                return new ReturnObject(ReturnNo.OK);
            }
            else
            {
                try
                {
                    onSalePoMapper.updateState(onSalePo.getId(),(byte)2);
                }
                catch (Exception e)
                {
                    return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
                }
                return new ReturnObject(ReturnNo.OK);
            }
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
                productPoMapper.updateState(id,offshelves);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            OnSalePo onSalePo;
            try
            {
                onSalePo=onSalePoMapper.selectByProductId(id);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            try
            {
                onSalePoMapper.updateState(onSalePo.getId(),offshelves);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
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
                productPoMapper.updateState(id,offshelves);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            OnSalePo onSalePo;
            try
            {
                onSalePo=onSalePoMapper.selectByProductId(id);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            if(onSalePo.getState().equals(prohibit))
            {
                try
                {
                    onSalePoMapper.updateState(onSalePo.getId(),offshelves);
                }catch (Exception e)
                {
                    return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
                }
                return new ReturnObject(ReturnNo.OK);
            }
            else
            {
                return new ReturnObject(ReturnNo.STATENOTALLOW);
            }
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
        if(productPo.getState().equals(onshelves))
        {
            try
            {
                productPoMapper.updateState(id,prohibit);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            OnSalePo onSalePo;
            try
            {
                onSalePo=onSalePoMapper.selectByProductId(id);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
            if(onSalePo.getState().equals(onshelves))
            {
                try
                {
                    onSalePoMapper.updateState(onSalePo.getId(),prohibit);
                }catch (Exception e)
                {
                    return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
                }
                return new ReturnObject(ReturnNo.OK);
            }
            else
            {
                return new ReturnObject(ReturnNo.STATENOTALLOW);
            }
        }
        else
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }


}
