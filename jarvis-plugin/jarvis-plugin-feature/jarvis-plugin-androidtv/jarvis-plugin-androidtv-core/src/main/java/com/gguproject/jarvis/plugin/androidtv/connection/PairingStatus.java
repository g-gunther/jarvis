package com.gguproject.jarvis.plugin.androidtv.connection;

/**
 * Device pairing result.
 */
public enum PairingStatus {
    /**
     * Pairing successful
     */
    PAIRING_SUCCESS,
    /**
     * Pairing failed due to connection issues
     */
    FAILED_CONNECTION,
    /**
     * Pairing failed due to secret mismatch
     */
    FAILED_SECRET,
    /**
     * User cancelled pairing
     */
    FAILED_CANCELLED,
}