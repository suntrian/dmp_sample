package com.quantchi.web.metadata.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MdDatabase implements Serializable {

    private String id;
    private String name;
    private String label;
    private String datasourceId;
    private String systemId;
    private String remark;


}
