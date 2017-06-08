package com.example.jocke.incidentguardianv2.Entities;


import com.microsoft.azure.storage.table.TableServiceEntity;

//Entity for Accelerometer

public class AccelerometerEntity extends TableServiceEntity {

    public AccelerometerEntity(String userName, String timestamp){
        this.partitionKey = userName;
        this.rowKey = userName + timestamp;
    }

    public AccelerometerEntity(){
    }

    private String type;
    private Double posX;
    private Double posY;
    private Double posZ;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPosX() {
        return posX;
    }

    public void setPosX(Double posX) {
        this.posX = posX;
    }

    public Double getPosY() {
        return posY;
    }

    public void setPosY(Double posY) {
        this.posY = posY;
    }

    public Double getPosZ() {
        return posZ;
    }

    public void setPosZ(Double posZ) {
        this.posZ = posZ;
    }
}
