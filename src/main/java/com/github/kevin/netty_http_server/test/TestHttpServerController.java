package com.github.kevin.netty_http_server.test;

import com.github.kevin.netty_http_server.handlers.annotation.HttpServerRequestController;
import com.github.kevin.netty_http_server.handlers.annotation.HttpServerRequestMapping;

@HttpServerRequestController(path = "/test")
public class TestHttpServerController {

    @HttpServerRequestMapping(path = "/index")
    public String index() {
        return "hello world";
    }
}
