package com.gguproject.jarvis.plugin.freeboxv6.service.dto;

public class FreeboxServerChallengeResponse {

    private boolean logged_in;

    private String challenge;

    public String getChallenge(){
        return this.challenge;
    }
}
