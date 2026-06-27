package com.usermgr.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("公寓租赁后台管理系统")
                        .version("1.0.0")
                        .description("UserManagement 后台管理 API 文档")
                        .contact(new Contact().name("管理员")));
    }

}
