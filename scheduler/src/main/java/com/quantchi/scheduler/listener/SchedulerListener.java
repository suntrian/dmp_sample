package com.quantchi.scheduler.listener;

import com.quantchi.scheduler.mapper.SchedulerJobMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.listeners.SchedulerListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchedulerListener extends SchedulerListenerSupport {

    @Autowired
    private SchedulerJobMapper quartzJobMapper;

    @Override
    public void triggerFinalized(Trigger trigger) {
        // do nothing because nothing got through a trigger
    }



    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        log.info(msg);
    }
}
