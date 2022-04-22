package com.gguproject.jarvis.plugin.androidtv.connection;

/**
 * Connection status enumeration.
 */
public enum ConnectionStatus {
    /**
     * Connection successful.
     */
    SUCCESS,
    /**
     * Error while creating socket or establishing connection.
     */
    ERROR,
    /**
     * Error during SSL handshake.
     */
    NEEDS_PAIRING
}
