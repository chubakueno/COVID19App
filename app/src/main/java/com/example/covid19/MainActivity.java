package com.example.covid19;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.covid19.api.models.requests.LocationHistoryRequest;
import com.example.covid19.api.models.responses.LocationHistoryResponse;
import com.example.covid19.utils.Anonimizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    final String loggedInUrl = "https://myaccount.google.com/";
    final String timelineUrl = "https://www.google.com/";
    final int marchStartingDate = 1;

    private WebView webView;
    private Button downloadButton;
    private ProgressDialog progressDialog;

    protected void showSavedDataDialog(){
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Operacion exitosa")
                .setMessage("¡Muchas gracias por contribuir en nuestra lucha!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //nothing
                    }
                })
                .show();
    }
    void configureDownload(){
        downloadButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
        downloadButton.setTextColor(0xFFFFFFFF);
        downloadButton.setText(R.string.download_data);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String script = buildJavascriptPayload();
                progressDialog = ProgressDialog.show(MainActivity.this,"Recopilando datos","Por favor espere");
                webView.evaluateJavascript(
                        script, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String jsonKmlData) {
                                progressDialog.dismiss();
                                setData(jsonKmlData);
                                configureUpload();
                            }
                        });
            }
        });
    }
    void sendAnonimized(){
        apiInterface.uploadData(new LocationHistoryRequest(userId, Anonimizer.anonimize(getData())))
                .enqueue(new Callback<LocationHistoryResponse>() {
            @Override
            public void onResponse(Call<LocationHistoryResponse> call, Response<LocationHistoryResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful() && response.body().success){
                    showSavedDataDialog();
                }
            }

            @Override
            public void onFailure(Call<LocationHistoryResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }
    void configureUpload(){
        downloadButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent));
        downloadButton.setTextColor(0xFFFFFFFF);
        downloadButton.setText(R.string.upload_data);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(MainActivity.this,"Subiendo datos","Por favor espere");
                sendAnonimized();
            }
        });
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };
    String buildJavascriptPayload(){
        return  "(function() {\n" +
                "    let date = new Date();\n" +
                "    let dateArr = []\n" +
                "    while(date >= new Date(2020,2,"+marchStartingDate+")){//since march 1st\n" +
                "        var request = new XMLHttpRequest();\n" +
                "        let year = date.getYear()+1900;\n" +
                "        let month = date.getMonth();\n" +
                "        let day = date.getDate();\n" +
                "        let url = 'https://www.google.com/maps/timeline/kml?authuser=0&pb=!1m8!1m3!1i'+year+'!2i'+month+'!3i'+day+'!2m3!1i'+year+'!2i'+month+'!3i'+day+''\n" +
                "        request.open('GET', url, false);" +
                "        request.send(null);\n" +
                "        if (request.status === 200) {\n" +
                "          dateArr.push({date: day+'-'+(month+1)+'-'+year, data: request.responseText});\n" +
                "        }\n" +
                "        date.setDate(date.getDate()-1);\n" +
                "    }\n" +
                "    return dateArr;\n" +
                "})();";
    }
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        downloadButton = findViewById(R.id.downloadButton);
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if(getData()==null) {
            webView.loadUrl("https://myaccount.google.com/intro");
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (loggedInUrl.equals(url)) {
                        webView.loadUrl(timelineUrl);
                    } else if (timelineUrl.equals(url)) {
                        configureDownload();
                    }
                }
            });
        } else {
            webView.loadUrl(timelineUrl);
            configureUpload();
        }
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
