package com.quantchi.web.metadata.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MdTable implements Serializable {

    private String id;
    private String name;
    private String label;
    private String databaseId;
    private String datasourceId;
    private String schema;
    private String type;
    private String remark;
    private String ddl;
    private String primaryKey;
    private Integer rank;

}
