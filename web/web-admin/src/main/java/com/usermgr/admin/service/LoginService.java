package com.usermgr.admin.service;

import com.usermgr.admin.vo.login.CaptchaVo;
import com.usermgr.admin.vo.login.LoginVo;

public interface LoginService {

    /** 生成验证码 */
    CaptchaVo generateCaptcha();

    /** 登录: 验证码+密码校验 → 返回 JWT token */
    String login(LoginVo loginVo, String ip);

}
