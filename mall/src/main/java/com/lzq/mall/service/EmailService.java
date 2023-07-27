package com.lzq.mall.service;

public interface EmailService {

    void sendSimpleMessage(String to, String subject, String text);

    Boolean saveVerificationCodeToRedis(String emailAddress, String verificationCode);

    Boolean checkMatch(String emailAddress, String verificationCode);
}
