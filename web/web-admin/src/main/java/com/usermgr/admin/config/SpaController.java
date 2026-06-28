package com.usermgr.admin.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA 路由回退: 非 API、非静态资源的路径交给 React Router。
 * 最后一层路径不含 "." (即不是 .js/.css/.svg 等静态文件) 时转发到 index.html。
 */
@Controller
public class SpaController {

    @RequestMapping(value = "/**/{path:[^\\.]+}")
    public String forward() {
        return "forward:/index.html";
    }
}
