package com.quantchi.web.common.constant;

import java.util.stream.Stream;

public enum  StatusConstant {

    NORMAL("NORMAL", 0),
    UNREADY("UNREADY",1),
    ABNORMAL("ABNORMAL",3),
    FORBIDDEN("FORBIDDEN",5),
    LOCKED("LOCKED", 7),
    DELETED("DELETED",9);

    private String name;
    private int status;

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    StatusConstant(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public static StatusConstant of(String name){
        if (name==null){return null;}
        return StatusConstant.valueOf(name.trim().toUpperCase());
    }

    public static StatusConstant of(Integer status){
        if (status == null) {return null;}
        return Stream.of(StatusConstant.values()).filter(i->status.equals(i.status)).findFirst().orElse(null);
    }

}
