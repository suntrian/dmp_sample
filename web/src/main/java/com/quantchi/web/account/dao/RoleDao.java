package com.quantchi.web.account.dao;

import com.quantchi.web.account.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleDao {

    Integer insert(Role role);

    Integer update(Role role);

    Integer delete(Integer roleId);

    List<Role> list(Role role);
}
