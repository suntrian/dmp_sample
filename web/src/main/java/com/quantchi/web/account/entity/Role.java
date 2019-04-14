package com.quantchi.web.account.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Role implements Serializable {

    private Integer id;
    private String name;
    private Integer departmentId;
    private String description;
    private String guid;
}
