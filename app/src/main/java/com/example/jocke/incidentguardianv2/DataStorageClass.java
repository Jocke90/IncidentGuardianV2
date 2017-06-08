package com.example.jocke.incidentguardianv2;

import android.os.AsyncTask;
import android.util.Log;

import com.example.jocke.incidentguardianv2.Activities.MainActivity;
import com.example.jocke.incidentguardianv2.Entities.AccelerometerEntity;
import com.example.jocke.incidentguardianv2.Entities.EmergencyEntity;
import com.example.jocke.incidentguardianv2.Entities.GpsEntity;
import com.example.jocke.incidentguardianv2.Entities.GyrometerEntity;
import com.example.jocke.incidentguardianv2.Entities.UserSensorEntity;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.*;
import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;

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

        //Getting values from params on execute. Params[0] Always type and Params[1] always user name
        type = params[0];
        userName = params[1];
        getDataValues = new ArrayList<>();

        Log.d("Type: ", type);

        try{

            //If statements that will run a specific method depending on which type it is

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
        //This runs when Menu starts to get user settings
        if(!returnList.isEmpty()) {
            delegate.processFinish(returnList);
        }
    }

    //This method stores an Accelerometer Entity with values for posX, posY and posZ
    public void insertAccelerometerBatch(Double posX, Double posY, Double posZ) throws StorageException, URISyntaxException, InvalidKeyException {
        //Setting up cloudstorage account with a connection string that is set in MainActivity class
        //Setting cloud table with correct table name and the creates it if it doesn't exists
        CloudStorageAccount account = CloudStorageAccount.parse(MainActivity.storageConnectionString);
        tableClient = account.createCloudTableClient();
        tableCollectedData = tableClient.getTableReference(tableSensorData);
        tableCollectedData.createIfNotExists();


        // Creates a batch operation to be able to add a AccelerometerEntity then insert it before it submits to the table
        TableBatchOperation accelerometerBatchOperation = new TableBatchOperation();

        time = String.valueOf(System.currentTimeMillis());

        AccelerometerEntity accelerometerEntity = new AccelerometerEntity(userName, time);
        accelerometerEntity.setType("Accelerometer");
        accelerometerEntity.setPosX(posX);
        accelerometerEntity.setPosY(posY);
        accelerometerEntity.setPosZ(posZ);
        accelerometerBatchOperation.insert(accelerometerEntity);

        tableCollectedData.execute(accelerometerBatchOperation);

    }

    //This method stores an Gyrometer Entity with values for posX, posY and posZ
    public void insertGyrometerBatch(Double posX, Double posY, Double posZ) throws StorageException, URISyntaxException, InvalidKeyException {
        //Setting up cloudstorage account with a connection string that is set in MainActivity class
        //Setting cloud table with correct table name and the creates it if it doesn't exists
        CloudStorageAccount account = CloudStorageAccount.parse(MainActivity.storageConnectionString);
        tableClient = account.createCloudTableClient();
        tableCollectedData = tableClient.getTableReference(tableSensorData);
        tableCollectedData.createIfNotExists();


        // Creates a batch operation to be able to add a GyrometerEntity then insert it before it submits to the table
        TableBatchOperation gyrometerBatchOperation = new TableBatchOperation();

        time = String.valueOf(System.currentTimeMillis());

        GyrometerEntity gyrometerEntity = new GyrometerEntity(userName, time);
        gyrometerEntity.setType("Gyrometer");
        gyrometerEntity.setPosX(posX);
        gyrometerEntity.setPosY(posY);
        gyrometerEntity.setPosZ(posZ);
        gyrometerBatchOperation.insert(gyrometerEntity);

        tableCollectedData.execute(gyrometerBatchOperation);

    }

    //This method stores an Gps Entity with values of latitude and longitude
    public void insertGpsBatch(Double latitude, Double longitude) throws StorageException, URISyntaxException, InvalidKeyException {
        //Setting up cloudstorage account with a connection string that is set in MainActivity class
        //Setting cloud table with correct table name and the creates it if it doesn't exists
        CloudStorageAccount account = CloudStorageAccount.parse(MainActivity.storageConnectionString);
        tableClient = account.createCloudTableClient();
        tableCollectedData = tableClient.getTableReference(tableSensorData);
        tableCollectedData.createIfNotExists();

        // Creates a batch operation to be able to add a GpsEntity then insert it before it submits to the table
        TableBatchOperation gpsBatchOperation = new TableBatchOperation();

        time = String.valueOf(System.currentTimeMillis());

        GpsEntity gpsEntity = new GpsEntity(userName, time);
        gpsEntity.setType("Gps");
        gpsEntity.setLatitude(latitude);
        gpsEntity.setLongitude(longitude);
        gpsBatchOperation.insert(gpsEntity);

        tableCollectedData.execute(gpsBatchOperation);

    }

    //This method stores an Emergency Entity with values latitude, longitude and a boolean if the user called for help
    public void insertEmergencyEntity(Double latitude, Double longitude, boolean calledForHelp) throws URISyntaxException, InvalidKeyException, StorageException {
        //Setting up cloudstorage account with a connection string that is set in MainActivity class
        //Setting cloud table with correct table name and the creates it if it doesn't exists
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

        // Creates and operation to add an Emergency
        TableOperation insertEmergency = TableOperation.insert(emergencyEntity);

        // Submit the operation to the table.
        tableCollectedData.execute(insertEmergency);

    }

    //This method gets the users setting when MenuActivity loads, to be able to set which sensor values that shall be stored and how often
    public ArrayList<Object> getData(String userName) throws StorageException, URISyntaxException, InvalidKeyException {
        //Setting up cloudstorage account with a connection string that is set in MainActivity class
        //Setting cloud table with correct table name
        CloudStorageAccount account = CloudStorageAccount.parse(MainActivity.storageConnectionString);
        tableClient = account.createCloudTableClient();
        tableUserSensors = tableClient.getTableReference(tableUserSensorStatus);
        ArrayList<Object> userData = new ArrayList<>();

        //Setup a Partition filter with the username to be able to get the users settings for Sensors and SampleRate
        String partitionFilter = TableQuery.generateFilterCondition("PartitionKey", QueryComparisons.EQUAL, userName);

        TableQuery<UserSensorEntity> partitionQuery = TableQuery.from(UserSensorEntity.class).where(partitionFilter);

        // Loop through the results and add to an ArrayList of Object
        for (UserSensorEntity entity : tableUserSensors.execute(partitionQuery)) {

            userData.add(entity.getAccelerometer());
            userData.add(entity.getGyrometer());
            userData.add(entity.getGps());
            userData.add(entity.getSampleRate());
        }

        return userData;
    }
}
