package com.quantchi.web.metadata.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MdTable implements Serializable {

    private Integer id;
    private String name;
    private String label;
    private Integer databaseId;
    private Integer datasourceId;
    private String schema;
    private String coordinate;
    private String type;
    private String remark;
    private String ddl;

}
