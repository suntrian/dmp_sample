package com.quantchi.web.account.entity;

import com.quantchi.web.common.constant.StatusConstant;
import lombok.Data;

import java.io.Serializable;

@Data
public class Resource implements Serializable {

    private static final Integer STATUS_NORMAL = StatusConstant.NORMAL.getStatus();
    private static final Integer STATUS_HIDDEN = StatusConstant.FORBIDDEN.getStatus();

    private static final String TYPE_MENU = "MENU";
    private static final String TYPE_API = "API";

    private Integer id;
    private String code;
    private String label;
    private Integer parentId;
    private String uri;
    private String method;
    private String type;
    private Integer status;
    private String description;
}
