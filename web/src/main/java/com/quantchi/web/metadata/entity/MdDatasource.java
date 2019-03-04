package com.quantchi.web.metadata.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MdDatasource implements Serializable {

    private Integer id;
    private String name;
    private String identifier;
    private Integer system;
    private String type;
    private String version;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String remark;

}
