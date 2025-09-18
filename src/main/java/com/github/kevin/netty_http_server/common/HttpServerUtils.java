package com.github.kevin.netty_http_server.common;

public class HttpServerUtils {

    /**
     * 组合请求处理器的key，格式：请求方式:请求路径，例如：GET:/api/users
     *
     * @param path   请求路径
     * @param method 请求方式
     * @return
     */
    public static String contactRequestHandlerKey(String path, String method) {
        return method + ":" + path;
    }
}
