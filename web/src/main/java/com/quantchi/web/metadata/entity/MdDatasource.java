package com.quantchi.web.metadata.entity;

import com.quantchi.web.common.BaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class MdDatasource extends BaseEntity implements Serializable {

    private String id;
    private String name;
    private String label;
    private String systemId;
    private String type;
    private String source;
    private String status;
    private String version;
    private String host;
    private Integer port;
    private String user;
    private String pass;
    private String description;



}
