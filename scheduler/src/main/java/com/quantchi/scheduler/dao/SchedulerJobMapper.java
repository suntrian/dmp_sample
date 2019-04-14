package com.quantchi.scheduler.dao;

import com.quantchi.scheduler.entity.SchedulerJob;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SchedulerJobMapper {

    Integer insert(SchedulerJob job);

    Integer update(SchedulerJob job);

    Integer delete(@Param("jobId") Integer id, @Param("userId") Integer userId);

    Integer deleteBatch(@Param("jobIds") Collection<Integer> ids, @Param("userId") Integer userId);

    SchedulerJob get(Integer id);

    List<SchedulerJob> list();

    List<SchedulerJob> listByIds(Collection<Integer> ids);

}
