package com.lzq.mall.service.impl;

import com.lzq.mall.common.Constant;
import com.lzq.mall.service.EmailService;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void sendSimpleMessage(String to, String subject, String text){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(Constant.EMAIL_FORM);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setText(text);
        simpleMailMessage.setSubject(subject);
        mailSender.send(simpleMailMessage);
    }
    @Override
    public Boolean saveVerificationCodeToRedis(String emailAddress, String verificationCode){
        RedissonClient client = Redisson.create();
        RBucket<String> bucket = client.getBucket(emailAddress);
        boolean exists = bucket.isExists();
        if(!exists){
            bucket.set(verificationCode, 60, TimeUnit.SECONDS);
            return true;
        }else{
            return false;
        }
    }
    @Override
    public Boolean checkMatch(String emailAddress, String verificationCode){
        RedissonClient client = Redisson.create();
        RBucket<String> bucket = client.getBucket(emailAddress);
        boolean exists = bucket.isExists();
        if(exists){
            String code = bucket.get();
            if(code.equals(verificationCode)){
                return true;
            }
        }
        return false;
    }
}
