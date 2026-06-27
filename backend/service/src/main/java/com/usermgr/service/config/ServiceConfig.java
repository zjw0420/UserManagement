package com.usermgr.service.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan("com.usermgr.service.mapper")
@EnableTransactionManagement
public class ServiceConfig {
}
