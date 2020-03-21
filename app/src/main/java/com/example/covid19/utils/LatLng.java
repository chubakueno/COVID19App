package com.example.covid19.utils;

import com.google.gson.annotations.SerializedName;

class LatLng {
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;

    LatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}