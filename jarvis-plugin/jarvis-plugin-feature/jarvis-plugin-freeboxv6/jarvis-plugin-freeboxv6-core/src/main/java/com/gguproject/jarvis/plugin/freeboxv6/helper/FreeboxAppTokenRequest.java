package com.gguproject.jarvis.plugin.freeboxv6.helper;

import com.gguproject.jarvis.plugin.freeboxv6.service.FreeboxRemoteException;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Helper used to get an app token from the freebox which will be stored in configuration file
 * 1. Run this program, it will return an app_token
 * 2. Then go to the freebox server and accept the app (in lcd screen)
 */
public class FreeboxAppTokenRequest {

    public static void main(String[] args){
        new FreeboxAppTokenRequest().process();
    }

    private HttpClient httpClient;

    private static Gson gson = new Gson();

    private FreeboxAppTokenRequest(){
        this.httpClient = HttpClient.newBuilder().build();
    }

    /**
     * {"success":true,"result":{"app_token":"eXvNZohH88EfUrDzacvjZI09XHIDOnNwWULj6P5L1juTbC5POxRbD2NAklq0IdST","track_id":1}}
     *
     * Process finished with exit code 0
     */
    public void process(){

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://mafreebox.freebox.fr/api/v8/login/authorize/"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new AppTokenRequest())))
                    .build();
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class AppTokenRequest {
        private String app_id = "fr.freebox.jarvis";
        private String app_name = "Jarvis freebox plugin";
        private String app_version = "0.0.1";
        private String device_name = "Jarvis";
    }
}
