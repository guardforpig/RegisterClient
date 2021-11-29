package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.po.ProductDraftPo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
@Service
public class ProductService {
    @Autowired
    private ProductDao productDao;

    @Transactional(readOnly = true,rollbackFor=Exception.class)
    public ReturnObject listProductsByFreightId(Long shopId,Long fid,Integer pageNumber, Integer pageSize)
    {
        return productDao.listProductsByFreightId(shopId,fid,pageNumber,pageSize) ;
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject publishProduct(Long shopId,Long productId)
    {
        if(shopId!=0){
            return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有发布货品的权限");
        }
        if(productDao.getProductDraft(productId)==null){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"货品草稿不存在");
        }
        ReturnObject<Product> ret=productDao.publishById(productId);
        if(ret.getData()!=null){
            ReturnObject temp=productDao.alterProductStates(ret.getData(), (byte) Product.ProductState.OFFSHELF.getCode(),(byte) Product.ProductState.WAIT_FOR_AUDIT.getCode());
            if(temp.getData()!=null){
                return new ReturnObject(ReturnNo.OK);
            }else{
                return temp;
            }
        }
        else{
            return ret;
        }
    }

    @Transactional(rollbackFor=Exception.class)
    public ReturnObject onshelvesProduct(Long shopId,Long productId)
    {
        Product product= productDao.getProduct(productId);
        if(product==null){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"货品id不存在");
        }
        if(shopId!=0&&!product.getShopId().equals(shopId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有对该商品进行上架的权限");
        }
        ReturnObject ret=productDao.alterProductStates(product,(byte)Product.ProductState.ONSHELF.getCode(),(byte)Product.ProductState.OFFSHELF.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK);
        }else{
            return ret;
        }
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject offshelvesProduct(Long shopId,Long productId)
    {
        Product product= productDao.getProduct(productId);
        if(product==null){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"货品id不存在");
        }
        if(shopId!=0&&product.getShopId().equals(shopId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有对该商品进行下架的权限");
        }
        return productDao.alterProductStates(product,(byte)Product.ProductState.OFFSHELF.getCode(),(byte)Product.ProductState.ONSHELF.getCode());
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject allowProduct(Long shopId,Long productId)
    {
        Product product= productDao.getProduct(productId);
        if(product==null){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"货品id不存在");
        }
        if(shopId!=0&&product.getShopId().equals(shopId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有对该商品进行解禁的权限");
        }
        return productDao.alterProductStates(product,(byte)Product.ProductState.OFFSHELF.getCode(),(byte)Product.ProductState.BANNED.getCode());
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject prohibitProduct(Long shopId,Long productId)
    {
        Product product= productDao.getProduct(productId);
        if(product==null){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"货品id不存在");
        }
        if(shopId!=0&&product.getShopId().equals(shopId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有对该商品进行禁售的权限");
        }
        return productDao.alterProductStates(product,(byte)Product.ProductState.BANNED.getCode(),(byte)Product.ProductState.OFFSHELF.getCode(),(byte)Product.ProductState.ONSHELF.getCode());
    }
}
