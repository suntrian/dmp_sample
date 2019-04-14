package com.quantchi.scheduler.executor;

import com.quantchi.scheduler.entity.SchedulerJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@Component
public class SchedulerJobExecutor {

    @Autowired
    private ApplicationContext context;

    public static Boolean checkJobValid(SchedulerJob job){
        try {
            SchedulerJobExecutor executor = new SchedulerJobExecutor();
            executor.getMethod(executor.getCaller(job),job);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public Object getCaller(SchedulerJob job) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        Object object = null;
        if (StringUtils.isNotBlank(job.getBean())){
            if (StringUtils.isNotBlank(job.getClazz())){
                object = context.getBean(job.getBean(), Class.forName(job.getClazz()));
            } else {
                object = context.getBean(job.getBean());
            }
        } else if (StringUtils.isNotBlank(job.getClazz())){
            object = Class.forName(job.getClazz()).newInstance();
        } else {
            throw new IllegalStateException("unknown target");
        }
        if (object == null){
            String name = StringUtils.isNotBlank(job.getBean())?job.getBean():job.getClazz();
            throw new ClassNotFoundException(name + "not found");
        }
        return object;
    }

    /**
     * 反射获取方法，参数不可以为基本数据类型
     *
     * @param object 方法的执行对象
     * @param job    job
     * @return Method
     * @throws NoSuchMethodException
     */
    public Method getMethod(Object object, SchedulerJob job) throws NoSuchMethodException {
        Class clazz = object.getClass();
        Method method;
        if (job.getArgument() == null ){
            method = ReflectUtils.findDeclaredMethod(clazz, job.getMethod(), new Class[0]);
        } else {
            Object argument = job.getArgument();
            Class[] argtype = new Class[1];
            argtype[0] = argument.getClass();
            method = ReflectUtils.findDeclaredMethod(clazz, job.getMethod(), argtype );
        }
        return method;
    }

    public Object invoke(SchedulerJob job)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Object object = getCaller(job);
        Method method = getMethod(object, job);
        return method.invoke(object, job.getArgument());

    }

}
