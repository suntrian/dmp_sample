package com.quantchi.scheduler.entity;

import lombok.Data;
import org.quartz.CronExpression;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class SchedulerJob implements Serializable {

  private static final long serialVersionUID = 8202947644296415020L;

  public static final String JOB_KEY = "DMP_SCHEDULE_JOB";

  public static final String STATUS_NONE = "NONE"; //Trigger.TriggerState.NONE.name();
  public static final String STATUS_NORMAL = "NORMAL"; //Trigger.TriggerState.NORMAL.name();
  public static final String STATUS_PAUSED = "PAUSED"; //Trigger.TriggerState.PAUSED.name();
  public static final String STATUS_COMPLETE = "COMPLETE"; //Trigger.TriggerState.COMPLETE.name();
  public static final String STATUS_BLOCKED = "BLOCKED"; //Trigger.TriggerState.BLOCKED.name();
  public static final String STATUS_ERROR = "ERROR"; //Trigger.TriggerState.ERROR.name();
  public static final String STATUS_STOPPED = "NONE";

  /**
   * @see org.quartz.SimpleTrigger#MISFIRE_INSTRUCTION_FIRE_NOW
   */
  public static final int MISFIRE_INSTRUCTION_FIRE_NOW = 1;
  /**
   * @see org.quartz.SimpleTrigger#MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT
   */
  public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT = 2;
  /**
   * @see org.quartz.SimpleTrigger#MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT
   */
  public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT = 3;
  /**
   * @see org.quartz.SimpleTrigger#MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT
   */
  public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT = 4;
  /**
   * @see org.quartz.SimpleTrigger#MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT
   */
  public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT = 5;

  /**
   * @see org.quartz.SimpleTrigger#REPEAT_INDEFINITELY
   */
  public static final int REPEAT_INDEFINITELY = -1;

  /**
   * @see org.quartz.CronTrigger#MISFIRE_INSTRUCTION_FIRE_ONCE_NOW
   */
  public static final int MISFIRE_INSTRUCTION_FIRE_ONCE_NOW = 1;
  /**
   * @see org.quartz.CronTrigger#MISFIRE_INSTRUCTION_DO_NOTHING
   */
  public static final int MISFIRE_INSTRUCTION_DO_NOTHING = 2;

  /**
   * @see org.quartz.Trigger#MISFIRE_INSTRUCTION_SMART_POLICY
   */
  public static final int MISFIRE_INSTRUCTION_SMART_POLICY = 0;
  /**
   * @see org.quartz.Trigger#MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY
   */
  public static final int MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY = -1;

  private Integer id;
  private String name;
  private String status = STATUS_NONE;
  private String group;
  private String type;
  private String cron;
  //used for simple job , millisecond
  private Long    interval;
  private Integer repeatCount;
  private String description;
  private String clazz;
  private String bean;
  private String method;
  private Object argument;
  private Boolean concurrent = false;         //是否并发执行
  private Integer misfirePolicy = MISFIRE_INSTRUCTION_DO_NOTHING;      //
  private Boolean autoStart = true;          //是否自动执行
  private Date effectTime;            //任务生效时间
  private Date expireTime;            //任务过期时间
  private Date createdAt;
  private Integer createdBy;
  private Date updatedAt;
  private Integer updatedBy;
  private Boolean deleted;
  private Integer deletedBy;
  private Date deletedAt;


  public boolean isSimpleJob(){
    if (this.cron == null){
      this.interval = 0L;
      this.repeatCount = 0;
      return true;
    }
    Pattern pattern = Pattern.compile("(\\d+)\\s*[x* _\\-|/#]\\s*(\\d+)");
    Matcher matcher = pattern.matcher(this.cron.trim());
    if (matcher.find()) {
      interval = Long.valueOf(matcher.group(1));
      repeatCount = Integer.valueOf(matcher.group(2));
      return true;
    }
    return false;
  }

  public boolean isCronJob(){
    if (CronExpression.isValidExpression(this.cron)){
      return true;
    }
    return false;
  }


  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SchedulerJob) {
      SchedulerJob job = (SchedulerJob) obj;
      return this.group.equals(job.group)
          && this.name.equals(job.name);
    }
    return super.equals(obj);
  }
}
