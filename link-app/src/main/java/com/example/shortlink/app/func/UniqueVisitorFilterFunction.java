package com.example.shortlink.app.func;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;

/**
 * @author 彭亮
 * @create 2023-01-18 16:59
 */
public class UniqueVisitorFilterFunction extends RichFilterFunction<JSONObject> {

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public boolean filter(JSONObject jsonObject) throws Exception {
        return false;
    }
}
