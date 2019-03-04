package com.quantchi.web.metadata.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class MdColumn implements Serializable {

    private Integer id;
    //字段名称
    private String name;
    //字段中文名
    private String label;
    //字段别名，多个别名用逗号分隔
    private String alias;
    //字段坐标
    private String coordinate;
    private Integer tableId;
    private Integer databaseId;
    private Integer datasourceId;
    //字段在表中的位置
    private Integer order;
    private String type;
    private Integer size;
    private String defaultValue;
    // 1 is nonnull, 2 is autoincrement, 3 is unique, 4 primary key, 5 is foreign key, 6 is generated, 7 is partition key
    private String extra = "0000000";
    private String remark;

    public boolean isNonnull(){
        return this.extra.charAt(0) == '1';
    }

    public boolean isAutoIncrement(){
        return this.extra.charAt(1) == '1';
    }

    public boolean isUnique(){
        return this.extra.charAt(2) == '1';
    }

    public int isPrimaryKey(){
        return this.extra.charAt(3);
    }

    public boolean isForeignKey(){
        return this.extra.charAt(4) == '1';
    }

    public boolean isGenerated(){
        return this.extra.charAt(5) == '1';
    }

    public int isPartitionKey(){
        return this.extra.charAt(6);
    }

    public void setNonNull(boolean nonnull){
        this.extra = replace(this.extra, 0, nonnull?'1':'0');
    }

    public void setAutoIncrement(boolean autoIncrement){
        this.extra = replace(this.extra, 1, autoIncrement?'1':'0');
    }

    public void setUnique(boolean unique){
        this.extra = replace(this.extra, 2, unique?'1':'0');
    }

    public void setPrimaryKey(int primaryKeyIndex){
        this.extra = replace(this.extra, 3, (char) primaryKeyIndex);
    }

    public void setForeignKey(boolean foreignKey){
        this.extra = replace(this.extra, 4, foreignKey?'1':'0');
    }

    public void setGenerated(boolean isGenerated){
        this.extra = replace(this.extra, 5, isGenerated?'1':'0');
    }

    public void setPartitionKey(int partitionKey){
        this.extra = replace(this.extra, 6, (char) partitionKey);
    }

    private String replace(String str, int pos, char c){
        char[] chars = str.toCharArray();
        chars[pos] = c;
        return new String(chars);
    }
}
