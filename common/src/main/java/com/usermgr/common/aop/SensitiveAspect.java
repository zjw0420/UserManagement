package com.usermgr.common.aop;

import com.usermgr.common.annotation.Sensitive;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * AOP 切面 — Controller 入参日志打印前，扫描 @Sensitive 注解，将敏感字段值替换为 ***。
 *
 * 注意：此切面在方法执行后、参数被序列化前介入。
 * 实际项目中如果在 Service 层或 Controller 方法体内手动 log.info(user)，不会被拦截。
 * 这层防护的是"统一日志拦截器"配合使用时，请求参数中的敏感字段。
 */
@Slf4j
@Aspect
@Component
public class SensitiveAspect {

    /**
     * 切点: 所有使用 @Sensitive 注解的类的方法
     * 实际作用范围: 我们主要拦截 Controller 的方法入参 DTO/VO
     */
    @Pointcut("execution(* com.usermgr..controller..*(..))")
    public void controllerPointcut() {}

    @Before("controllerPointcut()")
    public void maskSensitive(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                args[i] = maskObject(args[i]);
            }
        }
    }

    private Object maskObject(Object obj) {
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Sensitive.class)) {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (value != null) {
                        field.set(obj, "***");
                    }
                }
            }
        } catch (Exception e) {
            log.debug("脱敏反射失败: {}", e.getMessage());
        }
        return obj;
    }

}
