package com.lzq.mall.model.dao;

import com.lzq.mall.model.pojo.User;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    User selectByName(String username);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    User selectByOneEmailAddress(@Param("emailAddress") String emailAddress);
}