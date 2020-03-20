package com.example.covid19.api.models.requests;

import com.google.gson.annotations.SerializedName;

public class LocationHistoryRequest {
    @SerializedName("id")
    private String id;

    @SerializedName("data")
    private String data;

    public LocationHistoryRequest(String id, String data) {
        this.id = id;
        this.data = data;
    }
}
