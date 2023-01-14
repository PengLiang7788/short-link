package com.example.link.account.db;

import com.example.shortlink.common.util.TimeUtil;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 彭亮
 * @create 2023-01-14 18:52
 */
public class MyTest {

    @Test
    public void testMyDb() throws ParseException {
        String today = TimeUtil.format(new Date(), "yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = sdf.parse(today);
//        Date date = new Date(today);
        System.out.println(parse.toString());
    }
}
