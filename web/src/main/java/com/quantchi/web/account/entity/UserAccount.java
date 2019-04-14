package com.quantchi.web.account.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quantchi.web.common.BaseEntity;
import com.quantchi.web.common.constant.ConstantStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount extends BaseEntity implements Serializable {

    public static int STATUS_NORMAL = ConstantStatus.NORMAL.getStatus();
    public static int STATUS_ABNORMAL = ConstantStatus.ABNORMAL.getStatus();
    public static int STATUS_DELETED = ConstantStatus.DELETED.getStatus();

    private Integer id;
    private String username;
    private String realname;
    private String usercode;
    private String email;
    private String mobile;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String salt;
    private Integer type;
    private Integer status = STATUS_NORMAL;
    private String guid;

    public UserAccount(Integer id) {
        this.id = id;
    }

    public UserAccount(String username) {
        this.username = username;
    }
}
