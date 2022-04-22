package com.gguproject.jarvis.core.daemon;

/**
 * Support class for daemon threads.
 */
public class DaemonThread extends Thread {

    /**
     * Creates a new daemon thread.
     *
     * @param name Name of the thread. Real thred name will start with the "O2" prefix.
     */
    public DaemonThread(String name) {
        setDaemon(true);
        setName(name);
    }
}
