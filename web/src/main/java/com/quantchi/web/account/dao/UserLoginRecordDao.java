package com.quantchi.web.account.dao;

import com.quantchi.web.account.entity.UserLoginRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserLoginRecordDao {

    Integer insert(UserLoginRecord record);

    Integer deleteByUserId(Integer userId);

    Integer deleteByUserIdAndDay(@Param("userId") Integer userId, @Param("fromDate") Date fromDate,
                                 @Param("endDate") Date endDate);

    List<UserLoginRecord> listByUserId(Integer userId);

    List<UserLoginRecord> listByUserIdAndDay(@Param("userId") Integer userId, @Param("fromDate") Date fromDate,
                                             @Param("endDate") Date endDate);

    UserLoginRecord getLastLogin(Integer userId);

}
