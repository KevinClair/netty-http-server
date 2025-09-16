package com.github.kevin.netty_http_server.handlers.annotation;

import com.github.kevin.netty_http_server.handlers.enums.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpServerRequestMapping {

    /**
     * 路径映射
     *
     * @return
     */
    String path();

    /**
     * 请求方式
     *
     * @return
     */
    RequestMethod[] method() default {RequestMethod.POST};
}
