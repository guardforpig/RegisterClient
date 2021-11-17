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

    public ReturnObject createNewGoods(Goods goods)
    {
        try
        {
            if(goods.getName()==null)
            {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            GoodsPo goodsPo=(GoodsPo)cloneVo(goods,GoodsPo.class);
            int flag=goodsPoMapper.insert(goodsPo);
            if(flag==0)
            {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            return new ReturnObject((Goods) cloneVo(goodsPo,Goods.class));
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject updateGoods(Goods goods, GoodsVo goodsVo)
    {
        try
        {
            goods.setName(goodsVo.getName());
            GoodsPo goodsPo=(GoodsPo)cloneVo(goods,GoodsPo.class);
            int f1=goodsPoMapper.updateByPrimaryKeySelective(goodsPo);
            if(f1==0)
            {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
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
        GoodsPo goodsPo;
        try
        {
            goodsPo=(GoodsPo) redisUtils.get("g_"+id);
            if(goodsPo==null)
            {
                goodsPo=goodsPoMapper.selectByPrimaryKey(id);
                if(goodsPo!=null)
                {
                    redisUtils.set("g_"+id,(Goods)cloneVo(goodsPo,Goods.class),goodsTimeout);
                }
                else
                {
                    return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
                }
            }
            if(!goodsPo.getShopId().equals(shopId))
            {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
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
            goodsPo=(GoodsPo) redisUtils.get("g_"+id);
            if(goodsPo==null)
            {
                goodsPo=goodsPoMapper.selectByPrimaryKey(id);
                if(goodsPo==null)
                {
                    return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
                }
            }
            else
            {
                redisUtils.del("g_"+id);
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
