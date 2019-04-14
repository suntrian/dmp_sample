package com.quantchi.web.dictdata.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RDictValue implements Serializable {

    private DictValue metadataValue;
    private DictValue standardValue;

}
