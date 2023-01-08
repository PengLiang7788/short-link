package com.example.shortlink.shop.db;

import com.example.shortlink.common.util.CommonUtil;
import com.example.shortlink.shop.manager.ProductOrderManager;
import com.example.shortlink.shop.model.ProductOrderDo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @author 彭亮
 * @create 2023-01-08 18:12
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ProductOrderTest {

    @Autowired
    private ProductOrderManager productOrderManager;

    @Test
    public void testAdd(){
        ProductOrderDo productOrderDo = ProductOrderDo.builder().outTradeNo(CommonUtil.generateUUID())
                .payAmount(new BigDecimal(11))
                .state("NEW")
                .nickname("苟始")
                .accountNo(10L)
                .productId(2L)
                .build();
        productOrderManager.add(productOrderDo);
    }

}
