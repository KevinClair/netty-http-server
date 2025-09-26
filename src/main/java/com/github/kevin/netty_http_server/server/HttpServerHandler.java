package com.github.kevin.netty_http_server.server;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.github.kevin.netty_http_server.common.BaseResponse;
import com.github.kevin.netty_http_server.common.HttpServerUtils;
import com.github.kevin.netty_http_server.handlers.ParameterObjects;
import com.github.kevin.netty_http_server.handlers.RequestHandler;
import com.github.kevin.netty_http_server.handlers.RequestHandlerFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ChannelHandler.Sharable
@RequiredArgsConstructor
@Slf4j
public class HttpServerHandler extends io.netty.channel.SimpleChannelInboundHandler<io.netty.handler.codec.http.FullHttpRequest> {

    private final RequestHandlerFactory requestHandlerFactory;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        // 获取请求参数
        String requestData = msg.content().toString(CharsetUtil.UTF_8);
        // 获取Uri
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(msg.uri());
        // 获取请求方式
        HttpMethod httpMethod = msg.method();

        String requestHandlerKey = HttpServerUtils.contactRequestHandlerKey(queryStringDecoder.path(), httpMethod.name());
        // 获取请求处理器
        RequestHandler requestHandler = requestHandlerFactory.getRequestHandler(requestHandlerKey);
        if (Objects.isNull(requestHandler)) {
            // 返回 path not found
            this.sendResponse(ctx, msg, JSON.toJSONString(BaseResponse.builder().code(301).msg("request path error").data("Path not found: " + requestHandlerKey).build()));
        }

        Object invokeResponse = null;
        if (CollectionUtils.isEmpty(requestHandler.getParameterObjects())) {
            invokeResponse = requestHandler.getMethod().invoke(requestHandler.getBean());
        } else {
            List<Object> arguments = new ArrayList<>();
            for (ParameterObjects parameterObject : requestHandler.getParameterObjects()) {
                switch (parameterObject.getParameterType()) {
                    case REQUEST_BODY -> {
                        if (requestData == null || requestData.isEmpty()) {
                            if (parameterObject.getRequired()) {
                                // 返回 参数缺失
                                sendInvalidParameterError(ctx, msg, parameterObject, requestHandlerKey);
                                return;
                            }
                            arguments.add(null);
                        } else {
                            try {
                                // 处理JSON反序列化异常，例如传递的不是json类型的数据
                                arguments.add(JSON.parseObject(requestData, parameterObject.getParameterClass()));
                            } catch (JSONException exception) {
                                this.sendJsonParseError(ctx, msg, parameterObject, requestHandlerKey);
                                return;
                            }
                        }
                    }
                    case REQUEST_PATH_VARIABLE -> log.info("");
                    case REQUEST_PARAM -> {
                        // 解析uri中的Param参数，然后映射到对应的参数对象中
                        List<String> paramValues = queryStringDecoder.parameters().get(parameterObject.getValue());
                        if (CollectionUtils.isEmpty(paramValues)) {
                            if (parameterObject.getRequired()) {
                                // 返回 参数缺失
                                this.sendInvalidParameterError(ctx, msg, parameterObject, requestHandlerKey);
                                return;
                            }
                            arguments.add(null);
                        } else {
                            // 将参数值转换为目标类型
                            try {
                                arguments.add(JSON.parseObject(paramValues.get(0), parameterObject.getParameterClass()));
                            } catch (JSONException exception) {
                                this.sendJsonParseError(ctx, msg, parameterObject, requestHandlerKey);
                                return;
                            }
                        }
                    }
                    case UNKNOWN -> arguments.add(null);
                }

            }
            invokeResponse = requestHandler.getMethod().invoke(requestHandler.getBean(), arguments.toArray());
        }
        this.sendResponse(ctx, msg, JSON.toJSONString(invokeResponse));
    }

    private void sendInvalidParameterError(ChannelHandlerContext ctx, FullHttpRequest msg, ParameterObjects parameterObject, String requestHandlerKey) {
        this.sendResponse(ctx, msg, JSON.toJSONString(BaseResponse.builder().msg("invalid parameter error").code(302).data("Missing required parameter: " + parameterObject.getValue() + " for path " + requestHandlerKey).build()));
    }

    private void sendJsonParseError(ChannelHandlerContext ctx, FullHttpRequest msg, ParameterObjects parameterObject, String requestHandlerKey) {
        this.sendResponse(ctx, msg, JSON.toJSONString(BaseResponse.builder().msg("json parse error").code(303).data("JSON parse error: " + parameterObject.getValue() + " for path: " + requestHandlerKey).build()));
    }


    private void sendResponse(ChannelHandlerContext ctx, FullHttpRequest msg, String responseData) {
        // write response
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(responseData, CharsetUtil.UTF_8));   //  Unpooled.wrappedBuffer(responseJson)
        fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpResponse.content().readableBytes());
        if (HttpUtil.isKeepAlive(msg)) {
            fullHttpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.writeAndFlush(fullHttpResponse);
    }

    @Override
    public void exceptionCaught(io.netty.channel.ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // todo 处理异常
        ctx.close();
    }
}

