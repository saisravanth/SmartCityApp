package com.example.bharti.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import com.google.firebase.auth.FirebaseAuth;

public class FormActivity extends AppCompatActivity {
    WebView browser;

    public static final String TAG = "FormActivity";
    public String formUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        browser = (WebView) findViewById(R.id.webview);

        if (savedInstanceState != null) {
            browser.restoreState(savedInstanceState);
        }
        else
            browser.loadUrl("https://docs.google.com/forms/d/1C2kLWEt7s93DTiTP7vwgHduAxeg_yo8TrUKk6CIFH-M/viewform");

    }
}