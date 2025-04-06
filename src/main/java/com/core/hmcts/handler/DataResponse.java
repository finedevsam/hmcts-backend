package com.core.hmcts.handler;

import org.springframework.stereotype.Component;

@Component
public class DataResponse {
    public ResponseData responseData(int code, String message, Object data){
        return new ResponseData(code, message, data);
    }
    public record ResponseData(int code, String message, Object data) {}
}
