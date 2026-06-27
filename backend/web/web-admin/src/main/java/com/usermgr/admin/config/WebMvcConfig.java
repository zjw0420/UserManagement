package com.usermgr.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usermgr.admin.security.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 注册登录拦截器。
     * 放行: /admin/captcha, /admin/login, /doc.html, /swagger-ui, /v3/api-docs
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/captcha",
                        "/admin/login"
                );
    }

    /**
     * 注册 SensitiveSerializer: Jackson 序列化时扫描 @Sensitive 注解自动脱敏
     */
    @PostConstruct
    public void registerSensitiveSerializer() {
        objectMapper.setSerializerFactory(
                objectMapper.getSerializerFactory()
                        .withSerializerModifier(new SensitiveSerializer())
        );
    }

}
