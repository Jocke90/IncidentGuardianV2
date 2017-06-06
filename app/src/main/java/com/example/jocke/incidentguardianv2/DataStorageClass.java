package com.example.jocke.incidentguardianv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

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

import java.util.ArrayList;


public class DataStorageClass extends AsyncTask<String, Void , Void> {

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

    private Integer countAccelerometerOperations = 0;
    private Integer countGyrometerOperations = 0;
    private Integer countGpsOperations = 0;

    ArrayList<Object> getDataValues;

    String userName;
    Context context;

    MonitorActivity mAct = new MonitorActivity();

    @Override
    protected Void doInBackground(String... params) {
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
                insertAccelerometerBatch(posX, posY, posZ);

            }
            else if(type.equals("Gyrometer")){
                posX = Double.parseDouble(params[1]);
                posY = Double.parseDouble(params[2]);
                posZ = Double.parseDouble(params[3]);
                insertGyrometerBatch(posX, posY, posZ);
            }
            else if(type.equals("Gps")){
                latitude = Double.parseDouble(params[1]);
                longitude = Double.parseDouble(params[2]);
                insertGpsBatch(latitude, longitude);
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
                getData(params[1]);
            }

        }
        catch (Throwable t){

        }
        return null;
    }

    public void insertAccelerometerBatch(Double posX, Double posY, Double posZ) throws StorageException {

        TableBatchOperation accelerometerBatchOperation = new TableBatchOperation();

        time = String.valueOf(System.currentTimeMillis());

        AccelerometerEntity accelerometerEntity = new AccelerometerEntity(userName, time);
        accelerometerEntity.setType("Accelerometer");
        accelerometerEntity.setPosX(posX);
        accelerometerEntity.setPosY(posY);
        accelerometerEntity.setPosZ(posZ);
        accelerometerBatchOperation.insert(accelerometerEntity);
        countAccelerometerOperations++;

        // Submit the operation to the table service.
        if(countAccelerometerOperations == 90) {
            tableCollectedData.execute(accelerometerBatchOperation);
            countAccelerometerOperations = 0;
        }
    }
    public void insertGyrometerBatch(Double posX, Double posY, Double posZ) throws StorageException {

        TableBatchOperation gyrometerBatchOperation = new TableBatchOperation();

        time = String.valueOf(System.currentTimeMillis());

        GyrometerEntity gyrometerEntity = new GyrometerEntity(userName, time);
        gyrometerEntity.setType("Gyrometer");
        gyrometerEntity.setPosX(posX);
        gyrometerEntity.setPosY(posY);
        gyrometerEntity.setPosZ(posZ);
        countGyrometerOperations++;

        // Submit the operation to the table service.
        if(countGyrometerOperations == 90) {
            tableCollectedData.execute(gyrometerBatchOperation);
            countGyrometerOperations = 0;
        }
    }

    public void insertGpsBatch(Double latitude, Double longitude) throws StorageException {

        TableBatchOperation gpsBatchOperation = new TableBatchOperation();

        time = String.valueOf(System.currentTimeMillis());

        GpsEntity gpsEntity = new GpsEntity(userName, time);
        gpsEntity.setType("Gps");
        gpsEntity.setLatitude(latitude);
        gpsEntity.setLongitude(longitude);
        countGpsOperations++;

        // Submit the operation to the table service.
        if(countGpsOperations == 90) {
            tableCollectedData.execute(gpsBatchOperation);
            countGpsOperations = 0;
        }
    }

    public void insertEmergencyEntity(Double latitude, Double longitude, boolean calledForHelp) throws StorageException {

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

    public void getData(String userName) throws StorageException {

        //ArrayList<Object> userData = new ArrayList<>();

        String partitionFilter = TableQuery.generateFilterCondition(
                "PartitionKey", TableQuery.QueryComparisons.EQUAL, userName);

        TableQuery<UserSensorEntity> partitionQuery = TableQuery.from(
                UserSensorEntity.class).where(partitionFilter);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("AccelerometerCheck", true);
        editor.putBoolean("GyrometerCheck", false);
        editor.putBoolean("GpsCheck", true);
        editor.putInt("Samplerate", 2000);
        editor.commit();

        //userData.add(true);
        //userData.add(false);
        //userData.add(true);
        //userData.add(1000);
        // Loop through the results, displaying information about the entity.
        for (UserSensorEntity entity : tableUserSensors.execute(partitionQuery)) {

            //userData.add(entity.getAccelerometer());
            //userData.add(entity.getGyrometer());
            //userData.add(entity.getGps());
            //userData.add(entity.getSampleRate());
        }

        //return userData;
    }
}
