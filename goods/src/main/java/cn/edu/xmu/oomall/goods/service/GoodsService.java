package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.GoodsDao;
import cn.edu.xmu.oomall.goods.model.bo.Goods;
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoodsService {
    @Autowired
    private GoodsDao goodsDao;
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject insertGoods(GoodsVo goodsVo)
    {
        return new ReturnObject(goodsDao.createNewGoods(goodsVo.createGoods()));
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject deleteGoods(Long id)
    {
        return new ReturnObject(goodsDao.deleteGoodsById(id));
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject updateGoods(Long id,GoodsVo goodsVo)
    {
        Goods goods=goodsDao.findGoodsById(id).getData();
        if(goods==null)
            return new ReturnObject(goodsDao.findGoodsById(id));
        goods.setGoods(goodsVo.createGoods());
        return new ReturnObject(goodsDao.updateGoods(goods));
    }

    @Transactional(rollbackFor=Exception.class)
    public ReturnObject searchById(Long id)
    {
        return new ReturnObject(goodsDao.searchGoodsById(id));
    }

}
