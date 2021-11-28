package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.vo.SimpleProductRetVo;
import cn.edu.xmu.oomall.goods.mapper.ProductPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
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

    public boolean hasExist(Long productId) {
        return null != productMapper.selectByPrimaryKey(productId);
    }



    public boolean matchProductShop(Long productId, Long shopId) {

        ProductPo productPo=productMapper.selectByPrimaryKey(productId);
        return shopId.equals(productPo.getShopId());
    }

    public Long getShopIdById(Long id){
        try{
            Product ret=(Product) redisUtil.get("p_"+id);
            if(null!=ret){
                return ret.getId();
            }

            ProductPo po= productMapper.selectByPrimaryKey(id);

            if(po == null) {
                return null;
            }
            Product pro=(Product)cloneVo(po,Product.class);
            redisUtil.set("p_"+pro.getId(),pro,productTimeout);

            return pro.getId();
        }
        catch(Exception e){
            return null;
        }


    }

    /**
     * 该方法用于频繁查询onsale详情的api中，故使用redis
     * @param id
     * @return 返回的是Product类型
     */
    public ReturnObject getProductInfo(Long id){
        try{
            Product product=(Product) redisUtil.get("p_"+id);
            if(null!=product){
                return new ReturnObject(product);
            }else {
                ProductPo productPo = productMapper.selectByPrimaryKey(id);
                if (productPo == null) {
                    return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
                } else {
                    Product pro=(Product) Common.cloneVo(productPo,Product.class);
                    redisUtil.set("p_"+id,pro,productTimeout);
                    return new ReturnObject(pro);
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }
}
