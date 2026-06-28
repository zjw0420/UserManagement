package com.usermgr.common.result;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或 token 已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 业务异常
    USER_NOT_FOUND(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    ACCOUNT_DISABLED(1003, "账号已被禁用"),
    ACCOUNT_LOCKED(1004, "账号已被锁定，请联系管理员"),
    TOKEN_INVALID(1005, "Token 无效"),
    CAPTCHA_ERROR(1006, "验证码错误"),
    RATE_LIMITED(1007, "操作过于频繁，请稍后再试"),

    // 数据异常
    DB_ERROR(2001, "数据库异常"),
    REDIS_ERROR(2002, "缓存服务异常"),
    FILE_UPLOAD_ERROR(2003, "文件上传失败"),

    // 业务规则
    HAS_ACTIVE_LEASE(3001, "该公寓存在进行中的租赁合同，无法删除"),
    HAS_ACTIVE_APPOINTMENT(3002, "该公寓存在未完成的预约，无法删除"),
    FILE_TYPE_NOT_ALLOWED(3003, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(3004, "文件大小超过限制"),
    PASSWORD_TOO_WEAK(3005, "密码强度不足"),
    ;

    private final int code;
    private final String message;

    ResultCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
