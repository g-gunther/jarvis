package com.gguproject.jarvis.plugin.freeboxv6.event.exception;

public class CommandServiceException extends Exception {
    private static final long serialVersionUID = -2306913362855451853L;

    public CommandServiceException(Throwable e) {
        super(e);
    }

    public CommandServiceException(String message) {
        super(message);
    }
}
