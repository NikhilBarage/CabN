package com.example.cabn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

// Session Management
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private SharedPreferences prefs;
    private float distancee = 0.0f;
    private String startPlace = null, endPlace = null;

    private Button bookk, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            // Redirect to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.mapp);
        LayoutShow layoutShow = new LayoutShow(this);
        bookk = findViewById(R.id.next);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);

        // WebView Configuration
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/map.html");

        //Add JavaScript Interface
        webView.addJavascriptInterface(new WebAppInterface(), "AndroidInterface");

        //Book button click
        bookk.setOnClickListener(v -> {
            if (startPlace == null || endPlace == null || distancee <= 0.0f) {
                Toast.makeText(MainActivity.this, "Please wait for data to load...", Toast.LENGTH_SHORT).show();
            } else {
                layoutShow.getView(distancee, startPlace, endPlace);
            }
        });



        ImageButton logoutButton = toolbar.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }


    private void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //  JavaScript Interface
    public class WebAppInterface {
        @JavascriptInterface
        public void showPlace(String start, String end) {
            runOnUiThread(() -> {
                startPlace = start;
                endPlace = end;
            });
        }

        @JavascriptInterface
        public void showDistance(String distance) {
            try {
                distancee = Float.parseFloat(distance);
            } catch (NumberFormatException e) {
                distancee = 0.0f; // Default to 0.0 if parsing fails
            }
        }
    }
}