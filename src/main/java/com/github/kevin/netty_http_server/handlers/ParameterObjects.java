package com.github.kevin.netty_http_server.handlers;

import com.github.kevin.netty_http_server.handlers.enums.ParameterTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParameterObjects {

    /**
     * 是否必须
     */
    private Boolean required;

    /**
     * 参数值
     */
    private String value;

    /**
     * 参数类型
     */
    private ParameterTypeEnum parameterType;

    /**
     * 参数的类
     */
    private Class<?> parameterClass;
}
