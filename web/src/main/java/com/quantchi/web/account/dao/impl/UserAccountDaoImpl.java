package com.quantchi.web.account.dao.impl;

import com.quantchi.web.account.dao.UserAccountDao;
import com.quantchi.web.account.entity.UserAccount;
import com.quantchi.web.annotation.MongoDB;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@MongoDB
public class UserAccountDaoImpl implements UserAccountDao {

    @Override
    public Integer insert(UserAccount account) {
        return null;
    }

    @Override
    public Integer update(UserAccount account) {
        return null;
    }

    @Override
    public List<UserAccount> search(UserAccount account) {
        return null;
    }

    @Override
    public List<UserAccount> list(UserAccount account) {
        return null;
    }

    @Override
    public UserAccount get(Integer id) {
        return null;
    }
}
