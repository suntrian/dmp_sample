package com.quantchi.web.account.entity.dto;

import com.quantchi.web.account.entity.Resource;
import com.quantchi.web.account.entity.Role;
import lombok.Data;

import java.util.List;

@Data
public class RoleDto extends Role {

    List<Resource> resources;
}
