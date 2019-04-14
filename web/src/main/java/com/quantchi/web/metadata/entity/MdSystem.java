package com.quantchi.web.metadata.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MdSystem implements Serializable {

    private String id;
    private String name;
    private String remark;
    private Integer catalogId;

}
