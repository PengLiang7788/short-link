package com.example.shortlink.shop.manager;

import com.example.shortlink.shop.model.ProductDo;

import java.util.List;

/**
 * @author 彭亮
 * @create 2023-01-08 14:44
 */
public interface ProductManager {
    /**
     * 查找全部商品
     * @return
     */
    List<ProductDo> list();

    /**
     * 查找商品详情
     * @param productId
     * @return
     */
    ProductDo findDetailById(Long productId);

}
