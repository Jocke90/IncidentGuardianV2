package com.example.jocke.incidentguardianv2.Activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jocke.incidentguardianv2.DataStorageClass;
import com.example.jocke.incidentguardianv2.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class FallDetectedActivity extends AppCompatActivity {

    Button btnYes;
    Button btnNo;
    Boolean calledForHelpCheck;
    String phoneNr;
    String message;
    String contactInfo;
    String userName;
    String myLat;
    String myLongi;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        userName = sharedPref.getString("Username", "");

        btnYes = (Button) findViewById(R.id.buttonYes);
        btnNo = (Button) findViewById(R.id.buttonNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                falseAlarm();
                startActivity(new Intent(FallDetectedActivity.this, MonitorActivity.class));
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHelp();
                startActivity(new Intent(FallDetectedActivity.this, MonitorActivity.class));
            }
        });


    }

    public String readFileContacts(){
        String text = "";
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/IncidentGuardianFolder");
        File file = new File(dir, "Contacts.txt");
        StringBuilder sb = new StringBuilder();

        try{
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader buffer = new BufferedReader(isr);


            while ((text = buffer.readLine()) != null){
                sb.append(text);
                sb.append("-");
            }

        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(FallDetectedActivity.this, "Error reading file!", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String readFileMessage(){
        String text = "";
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/IncidentGuardianFolder");
        File file = new File(dir, "EmergencyMessage.txt");
        StringBuilder sb = new StringBuilder();

        try{
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader buffer = new BufferedReader(isr);


            while ((text = buffer.readLine()) != null){
                sb.append(text);
            }

        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(FallDetectedActivity.this, "Error reading file!", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void sendHelp(){
        myLat = sharedPref.getString("Latitude", "");
        myLongi = sharedPref.getString("Longitude", "");

        contactInfo = readFileContacts();
        message = readFileMessage() + " GPS Location, Latitude: " + String.valueOf(myLat) + " Longitude: " + String.valueOf(myLongi);
        String[] split = contactInfo.split("-");
        for(int i = 1; i < split.length; i = i + 2) {

            phoneNr = split[i].trim();
            sendSMS(phoneNr, message);
        }
        Toast.makeText(FallDetectedActivity.this, "Emergency message sent to your contacts!", Toast.LENGTH_SHORT).show();
        DataStorageClass dcs = new DataStorageClass();
        dcs.execute("Emergency", userName, String.valueOf(myLat), String.valueOf(myLongi), "true");
    }

    public void falseAlarm(){
        myLat = sharedPref.getString("Latitude", "");
        myLongi = sharedPref.getString("Longitude", "");
        DataStorageClass dcs = new DataStorageClass();
        dcs.execute("Emergency", userName, String.valueOf(myLat), String.valueOf(myLongi), "false");
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
