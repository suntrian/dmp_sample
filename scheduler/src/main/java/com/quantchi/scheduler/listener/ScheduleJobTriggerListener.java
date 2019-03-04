package com.quantchi.scheduler.listener;

import com.quantchi.scheduler.entity.SchedulerJob;
import com.quantchi.scheduler.mapper.SchedulerJobMapper;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("scheduleJobTriggerListener")
public class ScheduleJobTriggerListener extends TriggerListenerSupport {
    @Autowired
    private SchedulerJobMapper quartzJobMapper;

    @Override
    public String getName() {
        return "QuartzTriggerListener";
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        if (triggerInstructionCode.equals(Trigger.CompletedExecutionInstruction.DELETE_TRIGGER)){
            SchedulerJob job = (SchedulerJob) context.getMergedJobDataMap().get(SchedulerJob.JOB_KEY);
            if (job.getId()==null){return;}
            if (StringUtils.isNotBlank(job.getCron()) && trigger instanceof SimpleTrigger){
              //cron任务直接执行时会生成SimpleTrigger来触发，从而在此将错误地将任务标为结束状态
              return;
            }
            job.setStatus(SchedulerJob.STATUS_COMPLETE);
            quartzJobMapper.updateByEntity(job);
        } else if (Trigger.CompletedExecutionInstruction.SET_TRIGGER_ERROR.equals(triggerInstructionCode)) {
            SchedulerJob job = (SchedulerJob) context.getMergedJobDataMap().get(SchedulerJob.JOB_KEY);
            if (job.getId()==null){return;}
            job.setStatus(SchedulerJob.STATUS_ERROR);
            quartzJobMapper.updateByEntity(job);
        }
    }


}
