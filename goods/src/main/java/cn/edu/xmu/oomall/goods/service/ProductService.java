package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.microservice.ShopService;
import cn.edu.xmu.oomall.goods.microservice.vo.CategoryVo;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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

    @Autowired
    private ShopService shopService;

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
        Product product = productDao.getProduct(productId);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        if(!product.getShopId().equals(shopId))
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"该货品不属于该商铺");
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
        Product product= productDao.getProduct(productId);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        if(!product.getShopId().equals(shopId))
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"该货品不属于该商铺");
        }
        ReturnObject ret=productDao.alterProductStates(product,(byte)Product.ProductState.BANNED.getCode(),(byte)Product.ProductState.OFFSHELF.getCode(),(byte)Product.ProductState.ONSHELF.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK,"成功");
        }else
        {
            return ret;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject definitionFreight(Integer shopId, Integer productId, Integer fid) {
        Integer flag = productDao.updateProductFreight(shopId, productId, fid);
        return new ReturnObject(ReturnNo.OK,"成功");
    }
    @Transactional(readOnly = true)
    public ReturnObject secondProducts(Integer id, Integer page, Integer pageSize) {
        InternalReturnObject<CategoryVo> categoryById = shopService.getCategoryById(id);
        Integer errno = categoryById.getErrno();
        if(0 == errno){
            CategoryVo categoryVo = categoryById.getData();
            Long voId = categoryVo.getId();
            return Objects.isNull(voId)?new ReturnObject<>(ReturnNo.OK):
                    new ReturnObject<>(ReturnNo.OK,productDao.secondProducts(id,page,pageSize));
        }else {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"分类id不存在");
        }
    }
    @Transactional(readOnly = true)
    public ReturnObject secondShopProducts(Integer did, Integer cid, Integer page, Integer pageSize) {
        InternalReturnObject<CategoryVo> categoryById = shopService.getCategoryById(cid);
        Integer errno = categoryById.getErrno();
        if(0 == errno){
            CategoryVo categoryVo = categoryById.getData();
            Long voId = categoryVo.getId();
            return Objects.isNull(voId)?new ReturnObject<>(ReturnNo.OK):
                    new ReturnObject<>(ReturnNo.OK,productDao.secondShopProducts(did,cid,page,pageSize));
        }else {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"分类id不存在");
        }
    }
}
