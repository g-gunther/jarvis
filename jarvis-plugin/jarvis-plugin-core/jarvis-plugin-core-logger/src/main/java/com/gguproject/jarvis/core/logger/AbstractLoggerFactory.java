package com.gguproject.jarvis.core.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

/**
 * Logger factory
 */
public abstract class AbstractLoggerFactory {
    static {
        System.setProperty(ConfigurationFactory.CONFIGURATION_FACTORY_PROPERTY, JarvisLoggerConfigurationFactory.class.getName());
    }

    /**
     * Get the logger
     *
     * @param clazz Class
     * @return the Logger
     */
    @SuppressWarnings("rawtypes")
    public static com.gguproject.jarvis.core.logger.Logger getLogger(Class clazz) {
        final org.apache.logging.log4j.Logger logger = LogManager.getLogger(clazz);
        return new com.gguproject.jarvis.core.logger.Logger(logger);
    }

    /**
     * Set the name of the current context
     * This name will be used to indicates the context name of a log message
     * and to split logs in separated files
     *
     * @param pluginName
     */
    public static void init(String pluginName) {
        JarvisLoggerConfigurationFactory.CLASSLOADER_NAME = pluginName;
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);

        // reload the configuration to take this new value into account
        ctx.reconfigure();
    }
}
