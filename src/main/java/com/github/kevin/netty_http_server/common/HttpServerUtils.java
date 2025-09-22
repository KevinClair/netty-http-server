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

    public static String joinPaths(String requestControllerPath, String requestMappingPath) {
        if (requestControllerPath == null || requestControllerPath.isEmpty()) {
            requestControllerPath = "/";
        } else if (!requestControllerPath.startsWith("/")) {
            requestControllerPath = "/" + requestControllerPath;
        }

        if (requestMappingPath == null || requestMappingPath.isEmpty()) {
            requestMappingPath = "";
        } else if (requestMappingPath.startsWith("/")) {
            requestMappingPath = requestMappingPath.substring(1);
        }

        if (requestControllerPath.endsWith("/")) {
            requestControllerPath = requestControllerPath.substring(0, requestControllerPath.length() - 1);
        }

        return requestControllerPath + "/" + requestMappingPath;
    }
}
