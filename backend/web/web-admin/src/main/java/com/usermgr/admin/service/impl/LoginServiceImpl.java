package com.usermgr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.usermgr.admin.security.JwtTokenService;
import com.usermgr.admin.security.JwtUtil;
import com.usermgr.admin.security.LoginRateLimiter;
import com.usermgr.admin.service.LoginService;
import com.usermgr.admin.vo.login.CaptchaVo;
import com.usermgr.admin.vo.login.LoginVo;
import com.usermgr.common.exception.BusinessException;
import com.usermgr.common.result.ResultCodeEnum;
import com.usermgr.model.entity.SystemUser;
import com.usermgr.service.mapper.SystemUserMapper;
import com.wf.captcha.SpecCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    private static final long CAPTCHA_EXPIRE_MINUTES = 5;

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private LoginRateLimiter rateLimiter;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public CaptchaVo generateCaptcha() {
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        String code = captcha.text().toLowerCase();
        String key = UUID.randomUUID().toString();

        // 验证码存 Redis, 5 分钟过期
        redisTemplate.opsForValue().set("captcha:" + key, code, CAPTCHA_EXPIRE_MINUTES, TimeUnit.MINUTES);

        return new CaptchaVo(key, captcha.toBase64());
    }

    @Override
    public String login(LoginVo vo, String ip) {
        String username = vo.getUsername();

        // 限流检查
        rateLimiter.checkRateLimit(ip, username);

        // 验证码校验
        String captchaCode = redisTemplate.opsForValue().get("captcha:" + vo.getCaptchaKey());
        if (captchaCode == null || !captchaCode.equalsIgnoreCase(vo.getCaptchaCode())) {
            rateLimiter.recordFailed(ip, username);
            throw new BusinessException(ResultCodeEnum.CAPTCHA_ERROR);
        }
        redisTemplate.delete("captcha:" + vo.getCaptchaKey());

        // 查用户
        SystemUser user = systemUserMapper.selectOne(
                new LambdaQueryWrapper<SystemUser>()
                        .eq(SystemUser::getUsername, username)
        );
        if (user == null) {
            rateLimiter.recordFailed(ip, username);
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }

        // 状态检查
        if (user.getStatus() != null && user.getStatus() == com.usermgr.model.enums.BaseStatus.DISABLE) {
            throw new BusinessException(ResultCodeEnum.ACCOUNT_DISABLED);
        }

        // 锁检查
        if (rateLimiter.shouldLockAccount(username)) {
            user.setStatus(com.usermgr.model.enums.BaseStatus.DISABLE);
            systemUserMapper.updateById(user);
            throw new BusinessException(ResultCodeEnum.ACCOUNT_LOCKED);
        }

        // 密码校验
        if (!passwordEncoder.matches(vo.getPassword(), user.getPassword())) {
            rateLimiter.recordFailed(ip, username);
            throw new BusinessException(ResultCodeEnum.PASSWORD_ERROR);
        }

        // 登录成功: 清除限流计数
        rateLimiter.clearFailed(ip, username);

        // 签发 JWT
        int version = jwtTokenService.getOrInitVersion(user.getId());
        String token = jwtUtil.generate(user.getId(), version);

        log.info("用户 {} (id={}) 登录成功", username, user.getId());
        return token;
    }

}
