package com.example.jocke.incidentguardianv2.Entities;

import com.microsoft.azure.storage.table.TableServiceEntity;


public class UserSensorEntity extends TableServiceEntity {

    public UserSensorEntity(String userName, String timestamp){
        this.partitionKey = userName;
        this.rowKey = userName + timestamp;
    }

    public UserSensorEntity(){
    }

    private boolean accelerometer;
    private boolean gyrometer;
    private boolean gps;
    private boolean emergency;
    private int sampleRate;

    public boolean isAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(boolean accelerometer) {
        this.accelerometer = accelerometer;
    }

    public boolean isGyrometer() {
        return gyrometer;
    }

    public void setGyrometer(boolean gyrometer) {
        this.gyrometer = gyrometer;
    }

    public boolean isGps() {
        return gps;
    }

    public void setGps(boolean gps) {
        this.gps = gps;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }
}
