package com.quantchi.scheduler.listener;

import com.quantchi.scheduler.dao.ScheduleHistoryMapper;
import com.quantchi.scheduler.dao.SchedulerJobMapper;
import com.quantchi.scheduler.entity.SchedulerHistory;
import com.quantchi.scheduler.entity.SchedulerJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component("scheduleJobTriggerListener")
public class SchedulerJobTriggerListener extends TriggerListenerSupport {
    @Autowired
    private SchedulerJobMapper schedulerJobMapper;

    @Autowired
    private ScheduleHistoryMapper scheduleHistoryMapper;

    @Override
    public String getName() {
        return "QuartzTriggerListener";
    }

    /**
     * trigger被触发时开始时调用
     *
     * @param trigger trigger
     * @param context context
     */
    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        SchedulerJob job = (SchedulerJob) context.getJobDetail().getJobDataMap().get(SchedulerJob.JOB_KEY);
        log.info("{} fired", job.getName());
    }

    /**
     * trigger被触发完成时开始时调用
     *
     * @param trigger                trigger
     * @param context                context
     * @param triggerInstructionCode 完成状态
     */
    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        Object result = context.get("return");
        SchedulerJob job = (SchedulerJob) context.get("job");
        Boolean success = (Boolean) context.get("success");

        SchedulerHistory history = new SchedulerHistory();
        history.setJobId(job.getId());
        history.setJobName(job.getName());
        history.setSuccess(success);
        history.setStartTime(context.getFireTime());
        history.setFinishTime(new Date());
        history.setMessage(result == null ? null : result.toString());
        history.setNextFireTime(context.getNextFireTime());
        scheduleHistoryMapper.insert(history);

//        if (triggerInstructionCode.equals(Trigger.CompletedExecutionInstruction.DELETE_TRIGGER)){
//            SchedulerJob job = (SchedulerJob) context.getMergedJobDataMap().get(SchedulerJob.JOB_KEY);
//            if (job.getId()==null){return;}
//            if (StringUtils.isNotBlank(job.getCron()) && trigger instanceof SimpleTrigger){
//              //cron任务直接执行时会生成SimpleTrigger来触发，从而在此将错误地将任务标为结束状态
//              return;
//            }
//            job.setStatus(SchedulerJob.STATUS_COMPLETE);
//            schedulerJobMapper.update(job);
//        } else if (Trigger.CompletedExecutionInstruction.SET_TRIGGER_ERROR.equals(triggerInstructionCode)) {
//            SchedulerJob job = (SchedulerJob) context.getMergedJobDataMap().get(SchedulerJob.JOB_KEY);
//            if (job.getId()==null){return;}
//            job.setStatus(SchedulerJob.STATUS_ERROR);
//            schedulerJobMapper.update(job);
//        }
    }


}
