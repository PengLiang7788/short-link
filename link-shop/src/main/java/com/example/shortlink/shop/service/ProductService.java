package com.example.shortlink.shop.service;


import com.example.shortlink.shop.model.ProductDo;
import com.example.shortlink.shop.vo.ProductVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 彭亮
 * @since 2023-01-08
 */
public interface ProductService {

    /**
     * 查找商品列表
     * @return
     */
    List<ProductVo> list();

    /**
     * 查找商品详情
     * @param productId
     * @return
     */
    ProductVo findDetailById(Long productId);
}
