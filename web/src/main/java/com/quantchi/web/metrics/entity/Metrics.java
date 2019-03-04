package com.quantchi.web.metrics.entity;

import com.quantchi.web.common.constant.StatusConstant;
import lombok.Data;

import java.io.Serializable;

@Data
public class Metrics implements Serializable {

    public static final int STATUS_NORMAL = StatusConstant.NORMAL.getStatus();
    public static final int STATUS_UPGRADE = StatusConstant.UNREADY.getStatus();
    public static final int STATUS_DELETED = StatusConstant.DELETED.getStatus();

    private Integer id;
    private String name;
    private String status;
    private String comment;

}
