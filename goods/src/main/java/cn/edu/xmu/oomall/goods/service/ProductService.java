package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
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
    public ReturnObject pulishProduct(Long shopId,Long productId)
    {
        return productDao.publishById(shopId,productId);
    }

    @Transactional(rollbackFor=Exception.class)
    public ReturnObject onshelvesProduct(Long shopId,Long productId)
    {
        return new ReturnObject(productDao.onshelvesById(shopId,productId));
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject offshelvesProduct(Long shopId,Long productId)
    {
        return new ReturnObject(productDao.offshelvesById(shopId,productId));
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject allowProduct(Long shopId,Long productId)
    {
        return new ReturnObject(productDao.allowProductById(shopId,productId));
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject prohibitProduct(Long shopId,Long productId)
    {
        return new ReturnObject(productDao.prohibitProductById(shopId,productId));
    }
}
