package com.github.kevin.netty_http_server.server;

import com.alibaba.fastjson2.JSON;
import com.github.kevin.netty_http_server.common.HttpServerUtils;
import com.github.kevin.netty_http_server.handlers.RequestHandler;
import com.github.kevin.netty_http_server.handlers.RequestHandlerFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@ChannelHandler.Sharable
@RequiredArgsConstructor
public class HttpServerHandler extends io.netty.channel.SimpleChannelInboundHandler<io.netty.handler.codec.http.FullHttpRequest> {

    private final RequestHandlerFactory requestHandlerFactory;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        // 获取请求参数
        String requestData = msg.content().toString(CharsetUtil.UTF_8);
        // 获取Uri
        String uri = msg.uri();
        // 获取请求方式
        HttpMethod httpMethod = msg.method();

        String requestHandlerKey = HttpServerUtils.contactRequestHandlerKey(uri, httpMethod.name());
        // 获取请求处理器
        RequestHandler requestHandler = requestHandlerFactory.getRequestHandler(requestHandlerKey);
        if (Objects.isNull(requestHandler)) {
            // 返回 path not found

        }

        Object invokeResponse = null;
        if (requestHandler.getArgs().length == 0) {
            invokeResponse = requestHandler.getMethod().invoke(requestHandler.getBean());
        } else {
            // todo 解析参数
        }
        // write response
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(JSON.toJSONString(invokeResponse), CharsetUtil.UTF_8));   //  Unpooled.wrappedBuffer(responseJson)
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
