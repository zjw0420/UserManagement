package com.usermgr.admin.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.usermgr.common.annotation.Sensitive;

import java.io.IOException;
import java.util.List;

/**
 * Jackson 序列化修饰器 — 扫描 @Sensitive 注解，序列化到 HTTP response 时自动输出 "***"。
 *
 * 用法: 在 Jackson ObjectMapper 中注册:
 *   mapper.setSerializerFactory(mapper.getSerializerFactory()
 *       .withSerializerModifier(new SensitiveSerializer()));
 *
 * 配合 WebMvcConfig 的 Jackson 配置自动生效。
 */
public class SensitiveSerializer extends BeanSerializerModifier {

    @Override
    public List<BeanPropertyWriter> changeProperties(
            SerializationConfig config, BeanDescription beanDesc,
            List<BeanPropertyWriter> beanProperties) {

        for (BeanPropertyWriter writer : beanProperties) {
            if (writer.getAnnotation(Sensitive.class) != null) {
                writer.assignSerializer(new SensitiveJsonSerializer(writer));
            }
        }
        return beanProperties;
    }

    private static class SensitiveJsonSerializer extends com.fasterxml.jackson.databind.JsonSerializer<Object> {

        private final BeanPropertyWriter writer;

        SensitiveJsonSerializer(BeanPropertyWriter writer) {
            this.writer = writer;
        }

        @Override
        public void serialize(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
            try {
                Object value = writer.get(bean);
                if (value != null) {
                    gen.writeString("***");
                } else {
                    gen.writeNull();
                }
            } catch (Exception e) {
                gen.writeNull();
            }
        }
    }
}
