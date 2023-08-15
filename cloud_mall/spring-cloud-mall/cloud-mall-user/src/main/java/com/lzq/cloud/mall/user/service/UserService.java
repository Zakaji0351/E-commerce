package com.lzq.cloud.mall.user.service;


import com.lzq.cloud.mall.common.exception.MallException;
import com.lzq.cloud.mall.user.model.pojo.User;

public interface UserService {

    public void register(String username, String password) throws MallException;

    User login(String username, String password) throws MallException;

    void updateInformation(User user) throws MallException;

    boolean checkAdmin(User user);

    boolean checkEmailRegistered(String emailAddress);
}