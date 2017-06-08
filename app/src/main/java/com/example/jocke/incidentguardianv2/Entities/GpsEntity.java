package com.example.jocke.incidentguardianv2.Entities;


import com.microsoft.azure.storage.table.TableServiceEntity;

//Entity for Gps

public class GpsEntity extends TableServiceEntity{

    public GpsEntity(String userName, String timestamp){
        this.partitionKey = userName;
        this.rowKey = userName + timestamp;
    }

    public GpsEntity(){
    }

    private String type;
    private Double latitude;
    private Double longitude;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
