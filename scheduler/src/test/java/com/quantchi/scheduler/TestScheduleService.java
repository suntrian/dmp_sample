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

        SchedulerJob voidJob = new SchedulerJob();
        voidJob.setClazz(clazz);
        voidJob.setName("voidJob");
        voidJob.setGroup(groupName);
        voidJob.setCron("0 0/1 * * * ?");
        voidJob.setMethod("voidPrint");
        voidJob.setMisfirePolicy(SchedulerJob.MISFIRE_INSTRUCTION_FIRE_NOW);
        schedulerService.insert(voidJob);

        Thread.sleep(1000 * 60 * 2);

        SchedulerJob intJob = new SchedulerJob();
        intJob.setName("intJob");
        intJob.setGroup(groupName);
        intJob.setClazz(clazz);
        intJob.setCron("0 0/1 * * * ?");
        intJob.setMethod("intPrint");
        intJob.setArgument(123456);
        schedulerService.insert(intJob);

        Thread.sleep(1000 * 60 * 2);

        SchedulerJob strJob = new SchedulerJob();
        strJob.setName("strJob");
        strJob.setGroup(groupName);
        strJob.setClazz(clazz);
        strJob.setCron("0 0/3 * * * ?");
        strJob.setMethod("strPrint");
        strJob.setArgument("Hello World");
        schedulerService.insert(strJob);

        Thread.sleep(1000 * 60 * 2);

        SchedulerJob printerJob = new SchedulerJob();
        printerJob.setName("printerJob");
        printerJob.setGroup(groupName);
        printerJob.setClazz(clazz);
        printerJob.setCron("0 0/5 * * * ?");
        printerJob.setMethod("printerPrint");
        Printer printer = new Printer();
        printer.a = "NIHAO";
        printer.b = 123456;
        printer.c = 3.1415926;
        printerJob.setArgument(printer);
        schedulerService.insert(printerJob);

        Thread.sleep(1000 * 60 * 3);
    }


    public static class Printer implements Serializable {

        public String a;
        public Integer b;
        public Double c;

        public Printer() {
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
