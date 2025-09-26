package com.github.kevin.netty_http_server.handlers.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpServerRequestParam {

    /**
     * 参数名称
     *
     * @return
     */
    String value();

    /**
     * 是否必须
     *
     * @return
     */
    boolean required() default true;
}
