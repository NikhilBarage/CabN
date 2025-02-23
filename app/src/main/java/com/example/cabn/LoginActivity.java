package com.example.cabn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//session checking
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumber, otpInput;
    private TextView resendCode, register;
    private Button sendCode, nextBtn;
    private String generatedOTP;
    private boolean isOTPSent = false;

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
        }

        setContentView(R.layout.activity_login);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        ImageButton logoutButton = toolbar.findViewById(R.id.logoutButton);
        logoutButton.setVisibility(View.GONE); //hide toolbar logout button

        phoneNumber = findViewById(R.id.phone_number);
        otpInput = findViewById(R.id.otp_input);
        resendCode = findViewById(R.id.resend_code);
        register = findViewById(R.id.register);
        sendCode = findViewById(R.id.send_code);
        nextBtn = findViewById(R.id.next_btn);

        otpInput.setVisibility(View.GONE);
        resendCode.setVisibility(View.GONE);
        nextBtn.setVisibility(View.GONE);

        // Send OTP Button Click
        sendCode.setOnClickListener(v -> {
            String phone = phoneNumber.getText().toString().trim();
            if (isValidPhone(phone)) {
                sendOTP(phone);
            } else {
                Toast.makeText(LoginActivity.this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            }
        });

        // Verify OTP Button Click
        // Inside the nextBtn.setOnClickListener
        nextBtn.setOnClickListener(v -> {
            String enteredOTP = otpInput.getText().toString().trim();
            if (isValidOTP(enteredOTP)) {
                // Save login session
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Invalid OTP. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // Resend OTP
        resendCode.setOnClickListener(v -> {
            if (isOTPSent) {
                sendOTP(phoneNumber.getText().toString().trim());
            }
        });

        // Register Link
        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, Registration.class);
            startActivity(intent);
        });
    }

    // Validate Phone Number
    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    // Validate OTP (4-digit)
    private boolean isValidOTP(String otp) {
        return !TextUtils.isEmpty(otp) && otp.equals(generatedOTP);
    }

    // Simulate Sending OTP
    private void sendOTP(String phone) {
        isOTPSent = true;
        generatedOTP = generateRandomOTP();
        Toast.makeText(this, "OTP Sent: " + generatedOTP, Toast.LENGTH_LONG).show();

        // Show OTP input field & next button
        otpInput.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
        resendCode.setVisibility(View.VISIBLE);

        // Disable Resend OTP for 30 seconds
        resendCode.setEnabled(false);
        new Handler().postDelayed(() -> resendCode.setEnabled(true), 30000);
    }

    // Generate a Random 4-digit OTP
    private String generateRandomOTP() {
        int otp = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(otp);
    }
}
