package com.quantchi.web.account.dao;

import com.quantchi.web.account.entity.UserAccount;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAccountDao {

    @Insert("INSERT INTO user_account VALUES(#{id}, #{username}, #{realname})")
    Integer insert(UserAccount account);

    @Update("UPDATE user_account SET password = #{password} WHERE id = #{id}")
    Integer update(UserAccount account);

    @Select("SELECT * FROM user_account WHERE username LIKE '%#{username}%'")
    List<UserAccount> search(UserAccount account);

    @Select("SELECT * FROM user_account WHERE username = #{username}")
    List<UserAccount> list(UserAccount account);

    @Select("SELECT * FROM user_account WHERE id = #{id} LIMIT 1")
    UserAccount get(Integer id);
}
