package com.quantchi.scheduler.executor;


import com.quantchi.scheduler.entity.ScheduleHistory;
import com.quantchi.scheduler.entity.SchedulerJob;
import com.quantchi.scheduler.mapper.ScheduleHistoryMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@SuppressWarnings("Duplicates")
@Component
public class ConcurrentSchedulerExecutor implements Job {

    @Autowired
    protected SchedulerJobExecutor executor;

    @Autowired
    protected ScheduleHistoryMapper scheduleHistoryMapper;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SchedulerJob job = (SchedulerJob) context.getJobDetail().getJobDataMap().get(SchedulerJob.JOB_KEY);
        ScheduleHistory history = new ScheduleHistory();
        history.setJobId(job.getId());
        history.setJobName(job.getName());
        try {
            history.setStartTime(new Date());
            Object result = executor.invoke(job);
            history.setEndTime(new Date());
            history.setRecord(result);
            history.setSuccess(true);
        } catch (Exception e) {
            history.setSuccess(false);
            history.setMessage(e.getMessage());
            history.setEndTime(new Date());

            JobExecutionException exception = new JobExecutionException(e.getMessage());
            exception.setRefireImmediately(false);
            exception.setUnscheduleFiringTrigger(true);
            exception.setUnscheduleAllTriggers(true);
            throw exception;
        } finally {
            Object result = context.getResult();
            Date nextFireTime = context.getNextFireTime();
            Long runTime = context.getJobRunTime();
            Date fireTime = context.getFireTime();
            scheduleHistoryMapper.insert(history);
        }
    }
}
