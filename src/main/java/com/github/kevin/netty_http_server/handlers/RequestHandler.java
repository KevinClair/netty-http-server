package com.github.kevin.netty_http_server.handlers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestHandler {

    /**
     * 实例对象
     */
    private Object bean;

    /**
     * 方法对象
     */
    private Method method;

    /**
     * 参数对象集合
     */
    private List<ParameterObjects> parameterObjects;
}
