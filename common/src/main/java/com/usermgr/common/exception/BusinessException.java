package com.usermgr.common.exception;

import com.usermgr.common.result.ResultCodeEnum;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(ResultCodeEnum codeEnum) {
        super(codeEnum.getMessage());
        this.code = codeEnum.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCodeEnum codeEnum, String detail) {
        super(detail);
        this.code = codeEnum.getCode();
    }

}
