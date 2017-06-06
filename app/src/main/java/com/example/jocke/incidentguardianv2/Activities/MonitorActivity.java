package com.example.jocke.incidentguardianv2.Activities;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jocke.incidentguardianv2.DataStorageClass;
import com.example.jocke.incidentguardianv2.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MonitorActivity extends AppCompatActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView latitudeText;
    TextView longitudeText;
    Button btnStop;

    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location myLoc;
    public Double myLat;
    public Double myLongi;

    private SensorManager sensorManager;
    private Sensor accelerometerS;
    private Sensor gyrometerS;
    public Double acceX, acceY, acceZ;
    public Double gyroX, gyroY, gyroZ;
    private Double fall;

    private String contactInfo;
    private String phoneNr;
    private String message;
    private String userName;

    private Boolean isAccelerometer;
    private Boolean isGyrometer;
    private Boolean isGps;
    private Integer sampleRate;

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        latitudeText = (TextView) findViewById(R.id.textViewLatitude);
        longitudeText = (TextView) findViewById(R.id.textViewLongitude);
        btnStop = (Button) findViewById(R.id.buttonStopMonitoring);

        getUserSettings();
        Toast.makeText(MonitorActivity.this, "Username from sharedpref: " + userName, Toast.LENGTH_SHORT).show();

        DataStorageClass dcs = new DataStorageClass();
        dcs.execute("getData", userName);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerS = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyrometerS = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accelerometerS, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyrometerS, SensorManager.SENSOR_DELAY_NORMAL);

        /*handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(isGps == true) {
                    if (myLat != null && myLongi != null) {
                        DataStorageClass dcs = new DataStorageClass();
                        dcs.execute("Gps", String.valueOf(myLat), String.valueOf(myLongi));
                    }
                }
                if(isAccelerometer == true) {
                    if (acceX != null && acceY != null && acceZ != null) {
                        DataStorageClass dcs = new DataStorageClass();
                        dcs.execute("Accelerometer", String.valueOf(acceX), String.valueOf(acceY), String.valueOf(acceZ));
                    }
                }
                if(isGyrometer == true) {
                    if (gyroX != null && gyroY != null && gyroZ != null) {
                        DataStorageClass dcs = new DataStorageClass();
                        dcs.execute("Gyrometer", String.valueOf(gyroX), String.valueOf(gyroY), String.valueOf(gyroZ));
                    }
                }
                handler.postDelayed(runnable, sampleRate);
            }
        };
        handler.post(runnable);
*/
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MonitorActivity.this, MenuActivity.class));
            }
        });

    }

    @Override
    public void onConnected(Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLoc = location;
        myLat = location.getLatitude();
        myLongi = location.getLongitude();
        latitudeText.setText("Latitude : " + String.valueOf(myLat));
        longitudeText.setText("Longitude : " + String.valueOf(myLongi));
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerS, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyrometerS, SensorManager.SENSOR_DELAY_NORMAL);
        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
        //handler.removeCallbacks(runnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acceX = Double.parseDouble(Float.toString(event.values[0]));
            acceY = Double.parseDouble(Float.toString(event.values[1]));
            acceZ = Double.parseDouble(Float.toString(event.values[2]));

            fall = Math.sqrt(Math.pow(acceX, 2) + Math.pow(acceY, 2) + Math.pow(acceZ, 2));
            if (fall < 2.0) {
                fallDetected();
            }
        }
        if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroX = Double.parseDouble(Float.toString(event.values[0]));
            gyroY = Double.parseDouble(Float.toString(event.values[1]));
            gyroZ = Double.parseDouble(Float.toString(event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void fallDetected(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("A fall was detected, are you ok?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        myLat = myLoc.getLatitude();
                        myLongi = myLoc.getLongitude();
                        DataStorageClass dcs = new DataStorageClass();
                        dcs.execute("Emergency", String.valueOf(myLat), String.valueOf(myLongi), "false");
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendHelp();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
            Toast.makeText(MonitorActivity.this, "Error reading file!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MonitorActivity.this, "Error reading file!", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void sendHelp(){
        myLat = myLoc.getLatitude();
        myLongi = myLoc.getLongitude();

        contactInfo = readFileContacts();
        message = readFileMessage() + " GPS Location, Latitude: " + String.valueOf(myLat) + " Longitude: " + String.valueOf(myLongi);
        String[] split = contactInfo.split("-");
        for(int i = 1; i < split.length; i = i + 2) {

            phoneNr = split[i].trim();
            sendSMS(phoneNr, message);
        }
        Toast.makeText(MonitorActivity.this, "Emergency message sent to your contacts!", Toast.LENGTH_SHORT).show();
        DataStorageClass dcs = new DataStorageClass();
        dcs.execute("Emergency", String.valueOf(myLat), String.valueOf(myLongi), "true");


    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    /*public void getUserData(ArrayList<Object> userData){
        isAccelerometer = (Boolean) userData.get(0);
        isGyrometer = (Boolean) userData.get(1);
        isGps = (Boolean) userData.get(2);
        sampleRate = (Integer) userData.get(3);
        Toast.makeText(MonitorActivity.this, "Values from async: " + isAccelerometer + " " + isGyrometer + " " + isGps + " " + sampleRate, Toast.LENGTH_SHORT).show();

    }*/
    public void getUserSettings(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        userName = sharedPref.getString("Username", "");
        isAccelerometer = sharedPref.getBoolean("AccelerometerCheck", false);
        isGyrometer = sharedPref.getBoolean("GyrorometerCheck", false);
        isGps = sharedPref.getBoolean("GpsCheck", false);
        sampleRate = sharedPref.getInt("Samplerate", 0);
        Toast.makeText(MonitorActivity.this, "Values from async: " + isAccelerometer + " " + isGyrometer + " " + isGps + " " + sampleRate, Toast.LENGTH_SHORT).show();
    }


}
