package com.quantchi.web.dictdata.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DictItem implements Serializable {

    //来源于数据库
    public static final String SOURCE_DATABASE = "DATABASE";
    //来源于文件上传
    public static final String SOURCE_FILE = "FILE";
    //来源于手动添加
    public static final String SOURCE_MANUAL = "MANUAL";
    //来源于代码约定
    public static final String SOURCE_CODE = "CODE";

    private Integer id;
    private String name;
    private String code;
    //字典项类别
    private String category;
    //字典项来源
    private String source;
    //字典项来源表
    private Integer tableId;
    //字典项来源库
    private Integer databaseId;
    //字典项来源数据源
    private Integer datasourceId;

}
