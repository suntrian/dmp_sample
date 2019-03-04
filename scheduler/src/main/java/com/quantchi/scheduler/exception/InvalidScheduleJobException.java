package com.quantchi.scheduler.exception;

public class InvalidScheduleJobException extends RuntimeException {

    public InvalidScheduleJobException() {
        super();
    }

    public InvalidScheduleJobException(String message) {
        super(message);
    }
}
