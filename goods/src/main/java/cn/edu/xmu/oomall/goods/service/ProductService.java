package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.ImgHelper;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.microservice.FreightService;
import cn.edu.xmu.oomall.goods.microservice.ShopService;
import cn.edu.xmu.oomall.goods.microservice.vo.CategoryDetailRetVo;
import cn.edu.xmu.oomall.goods.microservice.vo.CategoryVo;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleCategoryVo;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleShopVo;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.po.OnSalePo;
import cn.edu.xmu.oomall.goods.model.po.ProductDraftPo;
import cn.edu.xmu.oomall.goods.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
/**
 * @author 王言光 22920192204292
 * @date 2021/12/7
 */
@Service
public class ProductService {
    @Autowired
    private ProductDao productDao;

    @Autowired
    private ShopService shopService;

    @Autowired
    private FreightService freightService;

    @Value("${productservice.webdav.username}")
    private String davUsername;

    @Value("${productservice.webdav.password}")
    private String davPassWord;

    @Value("${productservice.webdav.baseUrl}")
    private String baseUrl;

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
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        ReturnObject<Product> ret=productDao.publishById(productId);
        if(!ret.getCode().equals(ReturnNo.OK))
        {
            return ret;
        }
        Product product= ret.getData();
        ProductVo productVo=cloneVo(product,ProductVo.class);
        return new ReturnObject(productVo);
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
    @Transactional(readOnly = true)
    public ReturnObject<Object> getProductsOfCategories(Long did, Long cid, Integer page, Integer pageSize) {
        InternalReturnObject<List<CategoryVo>> categoryReturnObj = shopService.getSecondCategory(0L);
        Integer errno = categoryReturnObj.getErrno();
        if (errno != 0){
            return new ReturnObject<Object>(categoryReturnObj);
        }
        List<CategoryVo> categoryVos = categoryReturnObj.getData()
                .stream().filter(row-> row.getId().equals(cid))
                .collect(Collectors.toList());
        if (categoryVos.isEmpty()) {
           return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "分类id不存在");
        }
       return new ReturnObject<>(productDao.getProductsOfCategories(did, cid,page,pageSize));
    }

    /**
     * 获取商品的所有状态
     *
     * @param
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/10
     */
    public ReturnObject getProductStates(){ return productDao.getProductState(); }

    /**
     * 查询商品
     *
     * @param shopId,barCode,page,pageSize
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/11
     */
    @Transactional(readOnly=true)
    public ReturnObject getAllProducts(Long shopId, String barCode, Integer page, Integer pageSize) {
        return productDao.getAllProducts(shopId, barCode, page, pageSize);
    }

    /**
     * 获取product详细信息(非后台用户调用）
     *
     * @param productId
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    @Transactional(readOnly=true)
    public ReturnObject<ProductRetVo> getProductDetails(Long productId) {
        ReturnObject ret = productDao.getProductInfo(productId);
        if (ret.getCode() != ReturnNo.OK) {
            return ret;
        }
        Product product = (Product) ret.getData();
        OnSalePo onSalePo = productDao.getValidOnSale(productId);
        product.setOnSaleId(onSalePo.getId());
        product.setPrice(onSalePo.getPrice());
        product.setQuantity(onSalePo.getQuantity());
        //查找categoryName
        InternalReturnObject object = shopService.getCategoryDetailById(product.getCategoryId());
        if (!object.getErrno().equals(0)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleCategoryVo categoryVo = cloneVo(object.getData(),SimpleCategoryVo.class);
        product.setCategoryName(categoryVo.getName());
        ProductRetVo vo = (ProductRetVo) cloneVo(product, ProductRetVo.class);
        return new ReturnObject(vo);
    }

    /**
     * 将product添加到good中
     *
     * @param
     * @return ReturnObject
     * @author wyg
     * @date 2021/11/10
     */
    @Transactional(rollbackFor= Exception.class)
    public ReturnObject addProductToGood(Long shopId, ProductDetailVo productVo, Long loginUser, String loginUsername) {
        ProductDraftPo po = (ProductDraftPo) cloneVo(productVo, ProductDraftPo.class);
        po.setShopId(shopId);
        setPoCreatedFields(po,loginUser,loginUsername);

        ReturnObject ret = productDao.newProduct(po);

        Product product = (Product) ret.getData();

        //查找shopName
        InternalReturnObject object = shopService.getSimpleShopById(product.getShopId());
        if(!object.getErrno().equals(0)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleShopVo simpleShopVo = (SimpleShopVo) cloneVo(object.getData(),SimpleShopVo.class);
        product.setShopName(simpleShopVo.getName());
        //查找categoryName
        InternalReturnObject object1 = shopService.getCategoryById(product.getCategoryId());
        if(!object.getErrno().equals(0)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleCategoryVo categoryVo = (SimpleCategoryVo) object.getData();
        product.setCategoryName(categoryVo.getName());

        ProductNewReturnVo vo = (ProductNewReturnVo) cloneVo(product, ProductNewReturnVo.class);
        if (ret.getCode() != ReturnNo.OK) {
            return ret;
        }
        return new ReturnObject(vo);
    }

    /**
     * 上传货品图片
     *
     * @param shopId, id, multipartFile
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/10
     */
    @Transactional(rollbackFor= Exception.class)
    public ReturnObject upLoadProductImg(Long shopId, Long id, MultipartFile multipartFile, Long loginUser, String loginUsername) {
        ReturnObject ret = productDao.matchProductShop(id,shopId);
        if(ret.getCode()!=ReturnNo.OK){
            return ret;
        }
        Product product = productDao.getProduct(id);
        ReturnObject returnObject;

        try {
            returnObject = ImgHelper.remoteSaveImg(multipartFile, 2, davUsername, davPassWord, baseUrl);
            //文件上传错误
            if (returnObject.getCode() != ReturnNo.OK) {
                return returnObject;
            }

            //更新数据库
            String oldFilename = product.getImageUrl();
            Product updateProduct = new Product();
            updateProduct.setImageUrl(returnObject.getData().toString());
            updateProduct.setId(product.getId());
            updateProduct.setShopId(shopId);
            setPoModifiedFields(updateProduct,loginUser,loginUsername);

            ReturnObject updateReturnObject = productDao.addDraftProduct(updateProduct,loginUser,loginUsername);

            //数据库更新失败，需删除新增的图片
            if (updateReturnObject.getCode() == ReturnNo.FIELD_NOTVALID || updateReturnObject.getCode() == ReturnNo.INTERNAL_SERVER_ERR) {
                ImgHelper.deleteRemoteImg(returnObject.getData().toString(), davUsername, davPassWord, baseUrl);
                return updateReturnObject;
            }

            //数据库更新成功，删除原来的图片
            if (updateReturnObject.getCode() == ReturnNo.OK) {
                ImgHelper.deleteRemoteImg(oldFilename, davUsername, davPassWord, baseUrl);
                return updateReturnObject;
            }

        } catch (IOException e) {
            return new ReturnObject(ReturnNo.FILE_NO_WRITE_PERMISSION);
        }
        return returnObject;
    }

    /**
     * 物理删除Products
     *
     * @param shopId, id
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    @Transactional(rollbackFor= Exception.class)
    public ReturnObject deleteDraftProductById(Long shopId, Long id, Long loginUser, String loginUsername) {
        if(shopId!=0){
            ProductDraftPo productDraftPo = productDao.getProductDraft(id);
            if(productDraftPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(shopId.longValue()!=productDraftPo.getShopId()){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
        }
        ReturnObject ret = productDao.deleteDraftProductById(id);
        if(ret.getCode()!= ReturnNo.OK){
            return ret;
        }
        return ret;
    }

    /**
     * 添加DraftProduct
     *
     * @param shopId, id, productChangeVo
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    @Transactional(rollbackFor= Exception.class)
    public ReturnObject addDraftProduct(Long shopId, Long id, ProductChangeVo productChangeVo, Long loginUser, String loginUsername) {
        Product product = (Product) cloneVo(productChangeVo, Product.class);
        setPoModifiedFields(product,loginUser,loginUsername);
        product.setId(id);
        product.setShopId(shopId);
        ReturnObject ret = productDao.addDraftProduct(product,loginUser,loginUsername);
        return ret;
    }

    /**
     * 获取某一商铺的product详细信息（后台用户调用）
     *
     * @param shopId,productId
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    @Transactional(readOnly=true)
    public ReturnObject getShopProductDetails(Long shopId, Long productId, Long loginUser , String loginUsername) {
        ReturnObject ret = productDao.getProductInfo(productId);
        if (ret.getCode() != ReturnNo.OK) {
            return ret;
        }

        Product product = (Product) ret.getData();
        OnSalePo onSalePo = productDao.getValidOnSale(productId);
        product.setOnSaleId(onSalePo.getId());
        InternalReturnObject object = shopService.getCategoryById(product.getCategoryId());
        if(!object.getErrno().equals(0)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleCategoryVo categoryVo = (SimpleCategoryVo) object.getData();
        product.setCategoryName(categoryVo.getName());

        ProductShopRetVo vo = (ProductShopRetVo) cloneVo(product, ProductShopRetVo.class);
        return new ReturnObject(vo);
    }

    /**
     * 获取Good集合中的Product
     *
     * @param id
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/10
     */
    @Transactional(readOnly=true)
    public ReturnObject<GoodsRetVo> getGoodsProductById(Long id) {
        ReturnObject ret = productDao.getGoodsProductById(id);
        if(ret.getCode() != ReturnNo.OK){
            return ret;
        }
        GoodsRetVo vo = (GoodsRetVo)ret.getData();
        return new ReturnObject(vo);
    }

    /**
     * 将上线态的秒杀商品加载到Redis
     *
     * @param beginTime, endTime
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/16
     */
    @Transactional(readOnly = true)
    public Object loadSecondKillProduct(LocalDateTime beginTime, LocalDateTime endTime) {
            return productDao.loadSecondKillProduct(beginTime,endTime);
    }

    @Transactional(readOnly = true)
    public ReturnObject getFreightModels(Long shopId, Long id, Long loginUser, String loginUsername) {
        ReturnObject ret = productDao.matchProductShop(id,shopId);
        if(ret.getCode()!=ReturnNo.OK){
            return ret;
        }
        Product p = productDao.getProduct(id);
        if (p.getFreightId() != null) {
            return new ReturnObject(freightService.getFreightModel(shopId,p.getFreightId()).getData());
        } else {
            return new ReturnObject(freightService.getDefaultFreightModel(shopId).getData());
        }
    }

    @Transactional(rollbackFor= Exception.class)
    public ReturnObject changeFreightModels(Long shopId, Long id,Long fid, Long loginUser, String loginUsername) {
        Product p = new Product();
        p.setId(id);
        p.setShopId(shopId);
        p.setFreightId(fid);
        ReturnObject ret = productDao.addDraftProduct(p,loginUser,loginUsername);
        return ret;
    }

    /**
     * 根据productId获取种类利率
     * @author 李智樑
     */
    public ReturnObject getCommissionRateByProductId(Long id) {
        Product product = productDao.getProduct(id);

        if (product.getState().equals((byte)-1)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        CategoryDetailRetVo categoryRetVo =
                shopService.getCategoryDetailById(product.getCategoryId()).getData();

        return new ReturnObject(categoryRetVo.getCommissionRatio());
    }

}
