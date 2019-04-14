package com.quantchi.web.common.constant;

import java.util.stream.Stream;

public enum ConstantStatus {

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

    ConstantStatus(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public static ConstantStatus of(String name) {
        if (name==null){return null;}
        return ConstantStatus.valueOf(name.trim().toUpperCase());
    }

    public static ConstantStatus of(Integer status) {
        if (status == null) {return null;}
        return Stream.of(ConstantStatus.values()).filter(i -> status.equals(i.status)).findFirst().orElse(null);
    }

}
