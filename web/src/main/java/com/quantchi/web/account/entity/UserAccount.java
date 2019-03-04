package com.quantchi.web.account.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quantchi.web.common.constant.StatusConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount implements Serializable {

    public static int STATUS_NORMAL = StatusConstant.NORMAL.getStatus();
    public static int STATUS_ABNORMAL = StatusConstant.ABNORMAL.getStatus();
    public static int STATUS_DELETED = StatusConstant.DELETED.getStatus();

    private Integer id;
    private String username;
    private String realname;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String salt;
    private Integer type;
    private Integer status = STATUS_NORMAL;

    public UserAccount(Integer id) {
        this.id = id;
    }

    public UserAccount(String username) {
        this.username = username;
    }
}
