package com.example.jocke.incidentguardianv2.Activities;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jocke.incidentguardianv2.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MonitorActivity extends AppCompatActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView latitudeText;
    TextView longitudeText;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    public Double myLatitude;
    public Double myLongitude;

    private SensorManager sensorManager;
    private Sensor accelerometerS;
    private Sensor gyrometerS;
    public Double acceX, acceY, acceZ;
    public Double gyroX, gyroY, gyroZ;
    private Double fall;
    private Location myLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        latitudeText = (TextView) findViewById(R.id.tvLatitude);
        longitudeText = (TextView) findViewById(R.id.tvLongitude);

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
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
        latitudeText.setText("Latitude : " + String.valueOf(myLatitude));
        longitudeText.setText("Longitude : " + String.valueOf(myLongitude));
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

            fall = Math.sqrt(Math.pow(acceX, 2) + Math.pow(acceY, 2) + Math.pow(acceZ, 2));
            if (fall < 3.0) {
                if (myLatitude != null && myLongitude != null) {
                    myLongitude = myLoc.getLongitude();
                    myLatitude = myLoc.getLatitude();
                    Toast.makeText(this, "Fall detected: " + "Long: " + String.valueOf(myLongitude) + " Lat: " + String.valueOf(myLatitude), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Fall detected: ", Toast.LENGTH_SHORT).show();

                }
            }
        }
        if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroX = (double) event.values[0];
            gyroY = (double) event.values[1];
            gyroZ = (double) event.values[2];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
