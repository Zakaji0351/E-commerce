package com.lzq.cloud.mall.cartorder.filter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Configuration
@EnableFeignClients
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //通过RequestContextHolder获取到请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes == null)return;
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        if(headerNames != null){
            while(headerNames.hasMoreElements()){
                String name = headerNames.nextElement();
                Enumeration<String> values = request.getHeaders(name);
                while(values.hasMoreElements()){
                    String s = values.nextElement();
                    requestTemplate.header(name, s);
                }
            }
        }
    }
}
