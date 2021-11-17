package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Huang Tianyue
 * 2021.11.15
 **/
@Service
public class ProductService {
    @Autowired
    private ProductDao productDao;

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
