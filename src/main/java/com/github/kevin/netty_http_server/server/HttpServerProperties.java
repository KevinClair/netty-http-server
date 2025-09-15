package com.github.kevin.netty_http_server.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "http-server")
public class HttpServerProperties {

    private Boolean enable = true;

    private Integer port = 27077;
}
