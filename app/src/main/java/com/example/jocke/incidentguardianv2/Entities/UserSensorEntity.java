package com.example.jocke.incidentguardianv2.Entities;

import com.microsoft.azure.storage.table.TableServiceEntity;

//Entity for User settings

public class UserSensorEntity extends TableServiceEntity {

    public UserSensorEntity(String userName, String timestamp){
        this.partitionKey = userName;
        this.rowKey = userName + timestamp;
    }

    public UserSensorEntity(){
    }

    private Boolean Accelerometer;
    private Boolean Gyrometer;
    private Boolean Gps;
    private int SampleRate;


    public Boolean getAccelerometer() {
        return Accelerometer;
    }

    public void setAccelerometer(Boolean accelerometer) {
        Accelerometer = accelerometer;
    }

    public Boolean getGyrometer() {
        return Gyrometer;
    }

    public void setGyrometer(Boolean gyrometer) {
        Gyrometer = gyrometer;
    }

    public Boolean getGps() {
        return Gps;
    }

    public void setGps(Boolean gps) {
        Gps = gps;
    }

    public int getSampleRate() {
        return SampleRate;
    }

    public void setSampleRate(int sampleRate) {
        SampleRate = sampleRate;
    }
}
