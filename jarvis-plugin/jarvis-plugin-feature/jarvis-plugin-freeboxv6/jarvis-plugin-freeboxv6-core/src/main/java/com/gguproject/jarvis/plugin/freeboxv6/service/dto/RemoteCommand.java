package com.gguproject.jarvis.plugin.freeboxv6.service.dto;

import com.google.gson.annotations.SerializedName;

/**
 * List of remote commands
 * found at: https://www.abavala.com/piloter-la-freebox-revolution-v6-hd-v5/
 */
public enum RemoteCommand {
    RED("red"),
    GREEN("green"),
    YELLOW("yellow"),
    BLUE("blue"),

    POWER("power"),
    LIST("list"), // = OK
    //TV("tv"),

    TOUCH_0("0"),
    TOUCH_1("1"),
    TOUCH_2("2"),
    TOUCH_3("3"),
    TOUCH_4("4"),
    TOUCH_5("5"),
    TOUCH_6("6"),
    TOUCH_7("7"),
    TOUCH_8("8"),
    TOUCH_9("9"),

    BACK("back"),
    //SWAP("swap"),

    //INFO("info"),
    //MAIL("mail"),
    //HELP("help"),
    //PIP("pip"),
    //EPG("epg"),
    //MEDIA("media"),
    //OPTIONS("options"),
    //REPLAY("replay"),
    //VOD("vod"),
    //WHATSON("whatson"),
    //RECORDS("records"),
    YOUTUBE("youtube"),
    //RADIOS("radios"),
    //CANALVOD("canalvod"),
    //NETFLIX("netflix"),

    VOLUME_UP("vol_inc"),
    VOLUME_DOWN("vol_dec"),

    PROGRAM_UP("prgm_inc"),
    PROGRAM_DOWN("prgm_dec"),

    OK("ok"),
    UP("up"),
    RIGHT("right"),
    DOWN("down"),
    LEFT("left"),

    MUTE("mute"),
    HOME("home"),
    //REC("rec"),
    BACKWARD("bwd"),
    PREVIOUS("prev"),
    PLAY_PAUSE("play"),
    FORWARD("fwd"),
    NEXT("next");

    private String code;

    private RemoteCommand(String code){
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}
