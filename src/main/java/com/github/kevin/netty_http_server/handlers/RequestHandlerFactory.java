package com.github.kevin.netty_http_server.handlers;

import com.github.kevin.netty_http_server.common.HttpServerException;
import com.github.kevin.netty_http_server.common.HttpServerUtils;
import com.github.kevin.netty_http_server.handlers.annotation.*;
import com.github.kevin.netty_http_server.handlers.enums.ParameterTypeEnum;
import com.github.kevin.netty_http_server.handlers.enums.RequestMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RequestHandlerFactory implements ApplicationListener<ApplicationReadyEvent> {

    private Map<String, RequestHandler> handlerMap = new HashMap<>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        // 扫描所有包含HttpServerRequestController注解的Bean
        applicationContext.getBeansWithAnnotation(HttpServerRequestController.class).forEach((name, bean) -> {
            HttpServerRequestController requestController = AnnotationUtils.findAnnotation(bean.getClass(), HttpServerRequestController.class);
            // 扫描类中包含所有HttpServerRequestMapping注解的方法
            Method[] methods = bean.getClass().getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(HttpServerRequestMapping.class)) {
                    HttpServerRequestMapping requestMapping = AnnotationUtils.findAnnotation(method, HttpServerRequestMapping.class);
                    String fullPath = HttpServerUtils.joinPaths(requestController.path(), requestMapping.path());
                    // 解析请求方法
                    for (RequestMethod requestMethod : requestMapping.method()) {
                        String requestHandlerKey = requestMethod.name() + ":" + fullPath;
                        if (handlerMap.containsKey(requestHandlerKey)) {
                            throw new HttpServerException("Duplicate request requestMapping: " + requestHandlerKey);
                        }
                        Parameter[] parameters = method.getParameters();
                        if (parameters.length == 0) {
                            handlerMap.put(requestHandlerKey, new RequestHandler(bean, method, null));
                            log.info("HttpServer register request requestMapping: {}, method:{}", requestHandlerKey, bean.getClass().getName() + "#" + method.getName());
                            continue;
                        }
                        // 解析parameters中被@HttpServerRequestBody注解的参数
                        List<ParameterObjects> parameterObjects = new ArrayList<>();
                        for (Parameter parameter : parameters) {
                            if (parameter.getAnnotations().length == 0) {
                                parameterObjects.add(ParameterObjects.builder().required(false).parameterType(ParameterTypeEnum.UNKNOWN).parameterClass(parameter.getType()).build());
                                continue;
                            }
                            for (Annotation annotation : parameter.getAnnotations()) {
                                ParameterObjects.ParameterObjectsBuilder parameterObjectsBuilder = ParameterObjects.builder();
                                if (annotation instanceof HttpServerRequestBody httpServerRequestBody) {
                                    // 解析参数类型
                                    parameterObjects.add(parameterObjectsBuilder.required(httpServerRequestBody.required()).parameterType(ParameterTypeEnum.REQUEST_BODY).parameterClass(parameter.getType()).value(parameter.getName()).build());
                                } else if (annotation instanceof HttpServerRequestParam httpServerRequestParam) {
                                    // 解析RequestParam参数
                                    parameterObjects.add(parameterObjectsBuilder.required(httpServerRequestParam.required()).parameterType(ParameterTypeEnum.REQUEST_PARAM).parameterClass(parameter.getType()).value(httpServerRequestParam.value()).build());
                                } else if (annotation instanceof HttpServerRequestPathVariable) {
                                    // 解析PathVariable参数
                                    parameterObjects.add(parameterObjectsBuilder.required(true).parameterType(ParameterTypeEnum.REQUEST_PATH_VARIABLE).parameterClass(parameter.getType()).value(parameter.getName()).build());
                                } else {
                                    parameterObjects.add(parameterObjectsBuilder.required(false).parameterType(ParameterTypeEnum.UNKNOWN).parameterClass(parameter.getType()).build());
                                }
                            }
                        }
                        handlerMap.put(requestHandlerKey, new RequestHandler(bean, method, parameterObjects));
                        log.info("HttpServer register request requestMapping: {}, method:{}", requestHandlerKey, bean.getClass().getName() + "#" + method.getName());
                    }
                }
            }
        });
    }

    /**
     * 获取请求处理器
     *
     * @param requestHandlerKey 请求处理器Key，格式为：请求方法:路径，例如 GET:/api/test
     * @return
     * @throws HttpServerException
     */
    public RequestHandler getRequestHandler(String requestHandlerKey) {
        return handlerMap.get(requestHandlerKey);
    }
}
