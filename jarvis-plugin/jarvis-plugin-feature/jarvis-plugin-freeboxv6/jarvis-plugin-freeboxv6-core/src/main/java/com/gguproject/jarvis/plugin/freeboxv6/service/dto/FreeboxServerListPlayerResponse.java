package com.gguproject.jarvis.plugin.freeboxv6.service.dto;

import java.util.ArrayList;

public class FreeboxServerListPlayerResponse extends ArrayList<FreeboxServerListPlayerResponse.FreeboxServerPlayer> {

    public static class FreeboxServerPlayer {
        private String mac;
        private int id;
        private boolean api_available;
        private String device_name;
        private String device_model;
        private boolean reachable;
        private String uid;
        private String api_version;

        public int getId() {
            return id;
        }

        public String getDeviceName() {
            return device_name;
        }

        public boolean isApiAvailable(){
            return this.api_available;
        }

        public boolean isReachable(){
            return this.reachable;
        }

        public String getApiVersion(){
            return this.api_version;
        }

        @Override
        public String toString() {
            return "FreeboxServerPlayer{" +
                    "mac='" + mac + '\'' +
                    ", id=" + id +
                    ", api_available=" + api_available +
                    ", device_name='" + device_name + '\'' +
                    ", device_model='" + device_model + '\'' +
                    ", reachable=" + reachable +
                    ", uid='" + uid + '\'' +
                    ", api_version='" + api_version + '\'' +
                    '}';
        }
    }
}
