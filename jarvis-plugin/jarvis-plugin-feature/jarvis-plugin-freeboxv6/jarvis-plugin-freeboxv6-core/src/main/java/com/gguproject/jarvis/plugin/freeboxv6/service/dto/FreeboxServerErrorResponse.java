package com.gguproject.jarvis.plugin.freeboxv6.service.dto;

public class FreeboxServerErrorResponse {

    private boolean success;

    private AuthenticationStatus error_code;

    public boolean isSuccess(){
        return this.success;
    }

    public AuthenticationStatus getErrorCode(){
        return this.error_code;
    }
}
