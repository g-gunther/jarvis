package com.gguproject.jarvis.plugin.time;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class TimePluginConfiguration extends AbstractPluginConfiguration {

    public TimePluginConfiguration() {
        super("jarvis-plugin-time");
    }

    public class PropertyKey {
        public static final String soundAlarm = "sound.alarm";
        public static final String soundTimer = "sound.timer";

        public static final String speakerPropertyFile = "speaker.file";
    }
}
