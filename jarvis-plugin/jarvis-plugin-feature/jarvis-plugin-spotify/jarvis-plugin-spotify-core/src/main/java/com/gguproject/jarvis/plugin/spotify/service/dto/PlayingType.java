package com.gguproject.jarvis.plugin.spotify.service.dto;

import com.wrapper.spotify.enums.ModelObjectType;

import java.util.Arrays;

public enum PlayingType {
    PLAYLIST("playlist"),
    ALBUM("album"),
    ARTIST("artist"),
    AUDIO_FEATURES("audio_features"),
    EPISODE("episode"),
    GENRE("genre"),
    SHOW("show"),
    TRACK("track"),
    USER("user");

    private String type;

    PlayingType(String type){
        this.type = type;
    }

    public String toUri(String id){
        return "spotify:" + this.type + ":" + id;
    }

    public String findIdFromUri(String uri){
        return uri.substring(uri.lastIndexOf(":") + 1);
    }

    public static PlayingType fromType(ModelObjectType type){
        return Arrays.stream(PlayingType.values()).filter(t -> t.type.equals(type.getType())).findFirst().orElseThrow();
    }
}
