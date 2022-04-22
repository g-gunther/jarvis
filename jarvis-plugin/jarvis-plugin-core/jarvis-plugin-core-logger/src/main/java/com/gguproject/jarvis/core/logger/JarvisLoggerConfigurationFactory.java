package com.gguproject.jarvis.core.logger;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;

/**
 * Custom logger configuration
 * Used to add a property which is dynamically set
 */
public class JarvisLoggerConfigurationFactory extends ConfigurationFactory {

    public static String CLASSLOADER_NAME = null;

    @Override
    protected String[] getSupportedTypes() {
        return new String[]{".xml", "*"};
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
        return new CustomConfiguration(loggerContext, source);
    }

    public class CustomConfiguration extends XmlConfiguration {

        CustomConfiguration(LoggerContext context, ConfigurationSource configSource) {
            super(context, configSource);
        }

        @Override
        protected void doConfigure() {
            if (CLASSLOADER_NAME == null) {
                this.getProperties().put("classLoaderName", "main");
            } else {
                this.getProperties().put("classLoaderName", CLASSLOADER_NAME);
            }
            super.doConfigure();
        }
    }
}
