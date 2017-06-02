package com.example.jocke.incidentguardianv2;


import android.os.AsyncTask;

import com.example.jocke.incidentguardianv2.Activities.MainActivity;
import com.example.jocke.incidentguardianv2.Entities.AccelerometerEntity;
import com.example.jocke.incidentguardianv2.Entities.EmergencyEntity;
import com.example.jocke.incidentguardianv2.Entities.GpsEntity;
import com.example.jocke.incidentguardianv2.Entities.GyrometerEntity;
import com.example.jocke.incidentguardianv2.Entities.UserSensorEntity;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.*;

import java.util.ArrayList;


public class DataStorageClass extends AsyncTask<String, Void , ArrayList<Object>> {

    protected static CloudTableClient tableClient;
    protected static CloudTable tableCollectedData;
    protected static CloudTable tableUserSensors;
    protected final static String tableSensorData = "CollectedData";
    protected final static String tableUserSensorStatus = "UserSensors";

    String time;
    String type;
    Double posX;
    Double posY;
    Double posZ;
    Double latitude;
    Double longitude;
    String checkCalledForHelp;
    Boolean calledForHelp;

    ArrayList<Object> getDataValues;

    MainActivity mAct = new MainActivity();

    @Override
    protected ArrayList<Object> doInBackground(String... params) {
        type = params[0];
        getDataValues = new ArrayList<>();

        try{
            CloudStorageAccount account = CloudStorageAccount.parse(MainActivity.storageConnectionString);
            tableClient = account.createCloudTableClient();
            tableCollectedData = tableClient.getTableReference(tableSensorData);
            tableCollectedData.createIfNotExists();

            tableUserSensors = tableClient.getTableReference(tableUserSensorStatus);
            tableUserSensors.createIfNotExists();

            if(type.equals("Accelerometer")){
                posX = Double.parseDouble(params[1]);
                posY = Double.parseDouble(params[2]);
                posZ = Double.parseDouble(params[3]);
                insertAccelerometerEntity(posX, posY, posZ);

            }
            else if(type.equals("Gyrometer")){
                posX = Double.parseDouble(params[1]);
                posY = Double.parseDouble(params[2]);
                posZ = Double.parseDouble(params[3]);
                insertGyrometerEntity(posX, posY, posZ);
            }
            else if(type.equals("Gps")){
                latitude = Double.parseDouble(params[1]);
                longitude = Double.parseDouble(params[2]);
                insertGpsEntity(latitude, longitude);
            }
            else if(type.equals("Emergency")){
                latitude = Double.parseDouble(params[1]);
                longitude = Double.parseDouble(params[2]);
                checkCalledForHelp = params[3];
                if(checkCalledForHelp.equals("true")){
                    calledForHelp = true;
                    insertEmergencyEntity(latitude, longitude, calledForHelp);
                }
                else{
                    calledForHelp = false;
                    insertEmergencyEntity(latitude, longitude, calledForHelp);
                }

            }
            else if(type.equals("getData")){
                getDataValues.addAll(getData(params[1]));
            }

        }
        catch (Throwable t){

        }
        return getDataValues;
    }

    @Override
    protected void onPostExecute(ArrayList<Object> result)
    {
        mAct.getUserData(result);
    }



    public void insertAccelerometerEntity(Double posX, Double posY, Double posZ) throws StorageException {

        time = String.valueOf(System.currentTimeMillis());
        AccelerometerEntity accelerometerEntity = new AccelerometerEntity("Username", time);
        accelerometerEntity.setType("Accelerometer");
        accelerometerEntity.setPosX(posX);
        accelerometerEntity.setPosY(posY);
        accelerometerEntity.setPosZ(posZ);

        TableOperation insertAccelerometer = TableOperation.insert(accelerometerEntity);

        // Submit the operation to the table service.
        tableCollectedData.execute(insertAccelerometer);
    }
    public void insertGyrometerEntity(Double posX, Double posY, Double posZ) throws StorageException {

            time = String.valueOf(System.currentTimeMillis());
            GyrometerEntity gyrometerEntity = new GyrometerEntity("Username", time);
            gyrometerEntity.setType("Gyrometer");
            gyrometerEntity.setPosX(posX);
            gyrometerEntity.setPosY(posY);
            gyrometerEntity.setPosZ(posZ);

            TableOperation insertGyrometer = TableOperation.insert(gyrometerEntity);

            // Submit the operation to the table service.
            tableCollectedData.execute(insertGyrometer);
    }

    public void insertGpsEntity(Double latitude, Double longitude) throws StorageException {

        time = String.valueOf(System.currentTimeMillis());
        GpsEntity gpsEntity = new GpsEntity("Username", time);
        gpsEntity.setType("Gps");
        gpsEntity.setLatitude(latitude);
        gpsEntity.setLongitude(longitude);

        TableOperation insertGps = TableOperation.insert(gpsEntity);

        // Submit the operation to the table service.
        tableCollectedData.execute(insertGps);
    }

    public void insertEmergencyEntity(Double latitude, Double longitude, boolean calledForHelp) throws StorageException {

        time = String.valueOf(System.currentTimeMillis());
        EmergencyEntity emergencyEntity = new EmergencyEntity("Username", time);
        emergencyEntity.setType("Emergency");
        emergencyEntity.setLatitude(latitude);
        emergencyEntity.setLongitude(longitude);
        emergencyEntity.setCalledForHelp(calledForHelp);

        // Create an operation to add the new customer to the tablebasics table.
        TableOperation insertEmergency = TableOperation.insert(emergencyEntity);

        // Submit the operation to the table service.
        tableCollectedData.execute(insertEmergency);
    }

    public ArrayList<Object> getData(String userName) throws StorageException {

        ArrayList<Object> userData = new ArrayList<>();

        String partitionFilter = TableQuery.generateFilterCondition(
                "PartitionKey", TableQuery.QueryComparisons.EQUAL, userName);

        TableQuery<UserSensorEntity> partitionQuery = TableQuery.from(
                UserSensorEntity.class).where(partitionFilter);

        // Loop through the results, displaying information about the entity.
        for (UserSensorEntity entity : tableUserSensors.execute(partitionQuery)) {
            userData.add(entity.getAccelerometer());
            userData.add(entity.getGyrometer());
            userData.add(entity.getGps());
            userData.add(entity.getSampleRate());
        }

        return userData;
    }
}
