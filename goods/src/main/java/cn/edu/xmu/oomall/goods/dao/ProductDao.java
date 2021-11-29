package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import cn.edu.xmu.oomall.goods.mapper.ProductPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import static cn.edu.xmu.oomall.core.util.Common.cloneVo;

/**
 * @author yujie lin
 * @date 2021/11/11
 */
@Repository
public class ProductDao {
    private Logger logger = LoggerFactory.getLogger(OnSaleDao.class);

    @Autowired
    private ProductPoMapper productMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${oomall.goods.product.expiretime}")
    private long productTimeout;

    public ReturnObject hasExist(Long productId) {
        try{
            ProductPo po= productMapper.selectByPrimaryKey(productId);
            return new ReturnObject(null != po) ;
        }
        catch(Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }

    }


    public ReturnObject matchProductShop(Long productId, Long shopId) {
        try{
            ProductPo productPo=productMapper.selectByPrimaryKey(productId);
            return new ReturnObject(shopId.equals(productPo.getShopId())) ;
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }

    }

    public ReturnObject getShopIdById(Long id){
        try{
            Product ret=(Product) redisUtil.get("p_"+id);
            if(null!=ret){
                return new ReturnObject(ret.getId());
            }
            ProductPo po= productMapper.selectByPrimaryKey(id);

            if(po == null) {
                Long shopId=null;
                return new ReturnObject(shopId);
            }
            Product pro=(Product)cloneVo(po,Product.class);
            redisUtil.set("p_"+pro.getId(),pro,productTimeout);

            return new ReturnObject(pro.getId());
        }
        catch(Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }


    }
}
