package com.quantchi.web.standard.entity;

import com.quantchi.web.common.constant.ConstantStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class Standard implements Serializable {

    public static final int STATUS_NORMAL = ConstantStatus.NORMAL.getStatus();
    public static final int STATUS_UPGRADE = ConstantStatus.UNREADY.getStatus();
    public static final int STATUS_DELETED = ConstantStatus.DELETED.getStatus();

    private Integer id;
    private String name;
    private String label;
    private String alia;
    private String code;
    private Integer level;
    private String definition;
    private String dataType;
    private Integer status;

}
