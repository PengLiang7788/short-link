package com.example.shortlink.link;

import com.example.shortlink.link.manager.DomainManager;
import com.example.shortlink.link.model.DomainDo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author 彭亮
 * @create 2023-01-06 14:22
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkServiceApplication.class)
@Slf4j
public class DomainTest {

    @Autowired
    private DomainManager domainManager;

    @Test
    public void testListDomain(){
        List<DomainDo> domainDos = domainManager.listOfficialDomain();
        log.info(domainDos.toString());
    }

}
