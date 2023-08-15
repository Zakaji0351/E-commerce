package com.lzq.cloud.mall.zuul.filter;
/**
 * 管理员过滤器
 */

import com.lzq.cloud.mall.common.common.Constant;
import com.lzq.cloud.mall.user.model.pojo.User;
import com.lzq.cloud.mall.zuul.feign.UserFeignClient;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
@Component
public class AdminFilter extends ZuulFilter {
    @Autowired
    UserFeignClient userFeignClient;
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String requestURI = request.getRequestURI();
        if(requestURI.contains("admin")){
            return true;
        }
        if(requestURI.contains("adminLogin")){
            return false;
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute(Constant.MALL_USER);
        if(currentUser == null){
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseBody("{\n"
                    + "\"status\": 10009,\n"
                    + "\"msg\": \"NEED_LOGIN\",\n"
                    + "\"data\": null\n"
                    + "}");
            currentContext.setResponseStatusCode(200);
            return null;
        }
        Boolean adminRole = userFeignClient.checkAdminRole(currentUser);
        if(!adminRole){
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseBody("{\n"
                    + "\"status\": 10010,\n"
                    + "\"msg\": \"NEED_ADMIN\",\n"
                    + "\"data\": null\n"
                    + "}");
            currentContext.setResponseStatusCode(200);
        }
        return null;
    }
}
