package com.example.android.practiceset3;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("Wow");
        textview.setTextColor(Color.RED);
        textview.setTextSize(56);
        textview.setVisibility(View.INVISIBLE);

        setContentView(textview);
    }

}
