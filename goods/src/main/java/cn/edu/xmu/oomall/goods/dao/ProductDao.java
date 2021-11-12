package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.goods.mapper.OnSalePoMapper;
import cn.edu.xmu.oomall.goods.mapper.ProductDraftPoMapper;
import cn.edu.xmu.oomall.goods.mapper.ProductPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import cn.edu.xmu.oomall.goods.model.po.OnSalePo;
import cn.edu.xmu.oomall.goods.model.po.ProductDraftPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;
import cn.edu.xmu.oomall.goods.util.ReturnNo;
import cn.edu.xmu.oomall.goods.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
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
        ProductPo productPo=productPoMapper.selectByPrimaryKey(id);
        if(productPo!=null&&productPo.getState().equals((byte)0))
        {
            productPoMapper.updateState(id,(byte)1);
            ProductDraftPo productDraftPo=productDraftPoMapper.selectByProductId(id);
            if(productDraftPo!=null)
            {
                productDraftPoMapper.deleteByPrimaryKey(productDraftPo.getId());
                return new ReturnObject(ReturnNo.OK);
            }
            else
            {
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
            }
        }
        else
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);

    }

    public ReturnObject onshelvesById(Long id)
    {
        ProductPo productPo=productPoMapper.selectByPrimaryKey(id);
        if(productPo!=null&&productPo.getState().equals((byte)1))
        {
            productPoMapper.updateState(id,(byte)2);
            OnSalePo onSalePo=onSalePoMapper.selectByProductId(id);
            if(onSalePo==null)
            {
                OnSale onSale=new OnSale(productPo);
                onSalePoMapper.insert(onSale.getOnSalePo());
                return new ReturnObject(ReturnNo.OK);
            }
            else
            {
                onSalePoMapper.updateState(onSalePo.getId(),(byte)2);
                return new ReturnObject(ReturnNo.OK);
            }
        }
        else
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
    }

    public ReturnObject offshelvesById(Long id)
    {
        ProductPo productPo=productPoMapper.selectByPrimaryKey(id);
        if(productPo!=null&&productPo.getState()==2)
        {
            productPoMapper.updateState(id,(byte)1);
            OnSalePo onSalePo=onSalePoMapper.selectByProductId(id);
            if(onSalePo!=null)
            {
                onSalePoMapper.updateState(onSalePo.getId(),(byte)1);
                return new ReturnObject(ReturnNo.OK);
            }
            else
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);

        }
        else
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
    }

    public ReturnObject allowProductById(Long id)
    {
        ProductPo productPo=productPoMapper.selectByPrimaryKey(id);
        if(productPo!=null&&productPo.getState()==3)
        {
            productPoMapper.updateState(id,(byte)1);
            OnSalePo onSalePo=onSalePoMapper.selectByProductId(id);
            if(onSalePo!=null&&onSalePo.getState().equals((byte)3))
            {
                onSalePoMapper.updateState(onSalePo.getId(),(byte)1);
                return new ReturnObject(ReturnNo.OK);
            }
            else
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
        else
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
    }

    public ReturnObject prohibitProductById(Long id)
    {
        ProductPo productPo=productPoMapper.selectByPrimaryKey(id);
        if(productPo!=null&&productPo.getState()==2)
        {
            productPoMapper.updateState(id,(byte)3);
            OnSalePo onSalePo=onSalePoMapper.selectByProductId(id);
            if(onSalePo!=null&&onSalePo.getState().equals((byte)2))
            {
                onSalePoMapper.updateState(onSalePo.getId(),(byte)3);
                return new ReturnObject(ReturnNo.OK);
            }
            else
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
        else
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
    }


}
