package com.example.shortlink.shop.service.impl;

import com.example.shortlink.shop.manager.ProductManager;
import com.example.shortlink.shop.model.ProductDo;
import com.example.shortlink.shop.mapper.ProductMapper;
import com.example.shortlink.shop.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shortlink.shop.vo.ProductVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
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

    /**
     * 查找商品列表
     *
     * @return
     */
    @Override
    public List<ProductVo> list() {
        List<ProductDo> list = productManager.list();
        List<ProductVo> productVos = list.stream().map(item -> {
            ProductVo productVo = new ProductVo();
            BeanUtils.copyProperties(item, productVo);
            return productVo;
        }).collect(Collectors.toList());
        return productVos;
    }

    /**
     * 查找商品详情
     *
     * @param productId
     * @return
     */
    @Override
    public ProductVo findDetailById(Long productId) {
        ProductDo productDo = productManager.findDetailById(productId);
        ProductVo productVo = new ProductVo();
        BeanUtils.copyProperties(productDo, productVo);
        return productVo;
    }
}
