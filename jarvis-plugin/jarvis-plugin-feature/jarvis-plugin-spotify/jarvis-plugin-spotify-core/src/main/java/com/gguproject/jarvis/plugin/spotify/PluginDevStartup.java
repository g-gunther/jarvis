package com.gguproject.jarvis.plugin.spotify;

import com.gguproject.jarvis.plugin.core.dev.AbstractPluginDevStartup;
import com.gguproject.jarvis.plugin.spotify.service.SpotifyService;

import javax.inject.Named;

@Named
public class PluginDevStartup extends AbstractPluginDevStartup {

    public static void main(String[] args) {
        init("jarvis-plugin-spotify");
    }

    private final SpotifyService spotifyService;

    public PluginDevStartup(SpotifyService spotifyService){
        this.spotifyService = spotifyService;
    }

    protected void process() {
        this.spotifyService.listDevices().forEach(System.out::println);
    }
}
