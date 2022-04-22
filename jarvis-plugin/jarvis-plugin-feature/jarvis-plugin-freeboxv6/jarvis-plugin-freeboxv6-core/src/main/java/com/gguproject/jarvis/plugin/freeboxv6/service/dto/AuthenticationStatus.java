package com.gguproject.jarvis.plugin.freeboxv6.service.dto;

import com.google.gson.annotations.SerializedName;

public enum AuthenticationStatus {
    @SerializedName("auth_required")
    AUTH_REQUIRED, // Invalid session token, or not session token sent

    @SerializedName("invalid_token")
    INVALID_TOKEN,	// The app token you are trying to use is invalid or has been revoked

    @SerializedName("pending_token")
    PENDING_TOKEN,	// The app token you are trying to use has not been validated by user yet

    @SerializedName("insufficient_rights")
    INSUFFICIENT_RIGHTS,	// Your app permissions does not allow accessing this API

    @SerializedName("denied_from_external_ip")
    DENIED_FROM_EXTERNAL_IP,	// You are trying to get an app_token from a remote IP

    @SerializedName("invalid_request")
    INVALID_REQUEST,	// Your request is invalid

    @SerializedName("ratelimited")
    RATE_LIMITED,	// Too many auth error have been made from your IP

    @SerializedName("new_apps_denied")
    NEW_APPS_DENIED,	// New application token request has been disabled

    @SerializedName("apps_denied")
    APPS_DENIED,	// API access from apps has been disabled

    @SerializedName("internal_error")
    INTERNAL_ERROR;	// Internal error
}
