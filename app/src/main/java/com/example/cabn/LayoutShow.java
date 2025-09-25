package com.example.cabn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LayoutShow {
    private Context context;

    // Price per km
    private final float RKPS = 3, C4P = 5, C6P = 10;
    private int hour, minute;
    private String timepicker = "";

    // Constructor
    public LayoutShow(Context context) {
        this.context = context;
    }

    public void getView(int idd, String  ph, float dist, String stLat, String stLng, String edLat, String edLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle("Ride Details...");
        View priceView = inflater.inflate(R.layout.prices, null);

        priceView.setPadding(20, 10, 20, 10);
        String from, to;

        Calendar calendar = Calendar.getInstance();
        int currenthour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentminute = calendar.get(Calendar.MINUTE);

        TimePicker tm = priceView.findViewById(R.id.time_picker);
        tm.setIs24HourView(true);

        Button btnn = priceView.findViewById(R.id.cnfrmm);
        LinearLayout l1 = priceView.findViewById(R.id.rickshaw3);
        LinearLayout l2 = priceView.findViewById(R.id.car4);
        LinearLayout l3 = priceView.findViewById(R.id.car6);

        btnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hour = tm.getHour();
                minute = tm.getMinute();

                if (hour > currenthour || (hour == currenthour && minute > currentminute)) {
                    timepicker = String.format(Locale.getDefault(), "%02d:%02d:00", hour, minute);

                    TextView tmm = priceView.findViewById(R.id.pick);
                    tmm.setText("PickUp Time - " + timepicker);

                    // Make layouts visible
                    l1.setVisibility(View.VISIBLE);
                    l2.setVisibility(View.VISIBLE);
                    l3.setVisibility(View.VISIBLE);
                } else {
                    // Hide vehicle options
                    l1.setVisibility(View.GONE);
                    l2.setVisibility(View.GONE);
                    l3.setVisibility(View.GONE);

                    Toast.makeText(context, "Choose Correct Time, such PickUp Time should afterward 15 Minute from current time....", Toast.LENGTH_SHORT).show();
                }

            }
        });

        from = getAddressFromLatLng(stLat + "," + stLng);
        to = getAddressFromLatLng(edLat + "," + edLng);

        // UI elements
        TextView t0 = priceView.findViewById(R.id.information_place);
        TextView t1 = priceView.findViewById(R.id.rickshawprice);
        TextView t2 = priceView.findViewById(R.id.car4price);
        TextView t3 = priceView.findViewById(R.id.car6price);

        // Display trip details
        t0.setText("From - " + from + "\nTo - " + to + "\nDistance - " + dist);
        t1.setText("₹" + String.format("%.2f", RKPS * dist));
        t2.setText("₹" + String.format("%.2f", C4P * dist));
        t3.setText("₹" + String.format("%.2f", C6P * dist));


        // Vehicle selection with confirmation

        l1.setOnClickListener(v -> confirmSelection(idd, ph, stLat + ", " + stLng, edLat + ", " + edLng, dist, RKPS * dist, "Rickshaw", timepicker));
        l2.setOnClickListener(v -> confirmSelection(idd, ph, stLat + ", " + stLng, edLat + ", " + edLng, dist, C4P * dist, "car4", timepicker));
        l3.setOnClickListener(v -> confirmSelection(idd, ph, stLat + ", " + stLng, edLat + ", " + edLng, dist, C6P * dist, "car6", timepicker));


        builder.setView(priceView);
        builder.setPositiveButton("OK", null);
        builder.show();
    }


    // Confirmation Dialog
    private void confirmSelection(int idd, String ph, String from, String to, float dist, float totalPrice, String vehicle, String timepickerr) {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(context);
        confirmDialog.setTitle("Confirm Selection");
        confirmDialog.setMessage("Are you sure you want to book " + vehicle + " for ₹" + String.format("%.2f", totalPrice) + "?");

        confirmDialog.setPositiveButton("Yes", (dialog, which) -> {
            // Build payment dialog
            AlertDialog.Builder paymentDialog = new AlertDialog.Builder(context);
            paymentDialog.setTitle("Payment");

            // Create layout for radio buttons
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 30, 50, 10);

            RadioGroup rg = new RadioGroup(context);
            rg.setOrientation(RadioGroup.HORIZONTAL);

            RadioButton offline = new RadioButton(context);
            offline.setText("Offline");
            offline.setId(View.generateViewId());

            RadioButton online = new RadioButton(context);
            online.setText("Online");
            online.setId(View.generateViewId());

            rg.addView(offline);
            rg.addView(online);

            layout.addView(rg);

            paymentDialog.setView(layout);

            paymentDialog.setPositiveButton("Proceed", (d, w) -> {
                int selectedId = rg.getCheckedRadioButtonId();

                if (selectedId == -1) {
                    Toast.makeText(context, "Please select a payment mode", Toast.LENGTH_SHORT).show();

                } else {
                    RadioButton selected = layout.findViewById(selectedId);
                    String paymentMethod = selected.getText().toString();

                    Toast.makeText(context, "Selected: " + paymentMethod, Toast.LENGTH_SHORT).show();

                    if (selectedId == offline.getId()) {
                        // Handle offline booking

                        // Show progress dialog instead of ProgressBar
                        ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Booking your ride...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> {
                            String isReg = DBConnection.addUserRequest(idd, ph, from, to, vehicle, totalPrice, "offline", timepickerr);

                            new Handler(Looper.getMainLooper()).post(() -> {
                                progressDialog.dismiss();

                                Intent i = new Intent(context, Userhist.class);
                                if (isReg.equals("success")) {

                                    context.startActivity(i);
                                    ((Activity) context).finish();
                                    Toast.makeText(context, "Ride request sent successfully!", Toast.LENGTH_SHORT).show();
                                } else if (isReg.equals("failed")) {
                                    Toast.makeText(context, "Previous Ride not Completed...", Toast.LENGTH_SHORT).show();

                                    context.startActivity(i);
                                    ((Activity) context).finish();
                                } else if (isReg.equals("not")) {
                                    Toast.makeText(context, "server problem...", Toast.LENGTH_SHORT).show();
                                } else if (isReg.equals("error")) {
                                    Toast.makeText(context, "Error...", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Wrong...", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    } else if (selectedId == online.getId()) {
                        // Handle online payment (UPI/Intent)

                        // Show progress dialog instead of ProgressBar
                        ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Booking your ride...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> {
                            String isReg = DBConnection.addUserRequest(idd, ph, from, to, vehicle, totalPrice, "online", timepickerr);

                            new Handler(Looper.getMainLooper()).post(() -> {
                                progressDialog.dismiss();

                                if (isReg.equals("success")) {
                                    Intent i = new Intent(context, Userhist.class);
                                    context.startActivity(i);
                                    ((Activity) context).finish();

                                    Toast.makeText(context, "Ride request sent successfully!", Toast.LENGTH_SHORT).show();
                                } else if (isReg.equals("failed")) {
                                    Toast.makeText(context, "Previous Ride not Completed...", Toast.LENGTH_SHORT).show();
                                } else if (isReg.equals("not")) {
                                    Toast.makeText(context, "server problem...", Toast.LENGTH_SHORT).show();
                                } else if (isReg.equals("error")) {
                                    Toast.makeText(context, "Error...", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Wrong...", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    }
                }
            });

            paymentDialog.setNegativeButton("Cancel", (d, w) -> d.dismiss());
            paymentDialog.show();
        });

        confirmDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        confirmDialog.show();
    }

    private String getAddressFromLatLng(String latLngStr) {
        if (latLngStr == null || !latLngStr.contains(",")) {
            return "Invalid Coordinates"; // Ensure correct format
        }

        String[] latLng = latLngStr.split(",");
        if (latLng.length != 2) {
            return "Invalid Coordinates";
        }

        try {
            double latitude = Double.parseDouble(latLng[0].trim());
            double longitude = Double.parseDouble(latLng[1].trim());

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                //
                StringBuilder addressString = new StringBuilder();

                if (address.getSubLocality() != null) {
                    addressString.append(address.getSubLocality()).append(", "); //village name
                }
                if (address.getLocality() != null) {
                    addressString.append(address.getLocality()).append(", "); //city or state
                }
                if (address.getPostalCode() != null) {
                    addressString.append(address.getPostalCode()); //pincode
                }

                return addressString.toString().trim();
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
        return "Unknown Location";
    }


}
