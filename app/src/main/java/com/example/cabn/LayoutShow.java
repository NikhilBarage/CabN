package com.example.cabn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class LayoutShow {
    private Context context;
    private float rkps = 0, c4p, c6p, tmpp, trkp = 0;

    //constructor
    public LayoutShow(Context context) {this.context = context;}

    public void getView(float dist, String from, String to) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View priceView = inflater.inflate(R.layout.prices, null);

        LinearLayout l1 = priceView.findViewById(R.id.rickshaw3);
        TextView t0 = priceView.findViewById(R.id.information_place);
        TextView t1 = priceView.findViewById(R.id.rickshawprice);
        TextView t2 = priceView.findViewById(R.id.car4price);
        TextView t3 = priceView.findViewById(R.id.car6price);
        TextView t4 = priceView.findViewById(R.id.tempoprice);
        TextView t5 = priceView.findViewById(R.id.truckprice);

        t0.setText("From - "+from+"\nTo - "+to+"\nDistance - "+dist);
        t1.setText(""+(rkps*dist));
        t2.setText(""+(c4p*dist));
        t3.setText(""+(c6p*dist));
        t4.setText(""+(tmpp*dist));
        t5.setText(""+(trkp*dist));


        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Rickshaw Selected...", Toast.LENGTH_LONG).show();
            }
        });

        LinearLayout l2 = priceView.findViewById(R.id.car4);
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Car 4 Selected...", Toast.LENGTH_LONG).show();
            }
        });


        builder.setView(priceView);
        builder.setPositiveButton("OK", null);
        //builder.setNegativeButton("CANCLE", null);
        builder.show();

    }

}

