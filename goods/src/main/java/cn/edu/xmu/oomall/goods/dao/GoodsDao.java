package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.mapper.GoodsPoMapper;
import cn.edu.xmu.oomall.goods.mapper.ProductPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.Goods;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.po.GoodsPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPoExample;

import cn.edu.xmu.oomall.goods.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.List;
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
    private RedisUtils redisUtils;

    @Value("${goodsdemo.goods.expiretime}")
    private long goodsTimeout;

    public ReturnObject createNewGoods(Goods goods)
    {
        try
        {
            goodsPoMapper.insert(goods.getGoodsPo());
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        return new ReturnObject(ReturnNo.OK);
    }

    public ReturnObject updateGoods(Goods goods)
    {
        try
        {
            goodsPoMapper.updateByPrimaryKeySelective(goods.getGoodsPo());
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
                redisUtils.set(id.toString(),new Goods(goodsPo),goodsTimeout);
            }
        }
        catch (Exception e)
        {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        Goods returnGoods=new Goods(goodsPo);
        List<Product> productList=null;
        ProductPoExample productPoExample=new ProductPoExample();
        ProductPoExample.Criteria cr=productPoExample.createCriteria();
        cr.andGoodsIdEqualTo(id);
        List<ProductPo> products=productPoMapper.selectByExample(productPoExample);
            productList=new ArrayList<>(products.size());
            for(ProductPo productPo:products)
            {
                productList.add(new Product(productPo));
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
                redisUtils.set(id.toString(),new Goods(goodsPo),goodsTimeout);
            }
        }
        catch (Exception e)
        {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }

        ProductPoExample productPoExample=new ProductPoExample();
        ProductPoExample.Criteria cr=productPoExample.createCriteria();
        cr.andGoodsIdEqualTo(id);
            List<ProductPo> productPoList=productPoMapper.selectByExample(productPoExample);
            for(ProductPo productPo:productPoList)
            {
                try
                {
                    productPo.setGoodsId(Long.valueOf(0));
                    productPoMapper.updateByPrimaryKey(productPo);
                }
                catch (Exception e)
                {
                    return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
                }
            }
        try
        {
            goodsPoMapper.deleteByPrimaryKey(id);
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        return new ReturnObject(ReturnNo.OK);
    }


}
