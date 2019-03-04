package com.quantchi.scheduler.mapper;

import com.quantchi.scheduler.entity.SchedulerJob;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("quartzJobMapper")
public interface SchedulerJobMapper {

    Integer insertByEntity(SchedulerJob job);
    Integer updateByEntity(SchedulerJob job);
    Integer deleteById(Integer id);
    Integer deleteByIdList(List<Integer> ids);
    SchedulerJob selectOneById(Integer id);
    List<SchedulerJob> selectAll();
    List<SchedulerJob> selectByBeanNameAndParam(Map<String, Object> param);
    List<SchedulerJob> selectListByIdList(List<Integer> ids);
    List<SchedulerJob> selectByJobHisStartDateAndEndDate(Map<String, Object> param);
    List<SchedulerJob> listByArgument(Integer jobId);
}
