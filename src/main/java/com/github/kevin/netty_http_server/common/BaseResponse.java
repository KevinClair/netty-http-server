package com.github.kevin.netty_http_server.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseResponse {

    private Integer code;
    private String msg;
    private Object data;
}
