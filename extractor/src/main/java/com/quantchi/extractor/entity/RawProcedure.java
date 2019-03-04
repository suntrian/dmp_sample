package com.quantchi.extractor.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RawProcedure implements Serializable {

    private String PROCEDURE_CAT;
    private String PROCEDURE_SCHEM;
    private String PROCEDURE_NAME;
    private String REMARK;
    private Short  PROCEDURE_TYPE;
    private String SPECIFIC_NAME;

}
