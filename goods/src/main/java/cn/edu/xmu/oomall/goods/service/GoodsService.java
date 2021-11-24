package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.GoodsDao;
import cn.edu.xmu.oomall.goods.model.bo.Goods;
import cn.edu.xmu.oomall.goods.model.po.GoodsPo;
import cn.edu.xmu.oomall.goods.model.vo.CreateGoodsVo;
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.edu.xmu.oomall.core.util.Common.cloneVo;

/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
@Service
public class GoodsService {
    @Autowired
    private GoodsDao goodsDao;
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject insertGoods(Long shopId,GoodsVo goodsVo,Long loginUserId,String loginUserName)
    {
        Goods goods=(Goods)cloneVo(goodsVo,Goods.class);
        Common.setPoCreatedFields(goods,loginUserId,loginUserName);
        goods.setShopId(shopId);
        ReturnObject<Goods> ret=goodsDao.createNewGoods(goods);
        if(ret.getData()!=null){
            CreateGoodsVo goodsVo1=(CreateGoodsVo)Common.cloneVo(ret.getData(),CreateGoodsVo.class);
            return new ReturnObject(goodsVo1);
        }else{
            return ret;
        }
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject deleteGoods(Long shopId,Long id)
    {
        return new ReturnObject(goodsDao.deleteGoodsById(shopId,id));
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject updateGoods(Long shopId,Long id,GoodsVo goodsVo,Long loginUser,String loginUserName)
    {
        Goods goods = (Goods)Common.cloneVo(goodsVo,Goods.class);
        goods.setId(id);
        goods.setShopId(shopId);
        Common.setPoModifiedFields(goods,loginUser,loginUserName);
        return new ReturnObject(goodsDao.updateGoods(goods));
    }

    @Transactional(readOnly = true,rollbackFor=Exception.class)
    public ReturnObject searchById(Long shopId,Long id)
    {
        ReturnObject<Goods> ret=goodsDao.searchGoodsById(shopId,id);
        if(ret.getData()!=null){
            GoodsVo goodsVo=(GoodsVo)Common.cloneVo(ret.getData(),GoodsVo.class);
            return new ReturnObject(goodsVo);
        }else{
            return ret;
        }
    }

}
