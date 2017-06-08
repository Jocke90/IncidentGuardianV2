package com.example.jocke.incidentguardianv2.Activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jocke.incidentguardianv2.AsyncResponse;
import com.example.jocke.incidentguardianv2.DataStorageClass;
import com.example.jocke.incidentguardianv2.R;

import java.util.ArrayList;
import java.util.prefs.Preferences;

public class MenuActivity extends AppCompatActivity implements AsyncResponse{

    Button btnStart;
    Button btnContacts;
    Button btnEmergencyMessage;
    String userName;

    private Boolean isAccelerometer;
    private Boolean isGyrometer;
    private Boolean isGps;
    private Integer sampleRate = 0;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        userName = sharedPref.getString("Username", "");

        //Starts a new execute to asyncTask class to get user settings for sensors and sample rate, it is stored
        //to an ArrayList. dcs.delegate is for the interface AsyncResponse which enables to store the values
        //in an ArrayList in asyncTask onPostExecute method
        DataStorageClass dcs = new DataStorageClass();
        dcs.delegate = this;
        dcs.execute("getData", userName);

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

    //This method fetches the values from asyncTask class and is ran in onPostExecute method
    //Then the values are stored to shared preferences so they can be fetched in MonitorActivity
    @Override
    public void processFinish(ArrayList<Object> returnList) {
        isAccelerometer = (Boolean) returnList.get(0);
        isGyrometer = (Boolean) returnList.get(1);
        isGps = (Boolean) returnList.get(2);
        sampleRate = (Integer) returnList.get(3);
        editor.putBoolean("AccelerometerCheck", isAccelerometer);
        editor.putBoolean("GyrometerCheck", isGyrometer);
        editor.putBoolean("GpsCheck", isGps);
        editor.putInt("SampleRate", sampleRate);
        editor.commit();

        Toast.makeText(MenuActivity.this, "Values from async: " + String.valueOf(isAccelerometer) + " " + String.valueOf(isGyrometer) + " " + String.valueOf(isGps) + " " + String.valueOf(sampleRate), Toast.LENGTH_SHORT).show();
    }
}
