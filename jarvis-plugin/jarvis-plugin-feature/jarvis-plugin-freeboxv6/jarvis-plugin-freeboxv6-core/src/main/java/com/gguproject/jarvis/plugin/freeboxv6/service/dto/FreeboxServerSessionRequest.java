package com.gguproject.jarvis.plugin.freeboxv6.service.dto;

public class FreeboxServerSessionRequest {

    private String app_id;

    private String password;

    public FreeboxServerSessionRequest(String appId, String password){
        this.app_id = appId;
        this.password = password;
    }
}
