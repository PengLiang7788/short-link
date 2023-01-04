package com.example.shortlink.link.component;

import com.example.shortlink.common.util.CommonUtil;
import com.example.shortlink.link.strategy.ShardingDBConfig;
import com.example.shortlink.link.strategy.ShardingTableConfig;
import org.springframework.stereotype.Component;

/**
 * @author 彭亮
 * @create 2023-01-03 18:38
 */
@Component
public class ShortLinkComponent {

    /**
     * 62个字符
     */
    private static final String CHARS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 生成短链码
     *
     * @param param
     * @return
     */
    public String createShortLinkCode(String param) {
        long murmurHash = CommonUtil.murmurHash32(param);
        //进制转换
        String code = encodeToBase62(murmurHash);

        String shortLinkCode = ShardingDBConfig.getRandomDBPrefix() + code + ShardingTableConfig.getRandomTableSuffix();

        return shortLinkCode;
    }

    /**
     * 10进制转换成62进制
     * @param num
     * @return
     */
    private String encodeToBase62(long num) {
        //StringBuffer线程安全，StringBuilder线程不安全
        StringBuffer sb = new StringBuffer();
        do {
            int i = (int) (num % 62);
            sb.append(CHARS.charAt(i));
            num = num / 62;
        } while (num > 0);
        String value = sb.reverse().toString();
        return value;
    }

}
