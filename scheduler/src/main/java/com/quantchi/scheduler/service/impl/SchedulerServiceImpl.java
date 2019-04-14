package com.quantchi.scheduler.service.impl;

import com.quantchi.scheduler.dao.SchedulerJobMapper;
import com.quantchi.scheduler.entity.SchedulerJob;
import com.quantchi.scheduler.exception.InvalidScheduleJobException;
import com.quantchi.scheduler.executor.SchedulerJobExecutor;
import com.quantchi.scheduler.executor.SchedulerManager;
import com.quantchi.scheduler.service.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service("quartzService")
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    private SchedulerJobMapper schedulerJobMapper;

    @Autowired
    private SchedulerManager schedulerManager;

    @Autowired
    private SchedulerJobExecutor schedulerJobExecutor;

    @Autowired
    private MessageSource messageSource;

    private Locale locale = LocaleContextHolder.getLocale();

    @PostConstruct
    public void initScheduleJob() {
        String initSchedule = System.getenv("initSchedule");
        if (initSchedule!=null && initSchedule.equalsIgnoreCase("false")){
            return;
        }
        initSchedule();
    }


    @Override
    public Integer insert(SchedulerJob job) throws SchedulerException {
        if (!SchedulerJobExecutor.checkJobValid(job)){
            throw new InvalidScheduleJobException(messageSource.getMessage("invalidJob", new String[]{job.getName()},
                locale));
        }
        Integer count = schedulerJobMapper.insert(job);
        if (count == 0) return count;
        if (job.getAutoStart() || SchedulerJob.STATUS_NONE.equals(job.getStatus())) {
            schedulerManager.addJob(job);
        }
        return count;
    }

    @Override
    public Integer delete(SchedulerJob job) throws SchedulerException {
        if (! SchedulerJob.STATUS_NONE.equals(schedulerManager.checkJobStatus(job))) {
            schedulerManager.deleteJob(job);
        }
        return schedulerJobMapper.delete(job.getId(), job.getDeletedBy());
    }

    @Override
    public Integer delete(Integer jobId) throws SchedulerException {
        SchedulerJob job = schedulerJobMapper.get(jobId);
        if (job == null) {
            return 0;
        }
        return delete(job);
    }

    @Override
    public Integer delete(List<Integer> ids) throws SchedulerException {
        List<SchedulerJob> jobs = list(ids);
        int c = 0;
        for (SchedulerJob job: jobs) {
            c += delete(job);
        }
        return c;
    }

    @Override
    public Integer update(SchedulerJob job) throws SchedulerException {
        if (job.getStatus() != null) {
            if (StringUtils.isBlank(job.getGroup()) && StringUtils.isBlank(job.getName()) && job.getId() != null){
                job = get(job.getId());
            }
            switch (job.getStatus()){
                case SchedulerJob.STATUS_NONE:
                    schedulerManager.deleteJob(job);
                case SchedulerJob.STATUS_PAUSED:
                    schedulerManager.pauseJob(job);
                case SchedulerJob.STATUS_NORMAL:
                    schedulerManager.addJob(job);
                default:
                    //do nothing for now
            }
        }
        return schedulerJobMapper.update(job);
    }

    @Override
    public SchedulerJob get(Integer id) {
        return schedulerJobMapper.get(id);
    }

    @Override
    public List<SchedulerJob> list() {
        return schedulerJobMapper.list();
    }

    @Override
    public List<SchedulerJob> list(List<Integer> ids) {
        return schedulerJobMapper.listByIds(ids);
    }

    @Override
    public void executeJobNow(Integer jobId) throws SchedulerException {
        SchedulerJob schedulerJob = get(jobId);
        if (schedulerJob == null){
            throw new IllegalArgumentException("不存在此任务");
        }
        schedulerManager.executeJob(schedulerJob);
    }

    @Override
    public void executeJobDirectly(Integer jobId) throws JobExecutionException {
        SchedulerJob schedulerJob = get(jobId);
        if (schedulerJob == null){
            throw new IllegalArgumentException("不存在此任务");
        }
        try {
            schedulerJobExecutor.invoke(schedulerJob);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startScheduleJob(Integer jobId) throws SchedulerException {
        SchedulerJob job = this.get(jobId);
        switch (schedulerManager.checkJobStatus(job)){
            case SchedulerJob.STATUS_NORMAL:
                return;
            case SchedulerJob.STATUS_ERROR:
            case SchedulerJob.STATUS_BLOCKED:
            case SchedulerJob.STATUS_COMPLETE:
            case SchedulerJob.STATUS_PAUSED:
                schedulerManager.addJob(job);
            default:
                //do nothing for now
        }
        job.setStatus(SchedulerJob.STATUS_NORMAL);
        update(job);
    }

    @Override
    public void stopScheduleJob(Integer jobId) throws SchedulerException {
        SchedulerJob job = this.get(jobId);
        schedulerManager.deleteJob(job);
        job.setStatus(SchedulerJob.STATUS_STOPPED);
        update(job);
    }

    @Override
    public void pauseScheduleJob(Integer jobId) throws SchedulerException {
        SchedulerJob job = this.get(jobId);
        schedulerManager.pauseJob(job);
        job.setStatus(SchedulerJob.STATUS_PAUSED);
        update(job);
    }

    @Override
    public void resumeScheduleJob(Integer jobId) throws SchedulerException {
        SchedulerJob job = this.get(jobId);
        schedulerManager.resumeJob(job);
        job.setStatus(SchedulerJob.STATUS_NORMAL);
        update(job);
    }

    @Override
    public void initSchedule() {
        List<SchedulerJob> jobs = this.list();
        for (SchedulerJob job: jobs){
            //不自动启动的和手动停止的任务，重启时不启动
            if (job.getAutoStart() == null || !job.getAutoStart()
                    || SchedulerJob.STATUS_COMPLETE.equals(job.getStatus())
                    || SchedulerJob.STATUS_NONE.equals(job.getStatus())
                    || SchedulerJob.STATUS_ERROR.equals(job.getStatus())
                    || SchedulerJob.STATUS_PAUSED.equals(job.getStatus())
                    || StringUtils.isBlank(job.getCron())
            ) {
                continue;
            }
            try {
                schedulerManager.addJob(job);
                //log.info("Add Schedule Job {}.{} Success",job.getGroup(),job.getName());
            } catch (SchedulerException e) {
                log.error("Add Schedule Job {}.{} Failed for {} ", job.getGroup(), job.getName(), e.getMessage());
            }
        }
    }
}
