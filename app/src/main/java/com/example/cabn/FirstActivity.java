package com.example.cabn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

// Session checking
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FirstActivity extends AppCompatActivity {
    private LinearLayout vieww2;
    private Button gologin;
    private CardView vieww1;
    private static final int SPLASH_TIME = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);


        if (isLoggedIn) {
            // Redirect to HomePage
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_first);

        vieww1 = findViewById(R.id.view1);
        vieww2 = findViewById(R.id.view2);
        gologin = findViewById(R.id.getStartedButton);

        // Delay the transition effect for splash/welcome screen
        new Handler().postDelayed(() -> {
            vieww1.setVisibility(View.GONE);
            vieww2.setVisibility(View.VISIBLE);
        }, SPLASH_TIME);

        gologin.setOnClickListener(v -> {
            Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}