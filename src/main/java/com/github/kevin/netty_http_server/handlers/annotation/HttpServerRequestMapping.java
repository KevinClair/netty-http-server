package com.github.kevin.netty_http_server.handlers.annotation;

import com.github.kevin.netty_http_server.handlers.enums.RequestMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpServerRequestMapping {

    @AliasFor("path")
    String value() default "";

    /**
     *  路由映射
     *
     * @return
     */
    @AliasFor("value")
    String path() default "";

    /**
     * 请求方式
     *
     * @return
     */
    RequestMethod[] method() default {RequestMethod.POST};
}
