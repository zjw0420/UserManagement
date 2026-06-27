package com.usermgr.admin.controller.login;

import com.usermgr.admin.security.AdminOnly;
import com.usermgr.admin.service.LoginService;
import com.usermgr.admin.vo.login.CaptchaVo;
import com.usermgr.admin.vo.login.LoginVo;
import com.usermgr.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录管理")
@RestController
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Operation(summary = "获取图形验证码")
    @GetMapping("/captcha")
    public Result<CaptchaVo> captcha() {
        return Result.ok(loginService.generateCaptcha());
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginVo loginVo, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String token = loginService.login(loginVo, ip);
        return Result.ok(token);
    }

    @AdminOnly
    @Operation(summary = "测试登录态")
    @GetMapping("/whoami")
    public Result<String> whoami() {
        return Result.ok("已登录");
    }

}
