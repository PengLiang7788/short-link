package com.example.shortlink.shop.manager.impl;

import com.example.shortlink.shop.manager.ProductManager;
import com.example.shortlink.shop.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 彭亮
 * @create 2023-01-08 14:44
 */
@Component
@Slf4j
public class ProductManagerImpl implements ProductManager {

    @Autowired
    private ProductMapper productMapper;

}
