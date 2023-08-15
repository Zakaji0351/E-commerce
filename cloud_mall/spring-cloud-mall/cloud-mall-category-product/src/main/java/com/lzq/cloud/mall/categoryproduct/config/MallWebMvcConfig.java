package com.lzq.cloud.mall.categoryproduct.config;

import com.lzq.cloud.mall.categoryproduct.common.ProductConstant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MallWebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/admin/**").addResourceLocations("classpath:/static/admin/");
        registry.addResourceHandler("/images/**").addResourceLocations("file:" + ProductConstant.FILE_UPLOAD_DIR);
        registry.addResourceHandler("swagger-ui.html").addResourceLocations(
                "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");
    }

}
