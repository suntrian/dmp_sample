package com.quantchi.scheduler.executor;


import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@SuppressWarnings("Duplicates")
@Component
@DisallowConcurrentExecution
public class NonConcurrentSchedulerExecutor extends ConcurrentSchedulerExecutor {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        super.execute(context);
    }
}
