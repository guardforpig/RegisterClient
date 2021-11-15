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
    public ReturnObject pulishProduct(Long productId)
    {
        return new ReturnObject(productDao.publishById(productId));
    }

    @Transactional(rollbackFor=Exception.class)
    public ReturnObject onshelvesProduct(Long productId)
    {
        return new ReturnObject(productDao.onshelvesById(productId));
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject offshelvesProduct(Long productId)
    {
        return new ReturnObject(productDao.offshelvesById(productId));
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject allowProduct(Long productId)
    {
        return new ReturnObject(productDao.allowProductById(productId));
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject prohibitProduct(Long productId)
    {
        return new ReturnObject(productDao.prohibitProductById(productId));
    }
}
