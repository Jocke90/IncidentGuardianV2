package com.example.jocke.incidentguardianv2.Entities;

import com.microsoft.azure.storage.table.TableServiceEntity;


public class UserSensorEntity extends TableServiceEntity {

    public UserSensorEntity(String userName, String timestamp){
        this.partitionKey = userName;
        this.rowKey = userName + timestamp;
    }

    public UserSensorEntity(){
    }

    private Boolean accelerometer;
    private Boolean gyrometer;
    private Boolean gps;
    private Integer sampleRate;


    public Boolean getAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(Boolean accelerometer) {
        this.accelerometer = accelerometer;
    }

    public Boolean getGyrometer() {
        return gyrometer;
    }

    public void setGyrometer(Boolean gyrometer) {
        this.gyrometer = gyrometer;
    }

    public Boolean getGps() {
        return gps;
    }

    public void setGps(Boolean gps) {
        this.gps = gps;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }
}
