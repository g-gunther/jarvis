package com.gguproject.jarvis.plugin.spotify;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class SpotifyPluginConfiguration extends AbstractPluginConfiguration {

    public SpotifyPluginConfiguration() {
        super("jarvis-plugin-spotify");
    }

    public class PropertyKey {
        public final static String infraredCommandsConfigurationFile = "infrared.command.configuration.file";

        public static final String spotifyApiRefreshToken = "spotify.api.refresh_token";
        public static final String spotifyClientId = "spotify.api.client_id";
        public static final String spotifyClientSecret = "spotify.api.client_secret";

        public static final String spotifyDeviceFile = "spotify.device.file";
    }
}
