package com.github.kevin.netty_http_server.server;

import com.github.kevin.netty_http_server.handlers.RequestHandlerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServer {

    public HttpServer(HttpServerProperties properties, RequestHandlerFactory requestHandlerFactory) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // start server
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(5 * 1024 * 1024))  // merge request & reponse to FULL
                                    // 添加处理器
                                    .addLast(new HttpServerHandler(requestHandlerFactory));
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // bind
            ChannelFuture channelFuture = bootstrap.bind(properties.getPort()).sync();
            log.info("HttpServer started on port {}", properties.getPort());
            channelFuture.channel().closeFuture().addListener(future -> {
                log.info("HttpServer closed");
                // close server
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("HttpServer start error", e);
        }
    }
}
