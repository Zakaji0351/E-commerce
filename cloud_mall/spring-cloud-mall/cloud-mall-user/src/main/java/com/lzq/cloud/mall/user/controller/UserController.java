package com.lzq.cloud.mall.user.controller;



import com.lzq.cloud.mall.common.common.ApiRestResponse;
import com.lzq.cloud.mall.common.common.Constant;
import com.lzq.cloud.mall.common.exception.MallException;
import com.lzq.cloud.mall.common.exception.MallExceptionEnum;
import com.lzq.cloud.mall.user.model.pojo.User;
import com.mysql.cj.util.StringUtils;
import com.lzq.cloud.mall.user.service.UserService;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("username") String username, @RequestParam("password") String password) throws MallException {
        if(StringUtils.isNullOrEmpty(username)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isNullOrEmpty(password)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        if(password.length() < 8){
            return ApiRestResponse.error(MallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(username, password);
        return ApiRestResponse.success();
    }
    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) throws MallException {
        if(StringUtils.isNullOrEmpty(username)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isNullOrEmpty(password)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(username, password);
        user.setPassword(null);
        session.setAttribute(Constant.MALL_USER, user);
        return ApiRestResponse.success(user);
    }
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session, @RequestParam String signature) throws MallException {
        User currentUser = (User) session.getAttribute(Constant.MALL_USER);
        if(currentUser == null){
            return ApiRestResponse.error(MallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }
    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session){
        session.removeAttribute(Constant.MALL_USER);
        return ApiRestResponse.success();
    }
    @PostMapping("/adminlogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) throws MallException {
        if(StringUtils.isNullOrEmpty(username)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isNullOrEmpty(password)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(username, password);
        if (userService.checkAdmin(user)) {
            user.setPassword(null);
            session.setAttribute(Constant.MALL_USER, user);
            return ApiRestResponse.success(user);
        }else{
            return ApiRestResponse.error(MallExceptionEnum.NEED_ADMIN);
        }
    }
    @PostMapping("/checkAdminRole")
    @ResponseBody
    public Boolean checkAdminRole(@RequestBody User user){
        return userService.checkAdmin(user);
    }
    @GetMapping("/getUser")
    @ResponseBody
    public User getUser(HttpSession session){
        User currentUser = (User) session.getAttribute(Constant.MALL_USER);
        return currentUser;
    }


}
