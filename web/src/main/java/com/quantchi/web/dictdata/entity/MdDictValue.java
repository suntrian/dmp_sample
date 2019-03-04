package com.quantchi.web.dictdata.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MdDictValue implements Serializable {

    private Integer id;
    private String name;
    private String value;
    private Integer itemId;
    private String comment;

}
