package com.lzq.mall.service;

import com.lzq.mall.exception.MallException;
import com.lzq.mall.model.pojo.User;

public interface UserService {
    public User getUser();
    public void register(String username, String password, String emailAddress) throws MallException;

    User login(String username, String password) throws MallException;

    void updateInformation(User user) throws MallException;

    boolean checkAdmin(User user);

    boolean checkEmailRegistered(String emailAddress);
}
