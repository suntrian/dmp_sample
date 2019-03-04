package com.quantchi.web.common;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
public class ResultDto implements Serializable {

    private Integer code;
    private String message;
    private Long total;
    private Collection<?> data;

    public ResultDto(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultDto(Integer code, Collection<?> data) {
        this.code = code;
        this.data = data;
        this.total = (long)this.data.size();
    }

    public ResultDto(Integer code, Collection<?> data, Long total) {
        this.code = code;
        this.total = total;
        this.data = data;
    }

    public static ResultDto success(String message){
        return new ResultDto(HttpStatus.OK.value(), message);
    }

    public static <T> ResultDto success(T data){
        return new ResultDto(HttpStatus.OK.value(), Collections.singleton(data), 1L);
    }

    public static <T extends Collection> ResultDto success(T data){
        return new ResultDto(HttpStatus.OK.value(), data);
    }

    public static <T extends Collection> ResultDto success(T data, Number total){
        return new ResultDto(HttpStatus.OK.value(), data, total.longValue());
    }

    public static ResultDto failure(Integer code, String message){
        return new ResultDto(code, message);
    }

    public static ResultDto failure(String message){
        return new ResultDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

}
