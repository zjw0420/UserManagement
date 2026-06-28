package com.usermgr.admin.security;

import com.usermgr.common.exception.BusinessException;
import com.usermgr.common.result.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器。
 *
 * 流程: 取 Header token → 验 JWT 签名+过期 → 查 Redis tokenVersion → 扫 @AdminOnly → 鉴权
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private AuthorityChecker authorityChecker;

    @Autowired
    private LoginRateLimiter rateLimiter;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 非 Controller 方法直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 1. 取 token
        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 2. 验 JWT 签名 + 过期
        Long userId = jwtUtil.getUserId(token);
        int tokenVersion = jwtUtil.getTokenVersion(token);
        if (userId == null || tokenVersion < 0) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }

        // 3. 验 Redis tokenVersion（Redis 挂了走降级: 只验 JWT）
        try {
            if (!jwtTokenService.isVersionValid(userId, tokenVersion)) {
                throw new BusinessException(ResultCodeEnum.TOKEN_INVALID);
            }
        } catch (BusinessException e) {
            throw e; // 重新抛出业务异常，不能被吞
        } catch (Exception e) {
            log.warn("Redis 不可用, tokenVersion 校验降级: {}", e.getMessage());
            // Fail Open: Redis 挂时不踢人，JWT 签名+过期仍然有效
        }

        // 4. 扫 @AdminOnly 注解 → 鉴权
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AdminOnly adminOnly = handlerMethod.getMethodAnnotation(AdminOnly.class);
        if (adminOnly != null) {
            if (!authorityChecker.check(userId)) {
                throw new BusinessException(ResultCodeEnum.FORBIDDEN);
            }
        }

        // 5. 存入 ThreadLocal，Controller 可用 getCurrentUserId() 获取
        CURRENT_USER.set(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        CURRENT_USER.remove();
    }

    /**
     * Controller / Service 可通过此方法获取当前登录用户 ID
     */
    public static Long getCurrentUserId() {
        return CURRENT_USER.get();
    }

}
