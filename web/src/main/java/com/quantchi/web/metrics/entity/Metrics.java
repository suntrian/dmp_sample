package com.quantchi.web.metrics.entity;

import com.quantchi.web.common.constant.ConstantStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class Metrics implements Serializable {

    public static final int STATUS_NORMAL = ConstantStatus.NORMAL.getStatus();
    public static final int STATUS_UPGRADE = ConstantStatus.UNREADY.getStatus();
    public static final int STATUS_DELETED = ConstantStatus.DELETED.getStatus();

    private Integer id;
    private String name;
    private String status;
    private String comment;

}
