package com.lzq.mall.service.impl;

import com.lzq.mall.exception.MallException;
import com.lzq.mall.exception.MallExceptionEnum;
import com.lzq.mall.model.dao.UserMapper;
import com.lzq.mall.model.pojo.User;
import com.lzq.mall.service.UserService;
import com.lzq.mall.utils.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }

    @Override
    public void register(String username, String password, String emailAddress) throws MallException {
        User u = userMapper.selectByName(username);
        if(u != null){
            throw new MallException(MallExceptionEnum.USER_EXISTED);
        }
        User user = new User();
        user.setUsername(username);
        user.setEmailAddress(emailAddress);
        try{
            user.setPassword(Md5Utils.getMD5Str(password));
        }catch(Exception e){
            e.printStackTrace();
        }
//        user.setPassword(password);
        int count = userMapper.insertSelective(user);
        if(count == 0){
            throw new MallException(MallExceptionEnum.INSERT_FAILURE);
        }
    }
    @Override
    public User login(String username, String password) throws MallException {
        String md5Password = null;
        try {
            md5Password = Md5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        User user = userMapper.selectLogin(username, md5Password);
        if(user == null){
            throw new MallException(MallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    @Override
    public void updateInformation(User user) throws MallException {
        int count = userMapper.updateByPrimaryKeySelective(user);
        if(count > 1){
            throw new MallException(MallExceptionEnum.SIGNATURE_UPDATE_FAILURE);
        }
    }
    @Override
    public boolean checkAdmin(User user){
        if(user.getRole().equals(2)){
            return true;
        }else{
            return false;
        }
    }
    @Override
    public boolean checkEmailRegistered(String emailAddress){
        User user = userMapper.selectByOneEmailAddress(emailAddress);
        if(user != null){
            return false;
        }else{
            return true;
        }
    }
}
