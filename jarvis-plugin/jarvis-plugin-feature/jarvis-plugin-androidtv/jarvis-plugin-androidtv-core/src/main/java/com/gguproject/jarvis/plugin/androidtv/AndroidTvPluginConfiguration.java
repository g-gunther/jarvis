package com.gguproject.jarvis.plugin.androidtv;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class AndroidTvPluginConfiguration extends AbstractPluginConfiguration {

    public AndroidTvPluginConfiguration() {
        super("jarvis-plugin-androidtv");
    }

    public class PropertyKey {
        public static final String tvChannelFile = "androidtv.channel";
        public static final String ipAddress = "androidtv.ipAddress";
        public static final String port = "androidtv.port";
        public static final String keystore = "androidtv.keystore";
        public static final String keystorePassword = "androidtv.keystore.password";
    }
}
