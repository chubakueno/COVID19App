package com.example.covid19.utils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class DateData{
    @SerializedName("kmldate")
    String kmldate;
    @SerializedName("start")
    long start;
    @SerializedName("end")
    long end;
    @SerializedName("datatype")
    String datatype;
    @SerializedName("latlngArray")
    List<LatLng> latlngArray;

    DateData(String kmldate, long start, long end, String datatype, List<LatLng> latlngArray) {
        this.kmldate = kmldate;
        this.start = start;
        this.end = end;
        this.datatype = datatype;
        this.latlngArray = latlngArray;
    }
}