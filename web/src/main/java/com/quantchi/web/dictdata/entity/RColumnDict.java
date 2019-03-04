package com.quantchi.web.dictdata.entity;

import com.quantchi.web.metadata.entity.MdColumn;
import lombok.Data;

import java.io.Serializable;

@Data
public class RColumnDict implements Serializable {

    private MdColumn column;
    private MdDictItem dictItem;

}
