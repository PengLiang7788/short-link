package com.example.link.account.db;

import com.example.shortlink.account.AccountServiceApplication;
import com.example.shortlink.account.manager.TrafficManager;
import com.example.shortlink.account.mapper.TrafficMapper;
import com.example.shortlink.account.model.TrafficDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;

/**
 * @author 彭亮
 * @create 2023-01-03 13:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
@Slf4j
public class TrafficTest {

    @Autowired
    private TrafficMapper trafficMapper;

    @Autowired
    private TrafficManager trafficManager;

    @Test
    public void testTraffic() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            TrafficDO trafficDO = new TrafficDO();
            trafficDO.setAccountNo(Long.valueOf(random.nextInt(100)));
            trafficMapper.insert(trafficDO);
        }
    }

    @Test
    public void testDelete() {
        trafficManager.deleteExpireTraffic();
    }

    @Test
    public void testSelectAvailable() {
        List<TrafficDO> trafficDOList =
                trafficManager.selectAvailableTraffics(820257672426160128L);
        trafficDOList.stream().forEach(obj -> {
            log.info(obj.toString());
        });

    }

    @Test
    public void testAddDayUsedTimes() {
        int rows = trafficManager.addDayUsedTimes(820257672426160128L, 1613380696984899585L, 1);
        log.info("rows={}", rows);
    }






}
