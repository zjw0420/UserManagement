package com.usermgr.admin.vo.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录请求")
public class LoginVo {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Schema(description = "验证码 key")
    private String captchaKey;

    @Schema(description = "验证码")
    private String captchaCode;

}
