package com.quantchi.web.account.service;

import com.quantchi.web.account.entity.UserAccount;

import java.util.List;

public interface UserService {

    int addUser(UserAccount account);

    int modifyUser(UserAccount account);

    int deleteUser(UserAccount account);

    UserAccount get(Integer id);

    List<UserAccount> list();

    List<UserAccount> search(UserAccount account);
}
