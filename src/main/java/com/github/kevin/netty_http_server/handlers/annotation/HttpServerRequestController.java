package com.github.kevin.netty_http_server.handlers.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HTTP请求控制器注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface HttpServerRequestController {

    @AliasFor("path")
    String value() default "";

    /**
     *  路由映射
     *
     * @return
     */
    @AliasFor("value")
    String path() default "";
}
