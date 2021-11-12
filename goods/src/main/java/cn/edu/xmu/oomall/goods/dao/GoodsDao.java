package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.goods.mapper.GoodsPoMapper;
import cn.edu.xmu.oomall.goods.mapper.ProductPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.Goods;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.po.GoodsPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;
import cn.edu.xmu.oomall.goods.util.ResponseCode;
import cn.edu.xmu.oomall.goods.util.ReturnNo;
import cn.edu.xmu.oomall.goods.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.List;

@Repository
public class GoodsDao {
    @Autowired
    private GoodsPoMapper goodsPoMapper;

    @Autowired
    private ProductPoMapper productPoMapper;

    public ReturnObject createNewGoods(Goods goods)
    {
        int flag=goodsPoMapper.insert(goods.getGoodsPo());
        if(flag==0)
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        return new ReturnObject(ReturnNo.OK);
    }

    public ReturnObject updateGoods(Goods goods)
    {
        int flag=goodsPoMapper.updateByPrimaryKeySelective(goods.getGoodsPo());
        if(flag==0)
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        return new ReturnObject<>(ReturnNo.OK);
    }
    public Goods findGoodsById(Long id)
    {
        GoodsPo goodsPo=goodsPoMapper.selectByPrimaryKey(id);
        Goods returnGoods=new Goods(goodsPo);
        return returnGoods;
    }
    public ReturnObject<Goods> searchGoodsById(Long id)
    {
        GoodsPo goodsPo=goodsPoMapper.selectByPrimaryKey(id);
        Goods returnGoods=new Goods(goodsPo);
        List<Product> productList=null;
        if(goodsPo!=null)
        {
            List<ProductPo> products=productPoMapper.selectProductByGoodsId(id);
            productList=new ArrayList<>(products.size());
            for(ProductPo productPo:products)
                productList.add(new Product(productPo));
        }
        returnGoods.setProductList(productList);
        return new ReturnObject<Goods>(returnGoods);
    }
    public ReturnObject<Object> deleteGoodsById(Long id)
    {
        GoodsPo goodsPo=goodsPoMapper.selectByPrimaryKey(id);
        if(goodsPo!=null)
        {
            List<ProductPo> productPoList=productPoMapper.selectProductByGoodsId(id);
            for(ProductPo productPo:productPoList)
            {
                int sign=productPoMapper.updateGoodsId(productPo.getId(),Long.valueOf(0));
                if(sign==0)
                    return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
            }
        }
        int flag=goodsPoMapper.deleteByPrimaryKey(id);
        if(flag==0)
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        return new ReturnObject(ReturnNo.OK);
    }


}
