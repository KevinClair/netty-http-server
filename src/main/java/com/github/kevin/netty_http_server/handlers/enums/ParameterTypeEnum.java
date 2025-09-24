package com.github.kevin.netty_http_server.handlers.enums;

public enum ParameterTypeEnum {
    /**
     * 请求Body
     */
    REQUEST_BODY,

    /**
     * 请求参数
     */
    REQUEST_PARAM,

    /**
     * 请求头
     */
    REQUEST_HEADER,

    /**
     * 请求路径
     */
    REQUEST_PATH,

    /**
     * 未知
     */
    UNKNOWN
}
