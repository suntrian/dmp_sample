package com.quantchi.scheduler.entity;

import lombok.Data;

import java.util.Date;

@Data
public class SchedulerHistory {

    private Integer id;
    private Integer jobId;
    private String jobName;
    private Date startTime;
    private Date finishTime;
    private Date nextFireTime;
    private Boolean success;
    private String message;

}
