package com.example.shortlink.app.func;

import com.alibaba.fastjson.JSONObject;
import com.example.shortlink.app.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;

/**
 * @author 彭亮
 * @create 2023-01-18 16:59
 */
public class UniqueVisitorFilterFunction extends RichFilterFunction<JSONObject> {

    private ValueState<String> lastVisitDateState = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<String> visitDateStateDes =
                new ValueStateDescriptor<>("visitDateState", String.class);

        // 统计UV，配置过期时间
        StateTtlConfig stateTtlConfig = StateTtlConfig.newBuilder(Time.days(1)).build();
//        StateTtlConfig stateTtlConfig = StateTtlConfig.newBuilder(Time.seconds(15)).build();

        visitDateStateDes.enableTimeToLive(stateTtlConfig);

        lastVisitDateState = getRuntimeContext().getState(visitDateStateDes);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public boolean filter(JSONObject jsonObject) throws Exception {

        // 获取当前访问时间
        Long visitTime = jsonObject.getLong("visitTime");
        String udid = jsonObject.getString("udid");
        // 当前访问时间 yyyy-MM-dd
        String currentVisitDate = TimeUtil.format(visitTime);

        // 获取上次的状态存储访问时间
        String lastVisitDate = lastVisitDateState.value();
        // 上一次访问时间不为空，并且和这次访问时间是同一天，说明不是今天初次访问，不增加日活数
        if (StringUtils.isNotBlank(lastVisitDate) && currentVisitDate.equalsIgnoreCase(lastVisitDate)) {
            System.out.println(udid + " 已经在 " + currentVisitDate + " 时间访问过");
            return false;
        } else {
            System.out.println(udid + "在" + currentVisitDate + "时间初次访问");
            lastVisitDateState.update(currentVisitDate);
            return true;
        }

    }
}
