package com.usermgr.admin.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA 路由回退: 所有非 /admin/ 的路径交给 React Router 处理。
 * 前端打包在 resources/static/ 下, index.html 自动映射到 /。
 */
@Controller
public class SpaController {

    /**
     * 匹配所有路径, 但 Spring MVC 会优先匹配已有的 @RestController (/admin/**) 和静态资源。
     * 这里只作为兜底, 转发到 index.html 让 React Router 接管。
     */
    @RequestMapping(value = "/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }

    @RequestMapping(value = "/{path:[^\\.]*}/**")
    public String forwardNested() {
        return "forward:/index.html";
    }
}
