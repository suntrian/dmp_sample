package com.quantchi.web.exception;

import com.quantchi.web.common.ResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler {

    /**
     * 非法访问
     *
     * @param ex 非法访问的异常对象
     * @return 响应体
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResultDto handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.error("异常参数：{}", ex.getMessage());
        return ResultDto.failure(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResultDto> handleResourceNotFoundException(ResourceNotFoundException ex){
        return new ResponseEntity<>(ResultDto.failure(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * 其他异常都按照"未知异常"处理
     *
     * @param ex 异常对象
     * @return 响应体
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ResultDto> handleHasResourceExistsException(Exception ex) {
        log.error("操作失败：{}", ex.getMessage());

        return new ResponseEntity<>(ResultDto.failure(ex.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResultDto> handleNullPointerException(Exception ex){
        log.error("NOP EXCEPTION:{}",ex.getMessage());
        return new ResponseEntity<>(ResultDto.failure("NOP EXCEPTION:" + ex.getMessage()),HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultDto> handleGlobalException(Exception ex){
        log.error("EXCEPTION:{}",ex.getMessage());
        return new ResponseEntity<>(ResultDto.failure(ex.getMessage()), HttpStatus.OK);
    }
    
}
