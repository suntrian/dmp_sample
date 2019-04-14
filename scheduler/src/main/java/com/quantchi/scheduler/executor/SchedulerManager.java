package com.quantchi.scheduler.executor;


import com.quantchi.scheduler.entity.SchedulerJob;
import com.quantchi.scheduler.exception.InvalidScheduleJobException;
import org.jetbrains.annotations.NotNull;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class SchedulerManager {

    private final Logger logger = LoggerFactory.getLogger(SchedulerManager.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private MessageSource messageSource;

    private Locale locale = LocaleContextHolder.getLocale();

    public void addJob(@NotNull SchedulerJob job) throws SchedulerException {
        if (!SchedulerJobExecutor.checkJobValid(job)){
            throw new InvalidScheduleJobException();
        }
        if (SchedulerJob.STATUS_NORMAL.equals(checkJobStatus(job))){
            return;
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getName(), job.getGroup());
        Trigger trigger = scheduler.getTrigger(triggerKey);
        if (null == trigger) {
            JobDetail jobDetail = buildJobDetail(job);
            trigger = buildTrigger(buildScheduler(job), job).build();
            scheduler.scheduleJob(jobDetail, trigger);
            logger.info(messageSource.getMessage("addScheduleJob", new String[]{job.getName(), job.getCron(),
                trigger.getNextFireTime().toString()}, locale));
        } else {
            trigger = buildTrigger(buildScheduler(job), job).build();
            scheduler.rescheduleJob(triggerKey, trigger);
        }
    }


    public JobDetail buildJobDetail(SchedulerJob job) {
        Class<? extends Job> clazz = (job.getConcurrent()!=null && job.getConcurrent())? ConcurrentSchedulerExecutor.class :
                NonConcurrentSchedulerExecutor.class;
        JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getName(), job.getGroup()).build();
        jobDetail.getJobDataMap().put(SchedulerJob.JOB_KEY, job);
        return jobDetail;
    }

    @SuppressWarnings("unchecked")
    private TriggerBuilder buildTrigger(ScheduleBuilder scheduleBuilder, SchedulerJob job) {
        TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(job.getName(), job.getGroup())
                .withSchedule(scheduleBuilder);
        if (job.getEffectTime() != null) triggerBuilder.startAt(job.getEffectTime());
        if (job.getExpireTime() != null) triggerBuilder.endAt(job.getExpireTime());
        return triggerBuilder;
    }

    private ScheduleBuilder buildScheduler(SchedulerJob job) {
        if (job.isSimpleJob()){
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
            simpleScheduleBuilder.withIntervalInMilliseconds(job.getInterval()).withRepeatCount(job.getRepeatCount());
            if (job.getMisfirePolicy() == null){
                return simpleScheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
            }
            switch (job.getMisfirePolicy()){
                case SchedulerJob.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY:
                    simpleScheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                    break;
                case SchedulerJob.MISFIRE_INSTRUCTION_FIRE_NOW:
                    simpleScheduleBuilder.withMisfireHandlingInstructionFireNow();
                    break;
                case SchedulerJob.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT:
                    simpleScheduleBuilder.withMisfireHandlingInstructionNextWithExistingCount();
                    break;
                case SchedulerJob.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT:
                    simpleScheduleBuilder.withMisfireHandlingInstructionNextWithRemainingCount();
                    break;
                case SchedulerJob.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT:
                    simpleScheduleBuilder.withMisfireHandlingInstructionNowWithExistingCount();
                    break;
                case SchedulerJob.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT:
                    simpleScheduleBuilder.withMisfireHandlingInstructionNowWithRemainingCount();
                    break;
                default:
                    simpleScheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();

            }
            return simpleScheduleBuilder;
        } else if (job.isCronJob()){
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCron());
            if (job.getMisfirePolicy()==null){
                return cronScheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
            }
            switch (job.getMisfirePolicy()){
                case SchedulerJob.MISFIRE_INSTRUCTION_DO_NOTHING:
                    cronScheduleBuilder.withMisfireHandlingInstructionDoNothing();
                    break;
                case SchedulerJob.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW:
                    cronScheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
                    break;
                case SchedulerJob.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY:
                    cronScheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                    break;
            }
            return cronScheduleBuilder;
        } else {
            throw new InvalidScheduleJobException(job.getCron());
        }
    }

    public void pauseJob(SchedulerJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
        scheduler.pauseJob(jobKey);
    }

    public void resumeJob(SchedulerJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
        scheduler.resumeJob(jobKey);
    }

    public void deleteJob(SchedulerJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
        scheduler.deleteJob(jobKey);
    }

    public String checkJobStatus(SchedulerJob job) throws SchedulerException {
        Trigger.TriggerState state = scheduler.getTriggerState(TriggerKey.triggerKey(job.getName(), job.getGroup()));
        return state.name();
    }

    public void executeJob(SchedulerJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
        switch (checkJobStatus(job)) {
            case SchedulerJob.STATUS_NONE:
                throw new SchedulerException(messageSource.getMessage("jobNotRunning",null,"job is not Running", locale));
            case SchedulerJob.STATUS_NORMAL:
            case SchedulerJob.STATUS_PAUSED:
            case SchedulerJob.STATUS_COMPLETE:
                scheduler.triggerJob(jobKey);
                break;
            case SchedulerJob.STATUS_ERROR:
            case SchedulerJob.STATUS_BLOCKED:
                break;
        }
    }

    public void updateJob(SchedulerJob job) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getName(), job.getGroup());
        Trigger trigger = buildTrigger(buildScheduler(job), job).build();
        scheduler.rescheduleJob(triggerKey, trigger);
    }

    public List<SchedulerJob> listAllJob() throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeySet = scheduler.getJobKeys(matcher);
        List<SchedulerJob> jobList = new ArrayList<>();
        for (JobKey jobKey: jobKeySet) {
            Object job = scheduler.getJobDetail(jobKey).getJobDataMap().get(SchedulerJob.JOB_KEY);
            if (job instanceof SchedulerJob){
                jobList.add((SchedulerJob) job);
            } else {
                for (Trigger trigger: scheduler.getTriggersOfJob(jobKey)){
                    jobList.add(parseTrigger2ScheduleJob(trigger));
                }
            }
        }
        return jobList;
    }

    public List<SchedulerJob> listRunningJob() throws SchedulerException {
        List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
        List<SchedulerJob> jobList = new ArrayList<>(executingJobs.size());
        for (JobExecutionContext executingJob : executingJobs) {
            Object job = executingJob.getJobDetail().getJobDataMap().get(SchedulerJob.JOB_KEY);
            if (job instanceof SchedulerJob){
                jobList.add((SchedulerJob) job);
            } else {
                jobList.add(parseTrigger2ScheduleJob(executingJob.getTrigger()));
            }
        }
        return jobList;
    }

    private SchedulerJob parseTrigger2ScheduleJob(Trigger trigger) throws SchedulerException {
        SchedulerJob job = new SchedulerJob();
        job.setName(trigger.getJobKey().getName());
        job.setGroup(trigger.getJobKey().getGroup());
        job.setStatus(scheduler.getTriggerState(trigger.getKey()).name());
        if (trigger instanceof CronTrigger) {
            job.setCron(((CronTrigger) trigger).getCronExpression());
        } else if (trigger instanceof SimpleTrigger){
            job.setInterval(((SimpleTrigger) trigger).getRepeatInterval());
            job.setRepeatCount(((SimpleTrigger) trigger).getRepeatCount());
            job.setCron(((SimpleTrigger) trigger).getRepeatInterval() + "*" + ((SimpleTrigger) trigger).getRepeatCount());
        }
        return job;
    }

}
