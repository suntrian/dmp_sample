package com.quantchi.web.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

public class BaseEntity implements Serializable {

    @Setter
    @Getter
    protected String query;

    protected Integer createdBy;
    protected Date createdAt;
    protected Integer updatedBy;
    protected Date updatedAt;
    protected Integer deletedBy;
    protected Date deletedAt;
}
