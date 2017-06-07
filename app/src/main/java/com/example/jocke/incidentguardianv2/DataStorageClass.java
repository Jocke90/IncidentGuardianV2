package com.example.jocke.incidentguardianv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.jocke.incidentguardianv2.Activities.MainActivity;
import com.example.jocke.incidentguardianv2.Activities.MonitorActivity;
import com.example.jocke.incidentguardianv2.Entities.AccelerometerEntity;
import com.example.jocke.incidentguardianv2.Entities.EmergencyEntity;
import com.example.jocke.incidentguardianv2.Entities.GpsEntity;
import com.example.jocke.incidentguardianv2.Entities.GyrometerEntity;
import com.example.jocke.incidentguardianv2.Entities.UserSensorEntity;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.*;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;


public class DataStorageClass extends AsyncTask<String, Void , ArrayList<Object>> {

    protected static CloudTableClient tableClient;
    protected static CloudTable tableCollectedData;
    protected static CloudTable tableUserSensors;
    protected final static String tableSensorData = "CollectedData";
    protected final static String tableUserSensorStatus = "UserSensors";

    private String time;
    private String type;
    private Double posX;
    private Double posY;
    private Double posZ;
    private Double latitude;
    private Double longitude;
    private String checkCalledForHelp;
    private Boolean calledForHelp;

    ArrayList<Object> getDataValues;
    public AsyncResponse delegate = null;

    String userName;


    @Override
    protected ArrayList<Object> doInBackground(String... params) {
        type = params[0];
        userName = params[1];
        getDataValues = new ArrayList<>();

        Log.d("Type: ", type);

        try{

            if(type.equals("Accelerometer")){
                posX = Double.parseDouble(params[2]);
                posY = Double.parseDouble(params[3]);
                posZ = Double.parseDouble(params[4]);
                insertAccelerometerBatch(posX, posY, posZ);

            }
            else if(type.equals("Gyrometer")){
                posX = Double.parseDouble(params[2]);
                posY = Double.parseDouble(params[3]);
                posZ = Double.parseDouble(params[4]);
                insertGyrometerBatch(posX, posY, posZ);
            }
            else if(type.equals("Gps")){
                latitude = Double.parseDouble(params[2]);
                longitude = Double.parseDouble(params[3]);
                insertGpsBatch(latitude, longitude);
            }
            else if(type.equals("Emergency")){
                latitude = Double.parseDouble(params[2]);
                longitude = Double.parseDouble(params[3]);
                checkCalledForHelp = params[4];
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
    protected void onPostExecute(ArrayList<Object> returnList){
        delegate.processFinish(returnList);
    }

    public void insertAccelerometerBatch(Double posX, Double posY, Double posZ) throws StorageException, URISyntaxException, InvalidKeyException {
        CloudStorageAccount account = CloudStorageAccount.parse(MainActivity.storageConnectionString);
        tableClient = account.createCloudTableClient();
        tableCollectedData = tableClient.getTableReference(tableSensorData);
        tableCollectedData.createIfNotExists();

        TableBatchOperation accelerometerBatchOperation = new TableBatchOperation();

        time = String.valueOf(System.currentTimeMillis());

        AccelerometerEntity accelerometerEntity = new AccelerometerEntity(userName, time);
        accelerometerEntity.setType("Accelerometer");
        accelerometerEntity.setPosX(posX);
        accelerometerEntity.setPosY(posY);
        accelerometerEntity.setPosZ(posZ);
        accelerometerBatchOperation.insert(accelerometerEntity);
        // Submit the operation to the table service.

        tableCollectedData.execute(accelerometerBatchOperation);

    }
    public void insertGyrometerBatch(Double posX, Double posY, Double posZ) throws StorageException, URISyntaxException, InvalidKeyException {
        CloudStorageAccount account = CloudStorageAccount.parse(MainActivity.storageConnectionString);
        tableClient = account.createCloudTableClient();
        tableCollectedData = tableClient.getTableReference(tableSensorData);
        tableCollectedData.createIfNotExists();

        TableBatchOperation gyrometerBatchOperation = new TableBatchOperation();

        time = String.valueOf(System.currentTimeMillis());

        GyrometerEntity gyrometerEntity = new GyrometerEntity(userName, time);
        gyrometerEntity.setType("Gyrometer");
        gyrometerEntity.setPosX(posX);
        gyrometerEntity.setPosY(posY);
        gyrometerEntity.setPosZ(posZ);
        gyrometerBatchOperation.insert(gyrometerEntity);
        // Submit the operation to the table service.

        tableCollectedData.execute(gyrometerBatchOperation);

    }

    public void insertGpsBatch(Double latitude, Double longitude) throws StorageException, URISyntaxException, InvalidKeyException {
        CloudStorageAccount account = CloudStorageAccount.parse(MainActivity.storageConnectionString);
        tableClient = account.createCloudTableClient();
        tableCollectedData = tableClient.getTableReference(tableSensorData);
        tableCollectedData.createIfNotExists();

        TableBatchOperation gpsBatchOperation = new TableBatchOperation();

        time = String.valueOf(System.currentTimeMillis());

        GpsEntity gpsEntity = new GpsEntity(userName, time);
        gpsEntity.setType("Gps");
        gpsEntity.setLatitude(latitude);
        gpsEntity.setLongitude(longitude);
        gpsBatchOperation.insert(gpsEntity);
        // Submit the operation to the table service.

        tableCollectedData.execute(gpsBatchOperation);

    }

    public void insertEmergencyEntity(Double latitude, Double longitude, boolean calledForHelp) throws StorageException, URISyntaxException, InvalidKeyException {
        CloudStorageAccount account = CloudStorageAccount.parse(MainActivity.storageConnectionString);
        tableClient = account.createCloudTableClient();
        tableCollectedData = tableClient.getTableReference(tableSensorData);
        tableCollectedData.createIfNotExists();

        time = String.valueOf(System.currentTimeMillis());
        EmergencyEntity emergencyEntity = new EmergencyEntity(userName, time);
        emergencyEntity.setType("Emergency");
        emergencyEntity.setLatitude(latitude);
        emergencyEntity.setLongitude(longitude);
        emergencyEntity.setCalledForHelp(calledForHelp);

        // Create an operation to add the new customer to the tablebasics table.
        TableOperation insertEmergency = TableOperation.insert(emergencyEntity);

        // Submit the operation to the table service.
        tableCollectedData.execute(insertEmergency);
    }

    public ArrayList<Object> getData(String userName) throws StorageException, URISyntaxException, InvalidKeyException {
        CloudStorageAccount account = CloudStorageAccount.parse(MainActivity.storageConnectionString);
        tableClient = account.createCloudTableClient();
        tableUserSensors = tableClient.getTableReference(tableUserSensorStatus);
        tableUserSensors.createIfNotExists();
        ArrayList<Object> userData = new ArrayList<>();

        String partitionFilter = TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, userName);

        TableQuery<UserSensorEntity> partitionQuery = TableQuery.from(UserSensorEntity.class).where(partitionFilter);

        //userData.add(true);
        //userData.add(false);
        //userData.add(true);
        //userData.add(1000);
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
