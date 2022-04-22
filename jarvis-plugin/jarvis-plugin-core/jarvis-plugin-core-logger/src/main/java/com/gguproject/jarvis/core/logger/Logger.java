/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gguproject.jarvis.core.logger;

/**
 * Specific Logger, based on slf4j Logger
 * But implements only debug method and an event method.
 * Event method get the event text from a ResouceBundle
 */
public class Logger {
    /**
     * Logger
     */
    private org.apache.logging.log4j.Logger logger;

    /**
     * Constructor
     *
     * @param logger Logger
     */
    public Logger(org.apache.logging.log4j.Logger logger) {
        this.logger = logger;
    }

    /**
     * Get logger name
     *
     * @return name
     */
    public String getName() {
        return logger.getName();
    }

    /**
     * Check if the debug is enabled or not
     *
     * @return true or false
     */
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * debug log
     *
     * @param string message
     */
    public void debug(String string) {
    	logger.debug(string);
    }

    /**
     * Debug log
     *
     * @param string Message
     * @param o Parameters
     */
    public void debug(String string, Object... o) {
    	logger.debug(string, o);
    }

    /**
     * Debug log
     *
     * @param string Message
     * @param thrwbl Exception
     */
    public void debug(String string, Throwable thrwbl) {
    		logger.debug(string, thrwbl);
    }

    /**
     * Warn log
     *
     * @param string message
     */
    public void warn(String string) {
    		logger.warn(string);
    }

    /**
     * Warn log
     *
     * @param string Message
     * @param o Parameters
     */
    public void warn(String string, Object... o) {
    		logger.warn(string, o);
    }

    /**
     * Warn log
     *
     * @param string Message
     * @param thrwbl Exception
     */
    public void warn(String string, Throwable thrwbl) {
    		logger.warn(string, thrwbl);
    }

    /**
     * Error log
     *
     * @param string message
     */
    public void error(String string) {
    		logger.warn(string);
    }

    /**
     * Error log
     *
     * @param string Message
     * @param o Parameters
     */
    public void error(String string, Object... o) {
    		logger.error(string, o);
    }

    /**
     * Error log
     *
     * @param string Message
     * @param thrwbl Exception
     */
    public void error(String string, Throwable thrwbl) {
    		logger.error(string, thrwbl);
    }

    /**
     * Info log
     * @param string Message
     */
    public void info(String string){
    		logger.info(string);
    }

    /**
     * Info log
     * @param string Messages
     * @param o Parameters
     */
    public void info(String string, Object...o ){
    		logger.info(string, o);
    }

    /**
     * Check if the trace mode is enabled or not
     *
     * @return true or false
     */
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    /**
     * Trace log
     * @param string Message
     */
    public void trace(String string){
    		logger.trace(string);
    }

    /**
     * Trace log
     * @param string Messages
     * @param o Parameters
     */
    public void trace(String string, Object...o ){
    		logger.trace(string, o);
    }
}
