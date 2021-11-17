package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.core.util.RedisUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.mapper.GoodsPoMapper;
import cn.edu.xmu.oomall.goods.mapper.ProductPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.Goods;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.po.GoodsPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPoExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.oomall.core.util.Common.cloneVo;

/**
 * @author Huang Tianyue
 * 2021.11.15
 **/
@Repository
public class GoodsDao {
    @Autowired
    private GoodsPoMapper goodsPoMapper;

    @Autowired
    private ProductPoMapper productPoMapper;

    @Autowired
    private RedisUtil redisUtils;

    @Value("${goodsdemo.goods.expiretime}")
    private long goodsTimeout;

    public ReturnObject createNewGoods(Goods goods)
    {
        try
        {
            GoodsPo goodsPo=(GoodsPo)cloneVo(goods,GoodsPo.class);
            goodsPoMapper.insert(goodsPo);
            return new ReturnObject((Goods) cloneVo(goodsPo,Goods.class));
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject updateGoods(Goods goods)
    {
        try
        {
            GoodsPo goodsPo=(GoodsPo)cloneVo(goods,GoodsPo.class);
            goodsPoMapper.updateByPrimaryKeySelective(goodsPo);
        }
        catch (Exception e)
        {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        return new ReturnObject<>(ReturnNo.OK);
    }

    public ReturnObject<Goods> searchGoodsById(Long id)
    {
        GoodsPo goodsPo;
        try
        {
            goodsPo=(GoodsPo) redisUtils.get(id.toString());
            if(goodsPo==null)
            {
                goodsPo=goodsPoMapper.selectByPrimaryKey(id);
                if(goodsPo!=null)
                {
                    redisUtils.set(id.toString(),(Goods)cloneVo(goodsPo,Goods.class),goodsTimeout);
                }
                else
                {
                    return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
                }
            }
        }
        catch (Exception e)
        {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        Goods returnGoods=(Goods) cloneVo(goodsPo,Goods.class);
        List<Product> productList=null;
        ProductPoExample productPoExample=new ProductPoExample();
        ProductPoExample.Criteria cr=productPoExample.createCriteria();
        cr.andGoodsIdEqualTo(id);
        List<ProductPo> products=productPoMapper.selectByExample(productPoExample);
            productList=new ArrayList<>(products.size());
            for(ProductPo productPo:products)
            {
                productList.add((Product) cloneVo(productPo,Product.class));
            }
        returnGoods.setProductList(productList);
        return new ReturnObject<Goods>(returnGoods);
    }
    public ReturnObject<Object> deleteGoodsById(Long id)
    {
        GoodsPo goodsPo;
        try
        {
            goodsPo=(GoodsPo) redisUtils.get(id.toString());
            if(goodsPo==null)
            {
                goodsPo=goodsPoMapper.selectByPrimaryKey(id);
                if(goodsPo!=null)
                {
                    Goods goods=(Goods)cloneVo(goodsPo,Goods.class);
                    redisUtils.set(id.toString(),goods,goodsTimeout);
                }
                else {
                    return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
                }
            }

        ProductPoExample productPoExample=new ProductPoExample();
        ProductPoExample.Criteria cr=productPoExample.createCriteria();
        cr.andGoodsIdEqualTo(id);
            List<ProductPo> productPoList=productPoMapper.selectByExample(productPoExample);
            for(ProductPo productPo:productPoList)
            {
                    productPo.setGoodsId(Long.valueOf(0));
                    productPoMapper.updateByPrimaryKey(productPo);
            }

            goodsPoMapper.deleteByPrimaryKey(id);
        }
        catch (Exception e)
        {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        return new ReturnObject(ReturnNo.OK);
    }


}
