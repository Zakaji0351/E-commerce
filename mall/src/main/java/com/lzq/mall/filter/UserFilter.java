package com.lzq.mall.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lzq.mall.common.Constant;
import com.lzq.mall.exception.MallException;
import com.lzq.mall.exception.MallExceptionEnum;
import com.lzq.mall.model.pojo.User;
import com.lzq.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class UserFilter implements Filter {
    public static User currentUser = new User();
    @Autowired
    UserService userService;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpSession session = request.getSession();
//        currentUser = (User) session.getAttribute(Constant.MALL_USER);
        String token = request.getHeader(Constant.JWT_TOKEN);

//        if(currentUser == null)
        if(token == null){
//            return ApiRestResponse.error(MallExceptionEnum.NEED_LOGIN);
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("{\n"
                    + "\"status\": 10007,\n"
                    + "\"msg\": \"NEED_LOGIN\",\n"
                    + "\"data\": null\n"
                    + "}");
            out.flush();
            out.close();
            return;
        }
        Algorithm algorithm = Algorithm.HMAC256(Constant.JWT_KEY);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try{
            DecodedJWT jwt = verifier.verify(token);
            currentUser.setId(jwt.getClaim(Constant.USER_ID).asInt());
            currentUser.setRole(jwt.getClaim(Constant.USER_ROLE).asInt());
            currentUser.setUsername(jwt.getClaim(Constant.USER_NAME).asString());
        }catch (TokenExpiredException e){
            throw new MallException(MallExceptionEnum.TOKEN_EXPIRE);
        }catch(JWTDecodeException e){
            throw new MallException(MallExceptionEnum.TOKEN_WRONG);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
