package com.example.cabn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Registration extends AppCompatActivity {
    private Spinner carType;
    String[] cars = {"Choose type", "Rickshaw", "Car (4 Seater)", "Car (6 Seater)", "Tempo", "etc."};
    private EditText usrname, phone, email, addr;
    private LinearLayout driverr;
    private RadioGroup usertype;
    private RadioButton userbtn, driverbtn;
    private String car = null;
    private EditText carNum;
    private int userordriver = 0;
    Button rgstr;
    TextView log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Total 9 fields
        carType = findViewById(R.id.cartype); //cartype
        usrname = findViewById(R.id.name); //name
        phone = findViewById(R.id.phone); //phone no.
        email = findViewById(R.id.mail); //email
        addr = findViewById(R.id.addresss); //addr
        carNum = findViewById(R.id.carnum); //carnum

        usertype = findViewById(R.id.usertype); //usertype for user=0 and driver=1
        userbtn = findViewById(R.id.user);
        driverbtn = findViewById(R.id.driver);

        log = findViewById(R.id.login); //logintext
        rgstr = findViewById(R.id.reg_next_btn); //registration button

        //util usertype not select driver gone or invisible
        driverr = findViewById(R.id.ifdriver); //default invisible layout

        //login text redirect
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Registration.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        //usertype radio button
        usertype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.user) {
                    userordriver = 0;
                    driverr.setVisibility(View.GONE); // Hide driver fields
                } else if(checkedId == R.id.driver) {
                    userordriver = 1;
                    driverr.setVisibility(View.VISIBLE);
                }
            }
        });

        //car type spinner
        // Create an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cars);
        carType.setAdapter(adapter);
        carType.setSelection(0); // Set "Choose Type" as default

        // Handle Selection Event
        carType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedUserType = parent.getItemAtPosition(position).toString();
                if (!selectedUserType.equals("Choose Type")) {
                    car = selectedUserType;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        //registreation Button
        rgstr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unm = usrname.getText().toString().trim();
                String ml = email.getText().toString().trim();
                String phoneInput = phone.getText().toString().trim();
                String adr = addr.getText().toString().trim();
                String carno = carNum.getText().toString().trim();

                // Validate fields
                if (unm.isEmpty() || phoneInput.isEmpty() || adr.isEmpty()) {
                    Toast.makeText(Registration.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate phone number
                if (!phoneInput.matches("\\d{10}")) { //10digit validation
                    Toast.makeText(Registration.this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                Double ph = Double.parseDouble(phoneInput);

                if (userordriver == 1) { // Driver
                    if (car == null || car.equals("Choose type") || carno.isEmpty()) {
                        Toast.makeText(Registration.this, "Car type and number are required for drivers", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addDriver(unm, ml, ph, adr, userordriver, car, carno);
                } else { // User
                    addUser(unm, ml, ph, adr, userordriver);
                }
            }
        });


    }

    //driver add to database
    private void addDriver(String unm, String ml, Double ph, String adr, int user, String car, String carno) {
        Toast.makeText(this, ""+unm+ml+ph+adr+car+carno, Toast.LENGTH_SHORT).show();
    }

    //user add to database
    private void addUser(String unm, String ml, Double ph, String adr, int user) {
        Toast.makeText(this, ""+unm+ml+ph+adr, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}