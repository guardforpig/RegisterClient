package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.privilegegateway.util.RedisUtil;
import cn.edu.xmu.oomall.goods.mapper.ProductPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.po.ProductDraftPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPoExample;
import cn.edu.xmu.oomall.goods.model.vo.ProductVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.oomall.core.util.Common.cloneVo;


/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
@Repository
public class ProductDao {
    private static final Logger logger = LoggerFactory.getLogger(ProductDao.class);
    @Autowired
    private ProductPoMapper productPoMapper;
    @Autowired
    private ProductDraftPoMapper productDraftPoMapper;
    @Autowired
    private RedisUtil redisUtil;


    public ReturnObject listProductsByFreightId(Long shopId,Long fid,Integer pageNumber, Integer pageSize)
    {
        try {
            PageHelper.startPage(pageNumber, pageSize, true, true, true);
            if (shopId != 0) {
                return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE, "此商铺没有发布货品的权限");
            }
            ProductPoExample productPoExample = new ProductPoExample();
            ProductPoExample.Criteria cr = productPoExample.createCriteria();
            cr.andFreightIdEqualTo(fid);
            List<ProductPo> products = productPoMapper.selectByExample(productPoExample);
            List<ProductVo> productList = new ArrayList<>(products.size());
            for (ProductPo productPo : products) {
                ProductVo productVo = (ProductVo) cloneVo(productPo, ProductVo.class);
                productList.add(productVo);
            }
            PageInfo<VoObject> pageInfo = new PageInfo(productList);
            return new ReturnObject<>(pageInfo);
        }catch(Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }

    }


    public ReturnObject publishById(Long shopId,Long id)
    {
        try
        {
            if(shopId!=0){
                return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有发布货品的权限");
            }
        ProductDraftPo productDraftPo;
        productDraftPo=productDraftPoMapper.selectByPrimaryKey(id);
        if(productDraftPo!=null)
        {
            ProductPo productPo=(ProductPo) cloneVo(productDraftPo,ProductPo.class);
            productPo.setState((byte) Product.ProductState.OFFSHELF.getCode());
            productPoMapper.insert(productPo);
            productDraftPoMapper.deleteByPrimaryKey(id);
            redisUtil.del("g_"+productPo.getGoodsId());
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
            if(shopId!=0){
                return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有发布货品的权限");
            }
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
        if(productPo.getState().equals((byte) Product.ProductState.OFFSHELF.getCode()))
        {
            productPo.setState((byte) Product.ProductState.ONSHELF.getCode());
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
            if(shopId!=0){
                return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有发布货品的权限");
            }
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
        if(productPo.getState().equals((byte) Product.ProductState.ONSHELF.getCode()))
        {
            productPo.setState((byte) Product.ProductState.OFFSHELF.getCode());
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
            if(shopId!=0){
                return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有发布货品的权限");
            }
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
        if(productPo.getState().equals((byte) Product.ProductState.BANNED.getCode()))
        {

            productPo.setState((byte) Product.ProductState.OFFSHELF.getCode());
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
            if(shopId!=0){
                return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有发布货品的权限");
            }
            productPo=productPoMapper.selectByPrimaryKey(id);
            if(productPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
        if(productPo.getState().equals((byte) Product.ProductState.ONSHELF.getCode())||productPo.getState().equals((byte) Product.ProductState.OFFSHELF.getCode()))
        {
            productPo.setState((byte) Product.ProductState.BANNED.getCode());
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
