package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.goods.dao.GoodsDao;
import cn.edu.xmu.oomall.goods.model.bo.Goods;
import cn.edu.xmu.oomall.goods.model.bo.VoObject;
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import cn.edu.xmu.oomall.goods.util.ResponseCode;
import cn.edu.xmu.oomall.goods.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsService {
    @Autowired
    private GoodsDao goodsDao;
    public ReturnObject insertGoods(GoodsVo goodsVo)
    {
        return new ReturnObject(goodsDao.createNewGoods(goodsVo.createGoods()));
    }
    public ReturnObject deleteGoods(Long id)
    {
        return new ReturnObject(goodsDao.deleteGoodsById(id));
    }
    public ReturnObject updateGoods(Long id,GoodsVo goodsVo)
    {
        Goods goods=goodsDao.findGoodsById(id);
        goods.setGoods(goodsVo.createGoods());
        return new ReturnObject(goodsDao.updateGoods(goods));
    }

    public ReturnObject searchById(Long id)
    {
        return new ReturnObject(goodsDao.searchGoodsById(id));
    }

}
