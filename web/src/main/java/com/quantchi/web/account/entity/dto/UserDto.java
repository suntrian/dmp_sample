package com.quantchi.web.account.entity.dto;

import com.quantchi.web.account.entity.Department;
import com.quantchi.web.account.entity.Role;
import com.quantchi.web.account.entity.UserAccount;
import lombok.Data;

import java.util.List;

@Data
public class UserDto extends UserAccount {

    List<Role> roles;
    Department department;
}
