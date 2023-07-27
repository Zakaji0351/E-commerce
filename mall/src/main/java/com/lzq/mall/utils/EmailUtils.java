package com.lzq.mall.utils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EmailUtils {
    public static boolean isValidEmailAddress(String email){
        boolean result = true;
        try{
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
        }catch(AddressException e){
            e.printStackTrace();
            result = false;
        }
        return result;
    }
    public static String generateVerification(){
        String s = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        char[] chars = s.toCharArray();
        List<Character> list = new ArrayList<>();
        for(char c : chars){
            list.add(c);
        }
        Collections.shuffle(list);
        String result = "";
        for(int i = 0; i < 6; i++){
            result += list.get(i);
        }
        return result;
    }

    public static void main(String[] args) {

        System.out.println(EmailUtils.generateVerification());
    }
}
