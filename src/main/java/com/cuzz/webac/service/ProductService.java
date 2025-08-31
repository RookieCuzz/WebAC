package com.cuzz.webac.service;


import com.cuzz.webac.dao.ProductDao;
import com.cuzz.webac.model.doo.ProductDO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {


    @Resource
    ProductDao productDao;


    public PageInfo<ProductDO> getPageProduct(int pageNum, int pageSize) {
        // 使用 PageHelper 启动分页
        PageHelper.startPage(pageNum, pageSize);
        List<ProductDO> pageProduct = productDao.getPageProduct();

        return new PageInfo<>(pageProduct);
    }


}
