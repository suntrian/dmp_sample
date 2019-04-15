package com.quantchi.scheduler;


import com.quantchi.scheduler.entity.SchedulerJob;
import com.quantchi.scheduler.service.SchedulerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Transactional
@SpringBootTest(classes = SchedulerApplication.class)
@RunWith(SpringRunner.class)
public class TestScheduleService {

    @Autowired
    private SchedulerService schedulerService;

    @Test
    public void testAddJob() throws SchedulerException, InterruptedException {
        String clazz = "com.quantchi.scheduler.TestScheduleService$Printer";
        String groupName = "jobTest";

        SchedulerJob exceptionJob = new SchedulerJob();
        exceptionJob.setClazz(clazz);
        exceptionJob.setName("exceptionJob");
        exceptionJob.setMethod("exceptionPrint");
        exceptionJob.setCron("* */2 * * * ? ");
        schedulerService.insert(exceptionJob);

        Thread.sleep(1000 * 60);

        SchedulerJob voidJob = new SchedulerJob();
        voidJob.setClazz(clazz);
        voidJob.setName("voidJob");
        voidJob.setGroup(groupName);
        voidJob.setCron("1 * 4");
        voidJob.setMethod("voidPrint");
        voidJob.setMisfirePolicy(SchedulerJob.MISFIRE_INSTRUCTION_SMART_POLICY);
        schedulerService.insert(voidJob);

        Thread.sleep(1000 * 60);

        SchedulerJob intJob = new SchedulerJob();
        intJob.setName("intJob");
        intJob.setGroup(groupName);
        intJob.setClazz(clazz);
        intJob.setCron("0 0/1 * * * ?");
        intJob.setMethod("intPrint");
        intJob.setArgument(123456);
        intJob.setMisfirePolicy(SchedulerJob.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
        schedulerService.insert(intJob);

        Thread.sleep(1000 * 60);

        SchedulerJob strJob = new SchedulerJob();
        strJob.setName("strJob");
        strJob.setGroup(groupName);
        strJob.setClazz(clazz);
        strJob.setCron("0 0/3 * * * ?");
        strJob.setMethod("strPrint");
        strJob.setArgument("Hello World");
        Date now = new Date();
        strJob.setEffectTime(now);
        Calendar expire = Calendar.getInstance();
        expire.set(2019, Calendar.APRIL, 16, 0, 55, 0);
        strJob.setExpireTime(expire.getTime());
        schedulerService.insert(strJob);

        Thread.sleep(1000 * 60);

        SchedulerJob printerJob = new SchedulerJob();
        printerJob.setName("printerJob");
        printerJob.setGroup(groupName);
        printerJob.setClazz(clazz);
        printerJob.setCron("0 */5 * * * ?");
        printerJob.setMethod("printerPrint");
        Printer printer = new Printer();
        printer.a = "NIHAO";
        printer.b = 123456;
        printer.c = 3.1415926;
        printerJob.setArgument(printer);
        printerJob.setMisfirePolicy(SchedulerJob.MISFIRE_INSTRUCTION_SMART_POLICY);
        schedulerService.insert(printerJob);

        Thread.sleep(1000 * 60 * 10);
    }


    public static class Printer implements Serializable {

        public String a;
        public Integer b;
        public Double c;

        public Printer() {
        }

        public void exceptionPrint() throws Exception {
            throw new Exception("this is a exception");
        }

        public void voidPrint() {
            System.out.println("VOID PRINTER");
        }

        public Integer intPrint(Integer i) {
            System.out.println(i);
            return i;
        }

        public String strPrint(String str) {
            System.out.println(str);
            return str;
        }

        public Printer printerPrint(Printer printer) {
            System.out.println(printer);
            return printer;
        }


    }

}
