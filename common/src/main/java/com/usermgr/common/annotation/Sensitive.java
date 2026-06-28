package com.usermgr.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记敏感字段。
 * 用于 Jackson 序列化时自动脱敏（不输出到 HTTP response），
 * 以及 AOP 切面拦截日志打印时自动替换为 ***。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {
}
