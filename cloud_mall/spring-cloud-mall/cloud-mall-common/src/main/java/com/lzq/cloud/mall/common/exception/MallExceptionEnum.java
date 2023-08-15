package com.lzq.cloud.mall.common.exception;

public enum MallExceptionEnum {
    REQUEST_PARAM_ERROR(10001, "parameter error, try again"),
    ORDER_NO_ENUM(10002, "we can't find such enumeration"),
    NEED_USER_NAME(10003, "username required"),
    NEED_PASSWORD(10004, "password required"),
    PASSWORD_TOO_SHORT(10005, "you need a longer password"),
    USER_EXISTED(10006, "user existed with same username"),
    INSERT_FAILURE(10006, "failed to create user"),
    WRONG_PASSWORD(10007, "username and password don't match"),
    SIGNATURE_UPDATE_FAILURE(10008, "failed to update the signature"),
    NEED_LOGIN(10009, "need login"),
    NEED_ADMIN(10010, "need admin authority"),
    CATEGORY_EXISTED(10011, "category already existed"),
    CATEGORY_ADD_FAILURE(10012, "failed to add new category"),
    CATEGORY_UPDATE_FAILURE(10013, "failed to update the category"),
    CATEGORY_DELETE_FAILTURE(10014, "failed to delete the category"),
    PRODUCT_EXISTED(10015,"product already existed"),
    PRODUCT_ADD_FAILURE(10016, "failed to add new product"),
    PRODUCT_UPDATE_FAILURE(10017, "failed to update product"),
    PRODUCT_DELETE_FAILURE(10018, "failed to delete product"),
    MKDIR_FAILURE(10019,"failed to make directories"),
    IMAGE_UPLOAD_FAILURE(10020, "failed to upload image"),
    NOT_SALE(10021, "the current product is not for sale"),
    PRODUCT_NOT_ENOUGH(10022, "this product doesn't have enough in stock"),
    CART_UPDATE_FAILURE(10023, "failed to update cart information"),
    CART_DELETE_FAILURE(10024, "failed to delete cart"),
    CART_SELECT_EMPTY(10025, "you haven't selected any item in your cart"),
    ORDER_NOT_FOUND(10026, "order not found"),
    ORDER_NOT_MATCH(10027, "order doesn't match"),
    ORDER_STATUS_EXCEPTION(10028, "wrong order status"),

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
