package com.quantchi.web.dictdata.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class StdDictValue implements Serializable {

    private Integer id;
    private String name;
    private String value;
    private Integer standardId;
    private String comment;
    private String source;
}
