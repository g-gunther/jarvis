package com.gguproject.jarvis.plugin.freeboxv6;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class KodiPluginConfiguration extends AbstractPluginConfiguration {

    public KodiPluginConfiguration() {
        super("jarvis-plugin-kodi");
    }

    public class PropertyKey {
        public static final String host = "kodi.host";
        public static final String user = "kodi.auth.user";
        public static final String password = "kodi.auth.password";
        public static final String netflixProfileId = "kodi.netflix.profile_id";
    }
}
