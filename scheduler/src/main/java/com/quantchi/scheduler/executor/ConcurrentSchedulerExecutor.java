package com.quantchi.scheduler.executor;


import com.quantchi.scheduler.SchedulerConfiguration;
import com.quantchi.scheduler.entity.SchedulerJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class ConcurrentSchedulerExecutor implements Job {

    protected SchedulerJobExecutor executor;

    public static final ThreadLocal<SchedulerJob> jobThreadLocal = new ThreadLocal<>();

    public ConcurrentSchedulerExecutor() {
        this.executor = SchedulerConfiguration.getBean(SchedulerJobExecutor.class);
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
            execResult = e.getMessage();
            JobExecutionException exception = new JobExecutionException(e.getMessage());
            exception.setRefireImmediately(false);
            exception.setUnscheduleFiringTrigger(true);
            exception.setUnscheduleAllTriggers(true);
            throw exception;
        } finally {
            jobThreadLocal.remove();
            context.put("job", job);
            context.put("success", success);
            context.put("return", execResult);
        }
    }
}
