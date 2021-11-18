package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.GoodsDao;
import cn.edu.xmu.oomall.goods.model.bo.Goods;
import cn.edu.xmu.oomall.goods.model.po.GoodsPo;
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.edu.xmu.oomall.core.util.Common.cloneVo;

/**
 * @author Huang Tianyue
 * 2021.11.15
 **/
@Service
public class GoodsService {
    @Autowired
    private GoodsDao goodsDao;
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject insertGoods(Long shopId,GoodsVo goodsVo)
    {
        Goods goods=(Goods)cloneVo(goodsVo,Goods.class);
        goods.setShopId(shopId);
        GoodsPo goodsPo=(GoodsPo) cloneVo(goods,GoodsPo.class);
        return goodsDao.createNewGoods(goodsPo);
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject deleteGoods(Long shopId,Long id)
    {
        return new ReturnObject(goodsDao.deleteGoodsById(shopId,id));
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject updateGoods(Long shopId,Long id,GoodsVo goodsVo)
    {
        Goods goods=goodsDao.searchGoodsById(shopId,id).getData();
        if(goods==null)
        {
            return new ReturnObject(goodsDao.searchGoodsById(shopId,id));
        }
        goods.setName(goodsVo.getName());
        GoodsPo goodsPo=(GoodsPo) cloneVo(goods,GoodsPo.class);
        return new ReturnObject(goodsDao.updateGoods(goodsPo));
    }

    @Transactional(readOnly = true,rollbackFor=Exception.class)
    public ReturnObject searchById(Long shopId,Long id)
    {
        return goodsDao.searchGoodsById(shopId,id);
    }

}
