package com.quantchi.scheduler.dao;

import com.quantchi.scheduler.entity.SchedulerHistory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleHistoryMapper {

    Integer insert(SchedulerHistory history);

    List<SchedulerHistory> list(SchedulerHistory history);
}
