package com.quantchi.web.metadata.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MdDatabase implements Serializable {

    private Integer id;
    private String name;
    private String label;
    private Integer datasourceId;
    private Integer systemId;
    private String remark;


}
