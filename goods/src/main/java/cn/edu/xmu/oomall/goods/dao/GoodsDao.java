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
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(GoodsDao.class);

    @Autowired
    private GoodsPoMapper goodsPoMapper;

    @Autowired
    private ProductPoMapper productPoMapper;

    @Autowired
    private RedisUtil redisUtils;

    @Value("${goodsdemo.goods.expiretime}")
    private long goodsTimeout;

    public ReturnObject createNewGoods(GoodsPo goodsPo)
    {
        try
        {
            goodsPoMapper.insert(goodsPo);
            return new ReturnObject((Goods) cloneVo(goodsPo,Goods.class));
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject updateGoods(GoodsPo goodsPo)
    {
        try
        {
            goodsPoMapper.updateByPrimaryKeySelective(goodsPo);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        return new ReturnObject<>(ReturnNo.OK);
    }

    public ReturnObject<Goods> searchGoodsById(Long shopId,Long id)
    {
        try
        {
            Goods goods=(Goods) redisUtils.get("g_"+id);

            if(goods!=null)
            {
                if(!goods.getShopId().equals(shopId))
                {
                    return new ReturnObject(ReturnNo.FIELD_NOTVALID);
                }
                return new ReturnObject<>(goods);
            }
            else
            {
                GoodsPo goodsPo=goodsPoMapper.selectByPrimaryKey(id);
                if(goodsPo==null)
                {
                    return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
                }
                if(!goodsPo.getShopId().equals(shopId))
                {
                    return new ReturnObject(ReturnNo.FIELD_NOTVALID);
                }
                goods=(Goods)cloneVo(goodsPo,Goods.class);
                ProductPoExample productPoExample=new ProductPoExample();
                ProductPoExample.Criteria cr=productPoExample.createCriteria();
                cr.andGoodsIdEqualTo(id);
                List<ProductPo> products=productPoMapper.selectByExample(productPoExample);
                List<Product> productList=new ArrayList<>(products.size());
                for(ProductPo productPo:products)
                {
                    productList.add((Product) cloneVo(productPo,Product.class));
                }
                goods.setProductList(productList);
                redisUtils.set("g_"+id,goods,goodsTimeout);
                return new ReturnObject<>(goods);
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }
    public ReturnObject<Object> deleteGoodsById(Long shopId,Long id)
    {
        GoodsPo goodsPo;
        try
        {
            Goods goods=(Goods) redisUtils.get("g_"+id);
            if(goods!=null)
            {
                redisUtils.del("g_"+id);
            }
            goodsPo=goodsPoMapper.selectByPrimaryKey(id);
            if(goodsPo==null)
            {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(!goodsPo.getShopId().equals(shopId))
            {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
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
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        return new ReturnObject(ReturnNo.OK);
    }


}
