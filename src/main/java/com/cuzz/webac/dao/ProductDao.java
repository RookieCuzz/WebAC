package com.cuzz.webac.dao;

import com.cuzz.webac.mapper.ProductDOMapper;
import com.cuzz.webac.model.doo.ProductDO;
import com.cuzz.webac.model.doo.ProductDOExample;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ProductDao {

    @Resource
    ProductDOMapper productDOMapper;

    public List<ProductDO> getPageProduct(){


        // 创建一个空的 ProductExample 对象
        ProductDOExample example = new ProductDOExample();

        // 不设置任何查询条件，就相当于查询所有数据
        return productDOMapper.selectByExample(example);

    }



}
