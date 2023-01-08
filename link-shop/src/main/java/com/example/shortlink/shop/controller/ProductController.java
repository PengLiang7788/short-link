package com.example.shortlink.shop.controller;


import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.shop.model.ProductDo;
import com.example.shortlink.shop.service.ProductService;
import com.example.shortlink.shop.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 彭亮
 * @since 2023-01-08
 */
@RestController
@RequestMapping("/api/product/v1")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 查找商品列表
     *
     * @return
     */
    @GetMapping("/list")
    public JsonData list() {
        List<ProductVo> list = productService.list();
        return JsonData.buildSuccess(list);
    }

    /**
     * 查找商品详情
     * @param productId
     * @return
     */
    @GetMapping("/detail/{productId}")
    public JsonData detail(@PathVariable("productId") Long productId) {
        ProductVo productVo = productService.findDetailById(productId);
        return JsonData.buildSuccess(productVo);
    }

}

