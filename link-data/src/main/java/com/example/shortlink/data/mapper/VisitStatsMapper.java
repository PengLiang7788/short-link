package com.example.shortlink.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.shortlink.data.model.VisitStatsDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 彭亮
 * @create 2023-01-19 13:58
 */
public interface VisitStatsMapper extends BaseMapper<VisitStatsDo> {
    /**
     * 计算总条数
     *
     * @param code
     * @param accountNo
     * @return
     */
    int countTotal(@Param("code") String code, @Param("accountNo") long accountNo);

    /**
     * 分页查询
     *
     * @param code
     * @param accountNo
     * @param from
     * @param size
     * @return
     */
    List<VisitStatsDo> pageVisitRecord(@Param("code") String code, @Param("accountNo") long accountNo,
                                       @Param("from") int from, @Param("size") int size);

    /**
     * 根据时间范围查询地区访问分布
     *
     * @param code
     * @param startTime
     * @param endTime
     * @param accountNo
     * @return
     */
    List<VisitStatsDo> queryRegionVisitStartsWithDay(@Param("code") String code, @Param("startTime") String startTime,
                                                     @Param("endTime") String endTime, @Param("accountNo") long accountNo);

    /**
     * 查询时间范围内的访问趋势图 天级别
     *
     * @param code
     * @param accountNo
     * @param startTime
     * @param endTime
     * @return
     */
    List<VisitStatsDo> queryVisitTrendWithMultiDay(@Param("code") String code, @Param("accountNo") long accountNo,
                                                   @Param("startTime") String startTime, @Param("endTime") String endTime);


    /**
     * 查询时间范围内的访问趋势图 小时级别
     *
     * @param code
     * @param accountNo
     * @param startTime
     * @return
     */
    List<VisitStatsDo> queryVisitTrendWithMultiHour(@Param("code") String code, @Param("accountNo") long accountNo,
                                                    @Param("startTime") String startTime);

    /**
     * 查询时间范围内的访问趋势图 分钟级别
     *
     * @param code
     * @param accountNo
     * @param startTime
     * @param endTime
     * @return
     */
    List<VisitStatsDo> queryVisitTrendWithMultiMinute(@Param("code") String code, @Param("accountNo") long accountNo,
                                                      @Param("startTime") String startTime, @Param("endTime") String endTime);
}
