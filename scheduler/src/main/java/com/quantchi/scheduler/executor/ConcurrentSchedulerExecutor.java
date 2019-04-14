package com.quantchi.scheduler.executor;


import com.quantchi.scheduler.dao.ScheduleHistoryMapper;
import com.quantchi.scheduler.entity.SchedulerHistory;
import com.quantchi.scheduler.entity.SchedulerJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class ConcurrentSchedulerExecutor implements Job {

    @Autowired
    protected SchedulerJobExecutor executor;

    @Autowired
    protected ScheduleHistoryMapper scheduleHistoryMapper;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SchedulerJob job = (SchedulerJob) context.getJobDetail().getJobDataMap().get(SchedulerJob.JOB_KEY);
        Object execResult = null;
        boolean success = true;
        try {
            execResult = executor.invoke(job);
        } catch (Exception e) {
            success = false;
            JobExecutionException exception = new JobExecutionException(e.getMessage());
            exception.setRefireImmediately(false);
            exception.setUnscheduleFiringTrigger(true);
            exception.setUnscheduleAllTriggers(true);
            throw exception;
        } finally {

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
