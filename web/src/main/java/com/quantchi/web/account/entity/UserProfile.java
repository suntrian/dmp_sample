package com.quantchi.web.account.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserProfile extends UserAccount implements Serializable {

    private String avatar;
    private Boolean gender;
    private String address;

}
