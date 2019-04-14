package com.quantchi.web.common;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Collection;

@Data
public class ResultDto implements Serializable {

    private Integer code = HttpStatus.OK.value();
    private String message;
    private Long total;
    private Object data;

    public ResultDto(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultDto(Object data) {
        this.data = data;
    }

    public ResultDto(Object data, Number total) {
        this.data = data;
        this.total = total == null ? null : total.longValue();
    }

    public static ResultDto success(String message){
        return new ResultDto(HttpStatus.OK.value(), message);
    }

    public static <T> ResultDto success(T data){
        return new ResultDto(data);
    }

    public static <T extends Collection> ResultDto success(T data){
        return new ResultDto(data);
    }

    public static <T extends Collection> ResultDto success(T data, Number total){
        return new ResultDto(data, total);
    }

    public static ResultDto failure(Integer code, String message){
        return new ResultDto(code, message);
    }

    public static ResultDto failure(String message){
        return new ResultDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

}
