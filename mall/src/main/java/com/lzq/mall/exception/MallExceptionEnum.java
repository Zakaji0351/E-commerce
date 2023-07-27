package com.lzq.mall.exception;

public enum MallExceptionEnum {
    NEED_USER_NAME(10001, "username can't be null"),
    NEED_PASSWORD(10002, "password can't be null"),
    PASSWORD_TOO_SHORT(10003, "password too short"),
    USER_EXISTED(10004, "user already existed"),
    INSERT_FAILURE(10005, "insert user failed"),
    WRONG_PASSWORD(10006, "wrong password"),
    NEED_LOGIN(10007, "you need login first"),
    SIGNATURE_UPDATE_FAILURE(10008, "signature update failed"),
    NEED_ADMIN(10009, "need admin access"),
    NAME_NOT_NULL(10010, "category arguments not null"),
    CATEGORY_EXISTED(10011, "category already existed"),
    CATEGORY_ADD_FAILURE(10012, "category add failed"),
    REQUEST_PARAM_ERROR(10013, "request param error"),
    CATEGORY_UPDATE_FAILURE(10014, "category update failed"),
    CATEGORY_DELETE_FAILTURE(10015, "category delete failed"),
    PRODUCT_EXISTED(100016, "product already existed"),
    PRODUCT_ADD_FAILURE(10017, "product add failed"),
    MKDIR_FAILURE(10018, "directory created failed"),
    IMAGE_UPLOAD_FAILURE(10019, "upload image failed"),
    PRODUCT_UPDATE_FAILURE(10020, "product update failed"),
    PRODUCT_DELETE_FAILURE(10021, "product delete failed"),
    NOT_SALE(10022, "product is not avaiable for sale"),
    PRODUCT_NOT_ENOUGH(10023, "product quantity is not enough"),
    CART_UPDATE_FAILURE(10024, "cart update failed"),
    CART_DELETE_FAILURE(10025, "cart delte failed"),
    CART_SELECT_EMPTY(10026, "none of the item in the cart is selected"),
    ORDER_NO_ENUM(10027,"no specific exception of the order is found"),
    ORDER_NOT_FOUND(10028,"no order found"),
    ORDER_NOT_MATCH(10029,"order user not match"),
    ORDER_STATUS_EXCEPTION(10030, "your order can't be processed due to its status"),
    EMAIL_NOT_VALID(10031, "your email address is illegal"),
    EMAIL_REGISTERED(10032, "this email address has been registered"),
    EMAIL_ALREADY_SENT(10033, "your email has sent successfully, try again in 60s"),
    NEED_EMAIL(10034, "email address can't be null"),
    NEED_VERIFICATION_CODE(10035, "verification code can't be null"),
    WRONG_VERIFICATION(10036, "wrong verification code"),
    TOKEN_EXPIRE(10037, "your jwt token has expired"),
    TOKEN_WRONG(10038, "wrong jwt token"),
    PRODUCT_PRICE_LOW(10039, "the price of the product can't be less than 1"),
    PRODUCT_STOCK_HIGH(10040, "the stock of the product can't be more than 10000"),
    SYSTEM_ERROR(20000, "system exception");

    Integer code;
    String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    MallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
