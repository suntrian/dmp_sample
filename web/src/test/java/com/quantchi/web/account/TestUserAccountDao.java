package com.quantchi.web.account;

import com.quantchi.web.TestBase;
import com.quantchi.web.account.dao.UserAccountDao;
import com.quantchi.web.account.entity.UserAccount;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TestUserAccountDao extends TestBase {

    @Autowired
    private UserAccountDao userAccountDao;

    @Test
    public void testInsertUser() {
        UserAccount account = new UserAccount();
        account.setUsercode("ddd");
        account.setUsername("dafa");
        account.setPassword("ssdfd");
        account.setStatus(1);
        account.setSalt("slfsf");
        int c = userAccountDao.insert(account);

        account.setEmail("dslfsldfsfs");
        userAccountDao.update(account);

        List<UserAccount> accounts = userAccountDao.list();
        Assert.assertEquals(account.getEmail(),
            accounts.stream().filter(i -> i.getId().equals(account.getId())).map(UserAccount::getEmail).findFirst().orElse(""));
        userAccountDao.delete(account.getId());
    }

}
