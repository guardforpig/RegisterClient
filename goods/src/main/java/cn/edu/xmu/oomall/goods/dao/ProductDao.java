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

@Repository
public class ProductDao {
    @Autowired
    private ProductPoMapper productPoMapper;
    @Autowired
    private ProductDraftPoMapper productDraftPoMapper;
    @Autowired
    private OnSalePoMapper onSalePoMapper;


    public ReturnObject publishById(Long id)
    {
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(productPo.getState().equals((byte)0))
        {
            productPoMapper.updateState(id,(byte)1);
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
            return new ReturnObject(ReturnNo.STATENOTALLOW);

    }

    public ReturnObject onshelvesById(Long id)
    {
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }catch(Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(productPo.getState().equals((byte)1))
        {
            try
            {
                productPoMapper.updateState(id,(byte)2);
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
            return new ReturnObject(ReturnNo.STATENOTALLOW);
    }

    public ReturnObject offshelvesById(Long id)
    {
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(productPo.getState().equals((byte)2))
        {
            try
            {
                productPoMapper.updateState(id,(byte)1);
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
                onSalePoMapper.updateState(onSalePo.getId(),(byte)1);
            }catch (Exception e)
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
                return new ReturnObject(ReturnNo.OK);


        }
        else
            return new ReturnObject(ReturnNo.STATENOTALLOW);
    }

    public ReturnObject allowProductById(Long id)
    {
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(productPo.getState().equals((byte)3))
        {
            try
            {
                productPoMapper.updateState(id,(byte)1);
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
            if(onSalePo.getState().equals((byte)3))
            {
                try
                {
                    onSalePoMapper.updateState(onSalePo.getId(),(byte)1);
                }catch (Exception e)
                {
                    return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
                }
                return new ReturnObject(ReturnNo.OK);
            }
            else
                return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        else
            return new ReturnObject(ReturnNo.STATENOTALLOW);
    }

    public ReturnObject prohibitProductById(Long id)
    {
        ProductPo productPo;
        try
        {
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(productPo.getState().equals((byte)2))
        {
            try
            {
                productPoMapper.updateState(id,(byte)3);
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
            if(onSalePo.getState().equals((byte)2))
            {
                try
                {
                    onSalePoMapper.updateState(onSalePo.getId(),(byte)3);
                }catch (Exception e)
                {
                    return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
                }
                return new ReturnObject(ReturnNo.OK);
            }
            else
                return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        else
            return new ReturnObject(ReturnNo.STATENOTALLOW);
    }


}
