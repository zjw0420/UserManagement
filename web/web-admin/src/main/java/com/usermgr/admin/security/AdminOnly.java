package com.usermgr.admin.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 管理员专属接口。
 * 标记在 Controller 方法上，LoginInterceptor 扫描到此注解后调用 AuthorityChecker 鉴权。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminOnly {
}
