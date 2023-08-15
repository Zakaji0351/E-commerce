package com.lzq.cloud.mall.common.common;

import com.google.common.collect.Sets;

import com.lzq.cloud.mall.common.exception.MallException;
import com.lzq.cloud.mall.common.exception.MallExceptionEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Constant {
    public static final String SALT = "SDF234sdf!";
    public static final String MALL_USER = "mall_user";
    public static final String EMAIL_FORM = "1402966137@qq.com";
    public static final String EMAIL_SUBJECT = "your validation code";
    public static final Integer IMAGE_SIZE = 400;
    public static final Float OPACITY = 0.4f;
    public static final String WATER_MARK_JPG = "watermark.jpg";


//    public static String FILE_UPLOAD_DIR;
//    @Value("${file.upload.dir}")
//    public void setFileUploadDir(String fileUploadDir){
//        FILE_UPLOAD_DIR = fileUploadDir;
//    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price asc");
    }

    public interface SaleStatus{
        int NOT_SALE = 0;
        int FOR_SALE = 1;
    }
    public interface CheckStatus{
        int UN_CHECKED = 0;
        int CHECKED = 1;
    }
    public enum OrderStatusEnum{
        CANCELED(0,"user canceled the order"),
        NOT_PAID(10,"not paid"),
        PAID(20,"user paid"),
        DELIVERED(30, "order has delivered"),
        FINISHED(40,"order finished");
        private String value;
        private int code;
        public static OrderStatusEnum codeOf(int code){
            for(OrderStatusEnum orderStatusEnum : values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new MallException(MallExceptionEnum.ORDER_NO_ENUM);
        }

        OrderStatusEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
    public static final String JWT_KEY = "zakaji";
    public static final String JWT_TOKEN = "jwt_token";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_ROLE = "user_role";
    public static final Long EXPIRE_TIME = 60 * 1000 * 60 * 24 * 100l;

}
