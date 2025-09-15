package com.github.kevin.netty_http_server;

import com.github.kevin.netty_http_server.server.HttpServer;
import com.github.kevin.netty_http_server.server.HttpServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(prefix = "http-server", name = "enable", havingValue = "true")
@EnableConfigurationProperties(HttpServerProperties.class)
public class NettyHttpServerAutoConfiguration {

    /**
     * Http服务器
     *
     * @param properties 配置属性
     * @return
     */
    @Bean
    public HttpServer httpServer(HttpServerProperties properties) {
        return new HttpServer(properties);
    }
}
