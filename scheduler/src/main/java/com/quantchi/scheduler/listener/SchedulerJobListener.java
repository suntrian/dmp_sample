package com.quantchi.scheduler.listener;

import com.quantchi.scheduler.entity.SchedulerJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchedulerJobListener extends JobListenerSupport {
    @Override
    public String getName() {
        return "QuartzJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {

    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        SchedulerJob job = (SchedulerJob) context.getJobDetail().getJobDataMap().get(SchedulerJob.JOB_KEY);
        log.debug("{} EXECUTION VETOED", job.getName());
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {

    }
}
