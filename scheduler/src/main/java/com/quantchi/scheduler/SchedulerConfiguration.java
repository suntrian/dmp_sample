package com.quantchi.scheduler;

import com.quantchi.scheduler.listener.SchedulerJobListener;
import com.quantchi.scheduler.listener.SchedulerJobTriggerListener;
import com.quantchi.scheduler.listener.SchedulerListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class SchedulerConfiguration implements ApplicationContextAware {

    private static ApplicationContext context;

    @Autowired
    @Bean("scheduler")
    public SchedulerFactoryBean schedulerFactoryBean(SchedulerListener schedulerListener,
                                                     SchedulerJobListener schedulerJobListener,
                                                     SchedulerJobTriggerListener triggerListener) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setSchedulerListeners(schedulerListener);
        schedulerFactoryBean.setGlobalTriggerListeners(triggerListener);
        schedulerFactoryBean.setGlobalJobListeners(schedulerJobListener);
        return schedulerFactoryBean;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SchedulerConfiguration.setContext(applicationContext);
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        SchedulerConfiguration.context = context;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) context.getBean(name);
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static <T> T getBean(String name, Class<T> beanClass) {
        return context.getBean(name, beanClass);
    }

}
