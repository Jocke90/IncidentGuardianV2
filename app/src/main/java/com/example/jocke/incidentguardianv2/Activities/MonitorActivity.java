package com.example.jocke.incidentguardianv2.Activities;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
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

    String contactInfo;
    String phoneNr;
    String message;

    ArrayList<Double> accelerometerList;
    ArrayList<Double> gyrometerList;
    ArrayList<Double> gpsList;
    ArrayList<Double> emergencyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        latitudeText = (TextView) findViewById(R.id.textViewLatitude);
        longitudeText = (TextView) findViewById(R.id.textViewLongitude);
        btnStop = (Button) findViewById(R.id.buttonStopMonitoring);

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

        gpsList.add(myLat);
        gpsList.add(myLongi);

        if(gpsList.size() == 9){
            DataStorageClass.MyTaskParams params = new DataStorageClass.MyTaskParams("Gps", gpsList);
            DataStorageClass dataStorageClass = new DataStorageClass();
            dataStorageClass.execute(params);
            gpsList.clear();
        }

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
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acceX = (double) event.values[0];
            acceY = (double) event.values[1];
            acceZ = (double) event.values[2];

            accelerometerList.add(acceX);
            accelerometerList.add(acceY);
            accelerometerList.add(acceZ);

            if(accelerometerList.size() == 299) {

                DataStorageClass.MyTaskParams params = new DataStorageClass.MyTaskParams("Accelerometer", accelerometerList);
                DataStorageClass dataStorageClass = new DataStorageClass();
                dataStorageClass.execute(params);
                accelerometerList.clear();
            }

            fall = Math.sqrt(Math.pow(acceX, 2) + Math.pow(acceY, 2) + Math.pow(acceZ, 2));
            if (fall < 2.0) {
                fallDetected();
            }
        }
        if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroX = (double) event.values[0];
            gyroY = (double) event.values[1];
            gyroZ = (double) event.values[2];

            gyrometerList.add(gyroX);
            gyrometerList.add(gyroY);
            gyrometerList.add(gyroZ);

            if(gyrometerList.size() == 299) {

                DataStorageClass.MyTaskParams params = new DataStorageClass.MyTaskParams("Gyrorometer", gyrometerList);
                DataStorageClass dataStorageClass = new DataStorageClass();
                dataStorageClass.execute(params);
                gyrometerList.clear();
            }
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
        emergencyList.add(myLat);
        emergencyList.add(myLongi);

        contactInfo = readFileContacts();
        message = readFileMessage() + " GPS Location, Latitude: " + String.valueOf(myLat) + " Longitude: " + String.valueOf(myLongi);
        String[] split = contactInfo.split("-");
        for(int i = 1; i < split.length; i = i + 2) {

            phoneNr = split[i].trim();
            sendSMS(phoneNr, message);
        }
        Toast.makeText(MonitorActivity.this, "Emergency message sent to your contacts!", Toast.LENGTH_SHORT).show();

        DataStorageClass.MyTaskParams params = new DataStorageClass.MyTaskParams("Emergency", emergencyList);
        DataStorageClass dataStorageClass = new DataStorageClass();
        dataStorageClass.execute(params);
        emergencyList.clear();


    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }



}
