package com.usermgr.common.log;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Logback 脱敏转换器 — 兜底第三层。
 *
 * 第一层: SensitiveSerializer（Jackson 拦截 JSON 序列化到 HTTP response）
 * 第二层: SensitiveAspect（AOP 切面 Controller 入参反射替换）
 * 第三层: 本类（Logback 正则兜底，任何漏到日志里的敏感字段都会被替换）
 *
 * 用法: logback-spring.xml 里
 *   <conversionRule conversionWord="msg" converterClass="com.usermgr.common.log.SensitiveConverter"/>
 */
public class SensitiveConverter extends MessageConverter {

    // 匹配: password="xxx", "password":"xxx", password:xxx 等常见格式
    private static final java.util.regex.Pattern PWD =
            java.util.regex.Pattern.compile("(password[\"'=:\\s]+)([^,\"'\\s}]+)", java.util.regex.Pattern.CASE_INSENSITIVE);
    private static final java.util.regex.Pattern ID_CARD =
            java.util.regex.Pattern.compile("(idCard[\"'=:\\s]+)([^,\"'\\s}]+)", java.util.regex.Pattern.CASE_INSENSITIVE);
    private static final java.util.regex.Pattern PHONE =
            java.util.regex.Pattern.compile("(\\b1[3-9]\\d{9}\\b)");

    @Override
    public String convert(ILoggingEvent event) {
        String msg = event.getFormattedMessage();
        msg = PWD.matcher(msg).replaceAll("$1***");
        msg = ID_CARD.matcher(msg).replaceAll("$1***");
        msg = PHONE.matcher(msg).replaceAll(m -> m.group().substring(0, 3) + "****" + m.group().substring(7));
        return msg;
    }
}
