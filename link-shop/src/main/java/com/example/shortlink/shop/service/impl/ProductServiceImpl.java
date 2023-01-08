package com.example.shortlink.shop.service.impl;

import com.example.shortlink.shop.manager.ProductManager;
import com.example.shortlink.shop.model.ProductDo;
import com.example.shortlink.shop.mapper.ProductMapper;
import com.example.shortlink.shop.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 彭亮
 * @since 2023-01-08
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductManager productManager;

}
