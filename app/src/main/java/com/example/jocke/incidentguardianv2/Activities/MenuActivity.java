package com.example.jocke.incidentguardianv2.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.jocke.incidentguardianv2.R;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity{

    Button btnStart;
    Button btnContacts;
    Button btnEmergencyMessage;

    Boolean isAccelerometer;
    Boolean isGyrometer;
    Boolean isGps;
    Boolean isEmergency;
    Integer sampleRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnStart = (Button) findViewById(R.id.buttonStartMonitoring);
        btnContacts = (Button) findViewById(R.id.buttonAddContact);
        btnEmergencyMessage = (Button) findViewById(R.id.buttonEmergencyMessage);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, MonitorActivity.class));
            }
        });

        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, ContactActivity.class));
            }
        });

        btnEmergencyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, MessageActivity.class));
            }
        });

    }

    public void getUserData(ArrayList<Object> userData){
        isAccelerometer = (Boolean) userData.get(0);
        isGyrometer = (Boolean) userData.get(1);
        isGps = (Boolean) userData.get(2);
        isEmergency = (Boolean) userData.get(3);
        sampleRate = (Integer) userData.get(4);
    }
}
