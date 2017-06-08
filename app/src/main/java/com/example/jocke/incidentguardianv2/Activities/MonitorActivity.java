package com.example.jocke.incidentguardianv2.Activities;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jocke.incidentguardianv2.DataStorageClass;
import com.example.jocke.incidentguardianv2.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;

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
    private String userName;

    private Boolean isAccelerometer;
    private Boolean isGyrometer;
    private Boolean isGps;
    private Integer sampleRate = 0;
    public Integer counterSendData = 0;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    //ArrayList<Object> userSettingList;

    private Timer mTimer;
    private TimerTask mTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        latitudeText = (TextView) findViewById(R.id.textViewLatitude);
        longitudeText = (TextView) findViewById(R.id.textViewLongitude);
        btnStop = (Button) findViewById(R.id.buttonStopMonitoring);
        getUserSettings();
        startTimer();

        //Setting up the connection to google api client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //Setting up location request with set intervals, priority can be changed for higher accuarcy
        locationRequest = new LocationRequest();
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //Setting up for both Accelerometer and Gyrometer sensor
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

    //This method is for getting location updated for google api
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

    //This method set values to latitude and longitude and they are updated when
    //the location sense a change
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
        //startTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerS, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyrometerS, SensorManager.SENSOR_DELAY_NORMAL);
        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
        }
        //startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        stopTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
        stopTimer();
    }

    //This method updated posX, posY and posZ for Accelerometer and Gyrometer when values
    //on them change
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acceX = Double.parseDouble(Float.toString(event.values[0]));
            acceY = Double.parseDouble(Float.toString(event.values[1]));
            acceZ = Double.parseDouble(Float.toString(event.values[2]));

            //fall is for calculating so the app can detect if a fall occurs, in free fall the
            //the accelerometer values are near 0 so when threshold of 2 is passed both latitude
            //and longitude are stored to shared preference then FallDetectedActivity is started,
            fall = Math.sqrt(Math.pow(acceX, 2) + Math.pow(acceY, 2) + Math.pow(acceZ, 2));
            if (fall < 2.0) {
                myLat = myLoc.getLatitude();
                myLongi = myLoc.getLongitude();
                editor = sharedPref.edit();
                editor.putString("Latitude", String.valueOf(myLat));
                editor.putString("Longitude", String.valueOf(myLongi));
                editor.commit();
                startActivity(new Intent(MonitorActivity.this, FallDetectedActivity.class));
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
    //Method for getting values from shared preferences
    public void getUserSettings() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        userName = sharedPref.getString("Username", "");
        isAccelerometer = sharedPref.getBoolean("AccelerometerCheck", false);
        isGyrometer = sharedPref.getBoolean("GyrometerCheck", false);
        isGps = sharedPref.getBoolean("GpsCheck", false);
        sampleRate = sharedPref.getInt("SampleRate", 0);

    }
    //Method for starting new executes that depends which sensor is set to TRUE to asyncTask class
    public void sendData(){
        counterSendData++;
        if(isGps == true) {
            if (myLat != null && myLongi != null) {
                DataStorageClass dcs1 = new DataStorageClass();
                Log.d("Isgps send data", "");
                dcs1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Gps", userName, String.valueOf(myLat), String.valueOf(myLongi));
            }
        }
        if(isAccelerometer == true) {
            if (acceX != null && acceY != null && acceZ != null) {
                DataStorageClass dcs2 = new DataStorageClass();
                Log.d("IsAcce send data", "");
                dcs2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Accelerometer", userName, String.valueOf(acceX), String.valueOf(acceY), String.valueOf(acceZ));
            }
        }
        if(isGyrometer == true) {
            if (gyroX != null && gyroY != null && gyroZ != null) {
                DataStorageClass dcs3 = new DataStorageClass();
                Log.d("isGyro send data", "");
                dcs3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Gyrometer", userName, String.valueOf(gyroX), String.valueOf(gyroY), String.valueOf(gyroZ));
            }
        }
        if(counterSendData == 10){
            counterSendData = 0;
        }
    }
    //Method that is for setting how often sendData will run, this depends on which sampleRate is
    //set for the user
    private void startTimer(){
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            public void run() {
                sendData();
            }
        };

        mTimer.schedule(mTimerTask, 1, sampleRate);
    }
    //Method that stop the timer
    private void stopTimer(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
        }
    }
}
