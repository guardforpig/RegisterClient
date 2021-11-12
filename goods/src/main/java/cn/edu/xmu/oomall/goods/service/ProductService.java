package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    private ProductDao productDao;

    public ReturnObject pulishProduct(Long productId)
    {
        return new ReturnObject(productDao.publishById(productId));
    }

    public ReturnObject onshelvesProduct(Long productId)
    {
        return new ReturnObject(productDao.onshelvesById(productId));
    }
    public ReturnObject offshelvesProduct(Long productId)
    {
        return new ReturnObject(productDao.offshelvesById(productId));
    }
    public ReturnObject allowProduct(Long productId)
    {
        return new ReturnObject(productDao.allowProductById(productId));
    }
    public ReturnObject prohibitProduct(Long productId)
    {
        return new ReturnObject(productDao.prohibitProductById(productId));
    }
}
