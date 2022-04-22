package com.gguproject.jarvis.plugin.spotify.service.dto;

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;

public class CurrentDeviceContext {
    private String name;

    private String id;

    private boolean playing;

    private boolean shuffle;

    private String playingUri;

    private PlayingType playingType;

    public CurrentDeviceContext() {
    }

    public void init(CurrentlyPlayingContext context){
        if(context != null) {
            this.name = context.getDevice().getName();
            this.id = context.getDevice().getId();
            this.playing = context.getIs_playing();
            this.shuffle = context.getShuffle_state();
            this.playingUri = context.getContext().getUri();
            this.playingType = PlayingType.fromType(context.getContext().getType());
        }
    }

    public boolean hasCurrentContext(){
        return this.getId() != null;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public String getPlayingUri(){
        return this.playingUri;
    }

    public PlayingType getPlayingType(){
        return this.playingType;
    }

    public boolean isPlaying(){return this.playing;}

    public boolean isShuffle(){return this.shuffle;}

    @Override
    public String toString() {
        return "CurrentDeviceContext{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", playing=" + playing +
                ", shuffle=" + shuffle +
                ", playingUri='" + playingUri + '\'' +
                ", playingType=" + playingType +
                '}';
    }
}
