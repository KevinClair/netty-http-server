package com.github.kevin.netty_http_server.common;

import com.github.kevin.netty_http_server.handlers.RequestHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 提取路径变量
     *
     * @param requestHandler 路径模式，例如：/api/{test}
     * @param requestPath    请求路径，例如：/api/123
     * @return 路径变量的键值对，例如：{test=123}
     */
    public static Map<String, String> extractPathVariables(RequestHandler requestHandler, String requestPath) {
        Map<String, String> pathVariables = new HashMap<>();
        // 将路径模式中的占位符 {xxx} 替换为正则表达式捕获组 ([^/]+)
        Matcher matcher = requestHandler.getPathPattern().matcher(requestPath);

        // 如果匹配成功，提取命名捕获组的值
        if (matcher.matches()) {
            Pattern groupPattern = Pattern.compile("\\{([^/]+)}");
            Matcher groupMatcher = groupPattern.matcher(requestHandler.getRequestHandlerKey());
            while (groupMatcher.find()) {
                String groupName = groupMatcher.group(1);
                pathVariables.put(groupName, matcher.group(groupName));
            }
        }
        return pathVariables;
    }
}
