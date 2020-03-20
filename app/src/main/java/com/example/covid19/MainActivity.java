package com.example.covid19;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private Button downloadButton;
    String loggedInUrl = "https://myaccount.google.com/";
    String timelineUrl = "https://www.google.com/";
    String dataKey = "downloadedData";
    String appPackage = "com.example.covid19";
    protected void showSavedDataDialog(){
        SharedPreferences prefs = MainActivity.this.getSharedPreferences(
                appPackage, Context.MODE_PRIVATE);
        String savedData = prefs.getString(dataKey, null);
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Operacion exitosa")
                .setMessage(savedData)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        finish();
                    }
                })
                .show();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        webView = findViewById(R.id.webView);
        downloadButton = findViewById(R.id.downloadButton);
        webView.loadUrl("https://myaccount.google.com/intro");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        final View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Click:");
                String script =
                        "JSON.stringify((function() {\n" +
                                "    let date = new Date();\n" +
                                "    let dataArr = []\n" +
                                "    while(date >= new Date(2020,2,18)){//since march 1st\n" +
                                "        var request = new XMLHttpRequest();\n" +
                                "        let year = date.getYear()+1900;\n" +
                                "        let month = date.getMonth();\n" +
                                "        request.open('GET', 'https://www.google.com/maps/timeline/kml?authuser=0&pb=!1m8!1m3!1i2020!2i2!3i1!2m3!1i2020!2i2!3i1', false);\n" +
                                "        request.send(null);\n" +
                                "        if (request.status === 200) {\n" +
                                "          dataArr.push(request.responseText);\n" +
                                "        }\n" +
                                "        date.setDate(date.getDate()-1);\n" +
                                "    }\n" +
                                "    return dataArr;\n" +
                                "})());";
                System.out.println(script);
                webView.evaluateJavascript(
                        script, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        SharedPreferences prefs = MainActivity.this.getSharedPreferences(
                                appPackage, Context.MODE_PRIVATE);
                        prefs.edit().putString(dataKey,s).apply();
                        downloadButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent));
                        downloadButton.setTextColor(0xFFFFFFFF);
                        downloadButton.setText("Subir datos");
                        downloadButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showSavedDataDialog();
                            }
                        });
                    }
                });
            }
        };
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                if(loggedInUrl.equals(url)){
                    webView.loadUrl(timelineUrl);
                } else if (timelineUrl.equals(url)){
                    downloadButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorPrimary));
                    downloadButton.setTextColor(0xFFFFFFFF);
                    downloadButton.setOnClickListener(clickListener);;
                    downloadButton.setText("Descargar datos");
                }
            }
        });
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
