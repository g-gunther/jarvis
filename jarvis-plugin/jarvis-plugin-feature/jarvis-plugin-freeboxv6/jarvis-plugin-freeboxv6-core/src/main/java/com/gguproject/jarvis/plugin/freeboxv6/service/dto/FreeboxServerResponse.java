package com.gguproject.jarvis.plugin.freeboxv6.service.dto;

public class FreeboxServerResponse<T> {

    private boolean success;

    private T result;

    public boolean isSuccess(){
        return this.success;
    }

    public T getResult(){
        return this.result;
    }
}
