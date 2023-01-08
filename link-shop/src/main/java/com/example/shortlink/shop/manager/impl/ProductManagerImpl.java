package com.example.shortlink.shop.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shortlink.shop.manager.ProductManager;
import com.example.shortlink.shop.mapper.ProductMapper;
import com.example.shortlink.shop.model.ProductDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 彭亮
 * @create 2023-01-08 14:44
 */
@Component
@Slf4j
public class ProductManagerImpl implements ProductManager {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 查找全部商品
     * @return
     */
    @Override
    public List<ProductDo> list() {
        List<ProductDo> productDos = productMapper.selectList(null);
        return productDos;
    }

    /**
     * 查找商品详情
     * @param productId
     * @return
     */
    @Override
    public ProductDo findDetailById(Long productId) {
        ProductDo productDo = productMapper.selectOne(new LambdaQueryWrapper<ProductDo>()
                .eq(ProductDo::getId, productId));
        return productDo;
    }
}
