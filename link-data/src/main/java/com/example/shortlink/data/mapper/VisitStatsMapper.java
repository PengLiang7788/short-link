package com.example.shortlink.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.shortlink.data.model.VisitStatsDo;
import com.example.shortlink.data.vo.VisitStatsVo;
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
}
