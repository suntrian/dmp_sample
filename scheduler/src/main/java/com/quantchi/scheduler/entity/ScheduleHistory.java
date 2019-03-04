package com.quantchi.scheduler.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ScheduleHistory {

    private Integer id;
    private Integer jobId;
    private String jobName;
    private Date startTime;
    private Date endTime;
    private Boolean success;
    private String message;
    private Object record;
}
