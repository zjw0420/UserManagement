package com.usermgr.admin.config;

import com.usermgr.common.exception.BusinessException;
import com.usermgr.common.result.Result;
import com.usermgr.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * 全局异常处理器 (common 模块)。
 *
 * 分层异常处理: 各模块在自己 classpath 范围内捕获异常。
 *   common  → 业务异常 / 参数校验 / 文件IO / 兜底
 *   service → DataAccessException (Step 3 补)
 *   web-admin → JWT / Redis / MinIO (Step 5 补)
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 业务异常 ──

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e, jakarta.servlet.http.HttpServletResponse response) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        // 认证相关错误设置 HTTP 401，前端才能正确拦截
        if (e.getCode() == ResultCodeEnum.UNAUTHORIZED.getCode()
                || e.getCode() == ResultCodeEnum.TOKEN_INVALID.getCode()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        } else if (e.getCode() == ResultCodeEnum.FORBIDDEN.getCode()) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
        return Result.fail(e.getCode(), e.getMessage());
    }

    // ── 参数校验 ──

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("参数异常: {}", e.getMessage());
        return Result.fail(ResultCodeEnum.BAD_REQUEST.getCode(), e.getMessage());
    }

    // ── 认证/鉴权异常 (安全模块未就绪前，用 JDK 自带的 SecurityException) ──

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleSecurity(SecurityException e) {
        log.warn("安全异常: {}", e.getMessage());
        return Result.fail(ResultCodeEnum.FORBIDDEN);
    }

    // ── 文件 IO 异常 ──

    @ExceptionHandler({IOException.class})
    public Result<Void> handleIO(IOException e) {
        log.error("文件操作异常: {}", e.getMessage(), e);
        return Result.fail(ResultCodeEnum.FILE_UPLOAD_ERROR);
    }

    // ── 兜底 (所有未捕获异常) ──

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleUnknown(Exception e) {
        log.error("未知异常: {}", e.getMessage(), e);
        return Result.fail(ResultCodeEnum.INTERNAL_ERROR);
    }

}
