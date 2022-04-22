package com.gguproject.jarvis.plugin.freeboxv6.service.dto;

import com.google.gson.annotations.SerializedName;

public enum PlayerStatus {
    @SerializedName("standby")
    STANDBY,

    @SerializedName("running")
    RUNNING;
}
