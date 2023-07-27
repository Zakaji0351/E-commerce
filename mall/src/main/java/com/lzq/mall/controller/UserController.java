package com.lzq.mall.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.lzq.mall.common.ApiRestResponse;
import com.lzq.mall.common.Constant;
import com.lzq.mall.exception.MallException;
import com.lzq.mall.exception.MallExceptionEnum;
import com.lzq.mall.filter.UserFilter;
import com.lzq.mall.model.pojo.User;
import com.lzq.mall.service.EmailService;
import com.lzq.mall.service.UserService;
import com.lzq.mall.utils.EmailUtils;
import com.mysql.cj.util.StringUtils;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    EmailService emailService;
    @GetMapping("/test")
    @ResponseBody
    public User personalPage(){
        return userService.getUser();
    }

    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("username") String username, @RequestParam("password") String password,
                                    @RequestParam("emailAddress") String emailAddress, @RequestParam("verificationCode") String verificationCode) throws MallException {
        if(StringUtils.isNullOrEmpty(username)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isNullOrEmpty(password)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        if(StringUtils.isNullOrEmpty(emailAddress)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_EMAIL);
        }
        if(StringUtils.isNullOrEmpty(verificationCode)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_VERIFICATION_CODE);
        }
        if(password.length() < 8){
            return ApiRestResponse.error(MallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        if(!userService.checkEmailRegistered(emailAddress)){
            return ApiRestResponse.error(MallExceptionEnum.EMAIL_REGISTERED);
        }
        if(!emailService.checkMatch(emailAddress, verificationCode)){
            return ApiRestResponse.error(MallExceptionEnum.WRONG_VERIFICATION);
        }
        userService.register(username, password, emailAddress);
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
//        User currentUser = (User) session.getAttribute(Constant.MALL_USER);
        User currentUser = UserFilter.currentUser;
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
    @PostMapping("/user/sendEmail")
    @ResponseBody
    public ApiRestResponse sendEmail(@RequestParam("emailAddress") String emailAddress){
        boolean valid = EmailUtils.isValidEmailAddress(emailAddress);
        if(valid){
            if(!userService.checkEmailRegistered(emailAddress)){
                return ApiRestResponse.error(MallExceptionEnum.EMAIL_REGISTERED);
            }else{
                String verificationCode = EmailUtils.generateVerification();
                if(emailService.saveVerificationCodeToRedis(emailAddress, verificationCode)){
                    emailService.sendSimpleMessage(emailAddress, Constant.EMAIL_SUBJECT, "welcome to this website, your validation code is " + verificationCode);
                    return ApiRestResponse.success();
                }else{
                    return ApiRestResponse.error(MallExceptionEnum.EMAIL_ALREADY_SENT);
                }
            }
        }else{
            return ApiRestResponse.error(MallExceptionEnum.EMAIL_NOT_VALID);
        }
    }
    @GetMapping("/jwtLogin")
    @ResponseBody
    public ApiRestResponse jwtLogin(@RequestParam("username") String username, @RequestParam("password") String password){
        if(StringUtils.isNullOrEmpty(username)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isNullOrEmpty(password)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(username, password);
        user.setPassword(null);
        Algorithm algorithm = Algorithm.HMAC256(Constant.JWT_KEY);
        String sign = JWT.create().withClaim(Constant.USER_NAME, user.getUsername())
                .withClaim(Constant.USER_ID, user.getId())
                .withClaim(Constant.USER_ROLE, user.getRole())
                .withExpiresAt(new Date(System.currentTimeMillis() + Constant.EXPIRE_TIME))
                .sign(algorithm);
        return ApiRestResponse.success(sign);
    }
    @PostMapping("/jwtAdminlogin")
    @ResponseBody
    public ApiRestResponse jwtAdminLogin(@RequestParam("username") String username, @RequestParam("password") String password) throws MallException {
        if(StringUtils.isNullOrEmpty(username)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isNullOrEmpty(password)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(username, password);
        if (userService.checkAdmin(user)) {
            user.setPassword(null);
            Algorithm algorithm = Algorithm.HMAC256(Constant.JWT_KEY);
            String sign = JWT.create().withClaim(Constant.USER_NAME, user.getUsername())
                    .withClaim(Constant.USER_ID, user.getId())
                    .withClaim(Constant.USER_ROLE, user.getRole())
                    .withExpiresAt(new Date(System.currentTimeMillis() + Constant.EXPIRE_TIME))
                    .sign(algorithm);
            return ApiRestResponse.success(sign);
        }else{
            return ApiRestResponse.error(MallExceptionEnum.NEED_ADMIN);
        }
    }


}
