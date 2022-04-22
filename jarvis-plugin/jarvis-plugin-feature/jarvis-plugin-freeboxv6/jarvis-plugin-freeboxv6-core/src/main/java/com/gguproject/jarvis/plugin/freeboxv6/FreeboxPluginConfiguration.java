package com.gguproject.jarvis.plugin.freeboxv6;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class FreeboxPluginConfiguration extends AbstractPluginConfiguration {

    public FreeboxPluginConfiguration() {
        super("jarvis-plugin-freeboxv6");
    }

    public class PropertyKey {
        public class Remote {
            public static final String host = "freeboxv6.remote.host";
            public static final String code = "freeboxv6.remote.code";
        }

        public class Server {
            public static final String host = "freeboxv6.server.host";
            public static final String appId = "freeboxv6.server.app_id";
            public static final String appToken = "freeboxv6.server.app_token";
            public static final String playerId = "freeboxv6.server.player_id";
        }
    }
}
