package com.quantchi.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({"com.quantchi.web.*.dao"})
@SpringBootApplication
public class DMPApplication {

  public static void main(String[] args) {
    SpringApplication.run(DMPApplication.class, args);
  }
}
