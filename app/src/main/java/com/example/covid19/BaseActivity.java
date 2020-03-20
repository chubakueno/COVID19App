package com.example.covid19;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covid19.api.APIClient;
import com.example.covid19.api.APIInterface;

import java.util.UUID;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    final String appPackage = "com.example.covid19";
    protected APIInterface apiInterface;
    protected SharedPreferences prefs;
    protected String userId;

    final String dataKey = "downloadedData";
    final String userIdKey = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefs = getSharedPreferences(appPackage, Context.MODE_PRIVATE);
        userId = prefs.getString(userIdKey,null);
        if(userId == null){
            userId = UUID.randomUUID().toString();
            prefs.edit().putString(userIdKey,userId).apply();
        }
    }

    public String getData(){
        return prefs.getString(dataKey,null);
    }

    public void setData(String data){
        prefs.edit().putString(dataKey,data).apply();
    }
}
