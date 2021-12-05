package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.model.bo.Product;
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
        if(shopId!=0){
            return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有发布货品的权限");
        }
        return productDao.listProductsByFreightId(fid,pageNumber,pageSize) ;
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
            ReturnObject temp=productDao.alterProductStates(ret.getData(), (byte) Product.ProductState.OFFSHELF.getCode(),(byte) Product.ProductState.DRAFT.getCode());
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
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        if(!product.getShopId().equals(shopId))
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"该货品不属于该商铺");
        }
        ReturnObject ret=productDao.alterProductStates(product,(byte)Product.ProductState.ONSHELF.getCode(),(byte)Product.ProductState.OFFSHELF.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK);
        }else{
            return ret;
        }
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject offshelvesProduct(Long shopId,Long productId) {
        Product product = productDao.getProduct(productId);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        if(!product.getShopId().equals(shopId))
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"该货品不属于该商铺");
        }
        ReturnObject ret = productDao.alterProductStates(product, (byte) Product.ProductState.OFFSHELF.getCode(), (byte) Product.ProductState.ONSHELF.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK,"成功");
        }else
        {
            return ret;
        }
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject allowProduct(Long shopId,Long productId) {
        if(shopId!=0){
            return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有发布货品的权限");
        }
        Product product = productDao.getProduct(productId);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        ReturnObject ret = productDao.alterProductStates(product, (byte) Product.ProductState.OFFSHELF.getCode(), (byte) Product.ProductState.BANNED.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK,"成功");
        }else
        {
            return ret;
        }
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject prohibitProduct(Long shopId,Long productId)
    {
        if(shopId!=0){
            return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有发布货品的权限");
        }
        Product product= productDao.getProduct(productId);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        ReturnObject ret=productDao.alterProductStates(product,(byte)Product.ProductState.BANNED.getCode(),(byte)Product.ProductState.OFFSHELF.getCode(),(byte)Product.ProductState.ONSHELF.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK,"成功");
        }else
        {
            return ret;
        }
    }
}
