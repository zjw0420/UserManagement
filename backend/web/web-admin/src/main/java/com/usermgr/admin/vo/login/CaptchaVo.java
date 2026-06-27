package com.usermgr.admin.vo.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "验证码响应")
public class CaptchaVo {

    @Schema(description = "验证码 key，登录时回传")
    private String key;

    @Schema(description = "Base64 图片")
    private String imageBase64;

}
