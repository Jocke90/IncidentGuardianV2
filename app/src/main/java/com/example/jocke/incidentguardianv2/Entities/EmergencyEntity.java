package com.example.jocke.incidentguardianv2.Entities;


import com.microsoft.azure.storage.table.TableServiceEntity;

public class EmergencyEntity extends TableServiceEntity{

    public EmergencyEntity(String userName, String timestamp){
        this.partitionKey = userName;
        this.rowKey = userName + timestamp;
    }

    public EmergencyEntity(){
    }

    private String type;
    private Double latitude;
    private Double longitude;
    private Boolean calledForHelp;

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

    public Boolean getCalledForHelp() {
        return calledForHelp;
    }

    public void setCalledForHelp(Boolean calledForHelp) {
        this.calledForHelp = calledForHelp;
    }
}
