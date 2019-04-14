package com.quantchi.web.account.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserLoginRecord {

    private Date loginTime;
    private Integer userId;
    private String loginIp;
    private Integer status;

}
