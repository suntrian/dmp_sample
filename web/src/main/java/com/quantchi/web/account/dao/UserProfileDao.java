package com.quantchi.web.account.dao;

import com.quantchi.web.account.entity.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserProfileDao {

    Integer insert(UserProfile profile);

    Integer update(UserProfile profile);

    List<UserProfile> list();

    List<UserProfile> listByUserIds(Collection<Integer> ids);
}
