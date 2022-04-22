package com.gguproject.jarvis.plugin.freeboxv6.service.dto;

import java.util.Map;

public class FreeboxServerSessionResponse {

    private String session_token;
    private String challenge;
    private Map<String, Boolean> permissions;

    public String getSessionToken(){
        return this.session_token;
    }

    public Map<String, Boolean> getPermissions(){
        return this.permissions;
    }
}