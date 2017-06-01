package com.example.jocke.incidentguardianv2.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.jocke.incidentguardianv2.R;

public class MainActivity extends AppCompatActivity {

    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=incidentguardian;AccountKey=X9Cygj3SSBlAz5zrHbCEivfSb/lh3PoDwKmFXaNB9ZH+aD4REG0OwnmHUPYGeOeDPQcPDIB0wkxoyFGCsdh4Gw==;EndpointSuffix=core.windows.net";

    Button btnLogin;
    Button btnRegister;

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
}
