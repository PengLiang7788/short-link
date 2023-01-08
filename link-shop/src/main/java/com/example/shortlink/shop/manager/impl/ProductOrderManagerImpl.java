package com.example.shortlink.shop.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shortlink.shop.manager.ProductOrderManager;
import com.example.shortlink.shop.mapper.ProductOrderMapper;
import com.example.shortlink.shop.model.ProductOrderDo;
import com.example.shortlink.shop.vo.ProductOrderVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 彭亮
 * @create 2023-01-08 17:13
 */
@Component
@Slf4j
public class ProductOrderManagerImpl implements ProductOrderManager {

    @Autowired
    private ProductOrderMapper productOrderMapper;

    /**
     * 新增
     *
     * @param productOrderDo
     * @return
     */
    @Override
    public int add(ProductOrderDo productOrderDo) {
        return productOrderMapper.insert(productOrderDo);
    }

    /**
     * 通过订单号和账号查询
     *
     * @param outTraderNo
     * @param accountNo
     * @return
     */
    @Override
    public ProductOrderDo findByOutTradeNoAndAccountNo(String outTraderNo, Long accountNo) {
        ProductOrderDo productOrderDo = productOrderMapper.selectOne(new LambdaQueryWrapper<ProductOrderDo>()
                .eq(ProductOrderDo::getAccountNo, accountNo)
                .eq(ProductOrderDo::getOutTradeNo, outTraderNo)
                .eq(ProductOrderDo::getDel, 0));
        return productOrderDo;
    }

    /**
     * 更新订单状态
     *
     * @param outTraderNo
     * @param accountNo
     * @param newState
     * @param oldState
     * @return
     */
    @Override
    public int updateOrderPayState(String outTraderNo, Long accountNo, String newState, String oldState) {
        int rows = productOrderMapper.update(null, new LambdaUpdateWrapper<ProductOrderDo>()
                .eq(ProductOrderDo::getOutTradeNo, outTraderNo)
                .eq(ProductOrderDo::getAccountNo, accountNo)
                .eq(ProductOrderDo::getDel, 0)
                .eq(ProductOrderDo::getState, oldState)
                .set(ProductOrderDo::getState, newState));
        return rows;
    }

    /**
     * 分页查找订单列表
     *
     * @param page
     * @param size
     * @param accountNo
     * @param state
     * @return
     */
    @Override
    public Map<String, Object> page(int page, int size, Long accountNo, String state) {
        Page<ProductOrderDo> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<ProductOrderDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductOrderDo::getAccountNo, accountNo);
        if (StringUtils.isNotEmpty(state)) {
            wrapper.eq(ProductOrderDo::getState, state);
        }
        Page<ProductOrderDo> orderDoPage = productOrderMapper.selectPage(pageInfo, wrapper);

        List<ProductOrderDo> productOrderDoList = orderDoPage.getRecords();

        List<ProductOrderVo> productOrderVos = productOrderDoList.stream().map(item -> {
            ProductOrderVo productOrderVo = new ProductOrderVo();
            BeanUtils.copyProperties(item, productOrderVo);
            return productOrderVo;
        }).collect(Collectors.toList());

        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("total_record", orderDoPage.getTotal());
        pageMap.put("total_page", orderDoPage.getPages());
        pageMap.put("current_data", productOrderVos);

        return pageMap;
    }
}
