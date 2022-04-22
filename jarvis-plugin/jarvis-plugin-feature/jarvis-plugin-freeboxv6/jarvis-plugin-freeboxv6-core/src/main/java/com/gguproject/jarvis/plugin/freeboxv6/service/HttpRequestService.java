package com.gguproject.jarvis.plugin.freeboxv6.service;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.utils.StringUtils;
import com.gguproject.jarvis.plugin.freeboxv6.service.dto.FreeboxServerSessionRequest;
import com.gguproject.jarvis.plugin.freeboxv6.service.dto.FreeboxServerSessionResponse;
import com.google.gson.Gson;
import io.mikael.urlbuilder.UrlBuilder;
import io.mikael.urlbuilder.util.UrlParameterMultimap;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to send get & post http requests
 */
public abstract class HttpRequestService {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(HttpRequestService.class);

    private static Gson gson = new Gson();
    private static HttpClient defaultHttpClient = HttpClient.newBuilder().build();

    protected abstract String getHost();

    protected HttpClient getHttpClient(){
        return defaultHttpClient;
    }

    protected HttpResponse<String> get(Request request) {
        try {
            final UrlBuilder urlBuilder = UrlBuilder.fromString(this.getHost())
                    .withPath(request.path);
            UrlParameterMultimap queryParams = UrlParameterMultimap.newMultimap();
            request.queryParams.forEach((name, value) -> queryParams.add(name, value));
            URI uri = urlBuilder.withParameters(queryParams).toUri();

            LOGGER.debug("Execute GET request on uri: {} & request: {}", uri, request);

            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET();
            request.headers.forEach((name, value) -> httpRequestBuilder.header(name, value));

            HttpResponse<String> response = this.getHttpClient().send(httpRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            LOGGER.debug("Received response with status: {} & body: {}", response.statusCode(), response.body());

            return response;
        } catch (IOException | InterruptedException e) {
            throw new HttpRequestException("An error occurs while sending the request", e);
        }
    }

    protected HttpResponse<String> post(Request request, Object data) {
        try {
            final UrlBuilder urlBuilder = UrlBuilder.fromString(this.getHost())
                    .withPath(request.path);
            UrlParameterMultimap queryParams = UrlParameterMultimap.newMultimap();
            request.queryParams.forEach((name, value) -> queryParams.add(name, value));
            URI uri = urlBuilder.withParameters(queryParams).toUri();

            LOGGER.debug("Execute POST request on uri: {} & request: {}", uri, request);

            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data)));
            request.headers.forEach((name, value) -> httpRequestBuilder.header(name, value));

            HttpResponse<String> response = this.getHttpClient().send(httpRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            LOGGER.debug("Received response with status: {} & body: {}", response.statusCode(), response.body());

            return response;
        } catch (IOException | InterruptedException e) {
            throw new HttpRequestException("An error occurs while sending the request", e);
        }
    }

    /**
     *
     */
    public static class Request {
        private String path = "/";

        private Map<String, String> queryParams = new HashMap<>();

        private Map<String, String> headers = new HashMap<>();

        public Request path(String path){
            this.path = path;
            return this;
        }

        public Request header(String name, String value){
            this.headers.put(name, value);
            return this;
        }

        public boolean hasHeader(String name){
            return this.headers.containsKey(name);
        }

        public Request queryParam(String name, String value){
            this.queryParams.put(name, value);
            return this;
        }

        @Override
        public String toString() {
            return "Request{" +
                    "path='" + path + '\'' +
                    ", queryParams=" + queryParams +
                    ", headers=" + headers +
                    '}';
        }
    }
}
