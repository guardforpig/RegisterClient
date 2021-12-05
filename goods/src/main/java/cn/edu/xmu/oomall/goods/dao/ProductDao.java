package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.mapper.ProductDraftPoMapper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import cn.edu.xmu.oomall.goods.mapper.ProductPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.po.ProductDraftPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPo;
import cn.edu.xmu.oomall.goods.model.po.ProductPoExample;
import cn.edu.xmu.oomall.goods.model.vo.ProductVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author yujie lin
 * @date 2021/11/11
 */
/**
 * @author 黄添悦
 * @date 2021/11/25
 **/
/**
 * @author 王文飞
 * @date 2021/11/25
 */
@Repository
public class ProductDao {
    private Logger logger = LoggerFactory.getLogger(OnSaleDao.class);
    private final static String PRODUCT_ID="p_%d";

    @Autowired
    private ProductPoMapper productMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${oomall.goods.product.expiretime}")
    private long productTimeout;




    @Autowired
    private ProductDraftPoMapper productDraftPoMapper;

    public final static String GOODSKEY="goods_%d";

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

    /**
     * 该方法用于频繁查询onsale详情的api中，故使用redis
     * @author Zijun Min 22920192204257
     * @param id
     * @return 返回的是Product类型
     */
    public ReturnObject getProductInfo(Long id){
        try{
            String  key = String.format(PRODUCT_ID,id);
            Product product=(Product) redisUtil.get(key);
            if(null!=product){
                return new ReturnObject(product);
            }else {
                ProductPo productPo = productMapper.selectByPrimaryKey(id);
                if (productPo == null) {
                    return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
                } else {
                    Product pro=(Product) cloneVo(productPo,Product.class);
                    redisUtil.set(key,pro,productTimeout);
                    return new ReturnObject(pro);
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }
    /**
     * @author 黄添悦
     * @date 2021/11/25
     **/
    /**
     * @author 王文飞
     * @date 2021/11/25
     */
    public ReturnObject listProductsByFreightId(Long fid, Integer pageNumber, Integer pageSize)
    {
        try {
            PageHelper.startPage(pageNumber, pageSize, true, true, true);
            ProductPoExample productPoExample = new ProductPoExample();
            ProductPoExample.Criteria cr = productPoExample.createCriteria();
            cr.andFreightIdEqualTo(fid);
            List<ProductPo> products = productMapper.selectByExample(productPoExample);
            if(products.size()==0)
            {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            ReturnObject<PageInfo<Object>> ret=new ReturnObject(new PageInfo<ProductPo>(products));
            return Common.getPageRetVo(ret,ProductVo.class);
        }catch(Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }

    }
    public ReturnObject alterProductStates(Product product,Byte targetState,Byte... states){
        try{
            ProductPo productPo=new ProductPo();
            productPo.setId(product.getId());
            productPo.setState(product.getState());
            boolean ifValid=false;
            for(Byte state:states){
                if(productPo.getState().equals(state)) {
                    ifValid=true;
                }
            }if(ifValid){
                productPo.setState(targetState);
                productMapper.updateByPrimaryKeySelective(productPo);
                return new ReturnObject(productPo);
            }else{
                return new ReturnObject(ReturnNo.STATENOTALLOW,"当前货品状态不支持进行该操作");
            }
        }catch(Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }
    /**
     * @author 黄添悦
     * @date 2021/11/25
     **/
    /**
     * @author 王文飞
     * @date 2021/11/25
     */
    public ReturnObject<Product> publishById(Long id) {
        try {
            ProductDraftPo productDraftPo = productDraftPoMapper.selectByPrimaryKey(id);
            ProductPo productPo = (ProductPo) cloneVo(productDraftPo, ProductPo.class);
            if (productDraftPo.getProductId() == 0) {
                productPo.setId(null);
                productMapper.insert(productPo);
                productPo.setState((byte)1);
            } else {
                productPo.setId(productDraftPo.getProductId());
                productMapper.updateByPrimaryKey(productPo);
                productPo.setState((byte)1);
            }
            String key = String.format(GOODSKEY, productPo.getGoodsId());
            redisUtil.del(key);
            productDraftPoMapper.deleteByPrimaryKey(id);
            return new ReturnObject<Product>((Product) cloneVo(productPo, Product.class));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
    public List<Product> getProductsByGoodsId(Long id){
        ProductPoExample productPoExample = new ProductPoExample();
        ProductPoExample.Criteria cr = productPoExample.createCriteria();
        cr.andGoodsIdEqualTo(id);
        List<ProductPo> products = productMapper.selectByExample(productPoExample);
        List<Product> productList = new ArrayList<>(products.size());
        for (ProductPo productPo : products) {
            productList.add((Product) cloneVo(productPo, Product.class));
        }
        return productList;
    }
    public int resetGoodsIdForProducts(Long id,Long newId){
        ProductPoExample productPoExample = new ProductPoExample();
        ProductPoExample.Criteria cr = productPoExample.createCriteria();
        cr.andGoodsIdEqualTo(id);
        ProductPo productPo=new ProductPo();
        productPo.setGoodsId(newId);
        return productMapper.updateByExampleSelective(productPo,productPoExample);
    }
    public Product getProduct(Long id) {
        ProductPo productPo = productMapper.selectByPrimaryKey(id);
        if (productPo != null) {
            return (Product) cloneVo(productPo, Product.class);
        }
        Product product=new Product();
        product.setState((byte)-1);
        return product;
    }
    public ProductDraftPo getProductDraft(Long id){
        return productDraftPoMapper.selectByPrimaryKey(id);
    }

    public PageInfo<ProductPo> getProductsOfCategories(Integer did, Integer cid, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        ProductPoExample example = new ProductPoExample();
        ProductPoExample.Criteria criteria=example.createCriteria()
                .andCategoryIdEqualTo(Long.parseLong(String.valueOf(cid)));
        if (Objects.nonNull(did)){
            criteria.andShopIdEqualTo(Long.parseLong(String.valueOf(did)));
        }else{
            criteria.andStateEqualTo((byte)(Product.ProductState.ONSHELF.getCode()));
        }
        List<ProductPo> productPos = productMapper.selectByExample(example);
        return new PageInfo<>(productPos);
    }
}
