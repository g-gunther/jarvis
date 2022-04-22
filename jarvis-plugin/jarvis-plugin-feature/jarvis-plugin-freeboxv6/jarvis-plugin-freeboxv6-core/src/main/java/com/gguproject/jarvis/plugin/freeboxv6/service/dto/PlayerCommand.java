package com.gguproject.jarvis.plugin.freeboxv6.service.dto;

import com.google.gson.annotations.SerializedName;

public enum PlayerCommand {
    @SerializedName("play_pause")
    PLAY_PAUSE, //toogle play pause

    @SerializedName("stop")
    STOP,

    @SerializedName("prev")
    PREV,

    @SerializedName("next")
    NEXT,

    @SerializedName("select_stream")
    SELECT_STREAM, // select quality of the stream

    @SerializedName("select_audio_track")
    SELECT_AUDIO_TRACK, // select audio track

    @SerializedName("select_srt_track")
    SELECT_SRT_TRACK; //select subtitle track
}
