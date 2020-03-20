package com.example.covid19.api;

import com.example.covid19.api.models.requests.LocationHistoryRequest;
import com.example.covid19.api.models.responses.LocationHistoryResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIInterface {
    @POST("uploadData")
    Call<LocationHistoryResponse> uploadData(@Body LocationHistoryRequest user);
}