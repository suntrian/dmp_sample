package com.quantchi.web.account.dao;

import com.quantchi.web.account.entity.UserAccount;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAccountDao {

    Integer insert(UserAccount account);

    Integer update(UserAccount account);

    Integer delete(Integer userId);

    List<UserAccount> search(UserAccount account);

    List<UserAccount> list();

    UserAccount get(Integer id);
}
