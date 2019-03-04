package com.quantchi.web.account.service.impl;

import com.quantchi.web.account.dao.UserAccountDao;
import com.quantchi.web.account.entity.UserAccount;
import com.quantchi.web.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserAccountDao userAccountDao;

    @Override
    public int addUser(UserAccount account) {
        return userAccountDao.insert(account);
    }

    @Override
    public int modifyUser(UserAccount account){
        return userAccountDao.update(account);
    }

    @Override
    public int deleteUser(UserAccount account){
        account.setStatus(UserAccount.STATUS_DELETED);
        return userAccountDao.update(account);
    }

    @Override
    public UserAccount get(Integer id) {
        return userAccountDao.get(id);
    }

    @Override
    public List<UserAccount> list(UserAccount account){
        return userAccountDao.search(account);
    }

    @Override
    public List<UserAccount> queryByUsername(String userName){
        return userAccountDao.list(new UserAccount(userName));
    }
}
