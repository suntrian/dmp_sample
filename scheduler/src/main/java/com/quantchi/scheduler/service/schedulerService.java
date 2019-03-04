package com.quantchi.scheduler.service;

import com.quantchi.scheduler.entity.SchedulerJob;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import java.util.List;
import java.util.Map;

public interface schedulerService {

    Integer insert(SchedulerJob job) throws SchedulerException;
    Integer delete(SchedulerJob job) throws SchedulerException;
    Integer delete(Integer jobId) throws SchedulerException;
    Integer delete(List<Integer> ids) throws SchedulerException;
    Integer update(SchedulerJob job) throws SchedulerException;
    SchedulerJob get(Integer id);
    List<SchedulerJob> list();
    List<SchedulerJob> list(List<Integer> ids);
    void executeJobNow(Integer jobId) throws SchedulerException;
    void executeJobDirectly(Integer jobId) throws JobExecutionException;
    void startScheduleJob(Integer jobId) throws SchedulerException;
    void stopScheduleJob(Integer jobId) throws SchedulerException;
    void pauseScheduleJob(Integer jobId) throws SchedulerException;
    void resumeScheduleJob(Integer jobId) throws SchedulerException;

    void initSchedule() throws SchedulerException;
}
