package com.quantchi.scheduler.executor;


import com.quantchi.scheduler.SchedulerConfiguration;
import com.quantchi.scheduler.dao.ScheduleHistoryMapper;
import com.quantchi.scheduler.entity.SchedulerHistory;
import com.quantchi.scheduler.entity.SchedulerJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
public class ConcurrentSchedulerExecutor implements Job {

    protected SchedulerJobExecutor executor;

    protected ScheduleHistoryMapper scheduleHistoryMapper;

    public static final ThreadLocal<SchedulerJob> jobThreadLocal = new ThreadLocal<>();

    public ConcurrentSchedulerExecutor() {
        this.executor = SchedulerConfiguration.getBean(SchedulerJobExecutor.class);
        this.scheduleHistoryMapper = SchedulerConfiguration.getBean(ScheduleHistoryMapper.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SchedulerJob job = (SchedulerJob) context.getJobDetail().getJobDataMap().get(SchedulerJob.JOB_KEY);
        jobThreadLocal.set(job);
        Object execResult = null;
        boolean success = true;
        try {
            execResult = executor.invoke(job);
        } catch (Exception e) {
            log.error("job " + job.getName() + " execute failed", e);
            success = false;
            JobExecutionException exception = new JobExecutionException(e.getMessage());
            exception.setRefireImmediately(false);
            exception.setUnscheduleFiringTrigger(true);
            exception.setUnscheduleAllTriggers(true);
            throw exception;
        } finally {
            jobThreadLocal.remove();
            Object result = context.getResult();
            Date nextFireTime = context.getNextFireTime();
            Long runTime = context.getJobRunTime();
            SchedulerHistory history = new SchedulerHistory();
            history.setJobId(job.getId());
            history.setJobName(job.getName());
            history.setSuccess(success);
            history.setStartTime(context.getFireTime());
            history.setFinishTime(new Date());
            //history.setRecord(execResult);
            history.setMessage(result == null ? null : result.toString());
            history.setNextFireTime(nextFireTime);
            scheduleHistoryMapper.insert(history);
        }
    }
}
