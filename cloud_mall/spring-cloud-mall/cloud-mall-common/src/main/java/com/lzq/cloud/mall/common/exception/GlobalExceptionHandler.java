package com.lzq.cloud.mall.common.exception;


import com.lzq.cloud.mall.common.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e){
        logger.error("Default Exception:", e);
        return ApiRestResponse.error(MallExceptionEnum.SYSTEM_ERROR);
    }
    @ExceptionHandler(MallException.class)
    @ResponseBody
    public Object handleMallException(MallException e){
        logger.error("Mall Service Exception:", e);
        return ApiRestResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiRestResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        logger.error("MethodArgumentNotValidException:" , e);
        return handleBindingResult(e.getBindingResult());
    }

    private ApiRestResponse handleBindingResult(BindingResult result){
        List<String> list = new ArrayList<>();
        if(result.hasErrors()){
            List<ObjectError> allErrors = result.getAllErrors();
            for(int i = 0; i < allErrors.size(); i++){
                ObjectError objectError = allErrors.get(i);
                String message = objectError.getDefaultMessage();
                list.add(message);
            }
        }
        if(list.size() == 0){
            return ApiRestResponse.error(MallExceptionEnum.REQUEST_PARAM_ERROR);
        }
        return ApiRestResponse.error(MallExceptionEnum.REQUEST_PARAM_ERROR.getCode(), list.toString());
    }
}
