package com.example.jocke.incidentguardianv2.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.jocke.incidentguardianv2.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=incidentguardian;AccountKey=X9Cygj3SSBlAz5zrHbCEivfSb/lh3PoDwKmFXaNB9ZH+aD4REG0OwnmHUPYGeOeDPQcPDIB0wkxoyFGCsdh4Gw==;EndpointSuffix=core.windows.net";

    Button btnLogin;
    Button btnRegister;

    Boolean isAccelerometer;
    Boolean isGyrometer;
    Boolean isGps;
    Boolean isEmergency;
    Integer sampleRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.buttonLogin);
        btnRegister = (Button) findViewById(R.id.buttonReg);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MonitorActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
