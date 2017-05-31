package com.example.jocke.incidentguardianv2;


import android.os.AsyncTask;

import com.example.jocke.incidentguardianv2.Activities.MainActivity;
import com.example.jocke.incidentguardianv2.Entities.AccelerometerEntity;
import com.example.jocke.incidentguardianv2.Entities.EmergencyEntity;
import com.example.jocke.incidentguardianv2.Entities.GpsEntity;
import com.example.jocke.incidentguardianv2.Entities.GyrometerEntity;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.*;

import java.util.ArrayList;
import java.util.UUID;


public class DataStorageClass extends AsyncTask<String, Void ,Void> {

    protected static CloudTableClient tableClient;
    protected static CloudTable tableCollectedData;
    protected final static String tableSensors = "CollectedData";
    int countAccelemeterValues = 0;
    int countGyrometerValues = 0;
    int countGpsValues = 0;

    @Override
    protected Void doInBackground(String... params) {

        try{
            CloudStorageAccount account = CloudStorageAccount.parse(MainActivity.storageConnectionString);
            tableClient = account.createCloudTableClient();
            tableCollectedData = tableClient.getTableReference(tableSensors + UUID.randomUUID().toString().replace("-", ""));
            tableCollectedData.createIfNotExists();

        }
        catch (Throwable t){

        }
        return null;
    }

    public void InsertBatchAccelerometer(ArrayList<Double> accelerometerValues) throws StorageException {
        // Note: the limitations on a batch operation are
        // - up to 100 operations
        // - all operations must share the same PartitionKey
        // - if a retrieve is used it can be the only operation in the batch
        // - the serialized batch payload must be 4 MB or less

        // Define a batch operation.
        TableBatchOperation batchOperation = new TableBatchOperation();

        while(countAccelemeterValues < accelerometerValues.size()){
            AccelerometerEntity accelerometerEntity = new AccelerometerEntity("Username", "Username+timestamp");
            accelerometerEntity.setType("Accelerometer");
            accelerometerEntity.setPosX(accelerometerValues.get(countAccelemeterValues));
            countAccelemeterValues++;
            accelerometerEntity.setPosY(accelerometerValues.get(countAccelemeterValues));
            countAccelemeterValues++;
            accelerometerEntity.setPosZ(accelerometerValues.get(countAccelemeterValues));
            countAccelemeterValues++;
            batchOperation.insert(accelerometerEntity);
        }

        // Execute the batch of operations on the "tablebasics" table.
        tableCollectedData.execute(batchOperation);
        countAccelemeterValues = 0;
    }
    public void InsertBatchGyrometer(ArrayList<Double> gyrometerValues) throws StorageException {
        // Note: the limitations on a batch operation are
        // - up to 100 operations
        // - all operations must share the same PartitionKey
        // - if a retrieve is used it can be the only operation in the batch
        // - the serialized batch payload must be 4 MB or less

        // Define a batch operation.
        TableBatchOperation batchOperation = new TableBatchOperation();

        while(countGyrometerValues < gyrometerValues.size()){
            GyrometerEntity gyrometerEntity = new GyrometerEntity("Username", "Username+timestamp");
            gyrometerEntity.setType("Gyrometer");
            gyrometerEntity.setPosX(gyrometerValues.get(countGyrometerValues));
            countGyrometerValues++;
            gyrometerEntity.setPosY(gyrometerValues.get(countGyrometerValues));
            countGyrometerValues++;
            gyrometerEntity.setPosZ(gyrometerValues.get(countGyrometerValues));
            countGyrometerValues++;
            batchOperation.insert(gyrometerEntity);
        }

        // Execute the batch of operations on the "tablebasics" table.
        tableCollectedData.execute(batchOperation);
        countGyrometerValues = 0;
    }

    public void InsertBatchGps(ArrayList<Double> gpsValues) throws StorageException {
        // Note: the limitations on a batch operation are
        // - up to 100 operations
        // - all operations must share the same PartitionKey
        // - if a retrieve is used it can be the only operation in the batch
        // - the serialized batch payload must be 4 MB or less

        // Define a batch operation.
        TableBatchOperation batchOperation = new TableBatchOperation();

        while(countGpsValues < gpsValues.size()){
            GpsEntity gpsEntity = new GpsEntity("Username", "Username+timestamp");
            gpsEntity.setType("Gps");
            gpsEntity.setLatitude(gpsValues.get(countGpsValues));
            countGpsValues++;
            gpsEntity.setLongitude(gpsValues.get(countGpsValues));
            countGpsValues++;
            batchOperation.insert(gpsEntity);
        }

        // Execute the batch of operations on the "tablebasics" table.
        tableCollectedData.execute(batchOperation);
        countGpsValues = 0;
    }

    public void InsertEmergencyEntity(Double latitude, Double longitude) throws StorageException {
        // Note: the limitations on an insert operation are
        // - the serialized payload must be 1 MB or less
        // - up to 252 properties in addition to the partition key, row key and
        // timestamp. 255 properties in total
        // - the serialized payload of each property must be 64 KB or less

        // Create a new customer entity.
        EmergencyEntity emergencyEntity = new EmergencyEntity("Username", "Username+timestamp");
        emergencyEntity.setType("Emergency");
        emergencyEntity.setLatitude(latitude);
        emergencyEntity.setLongitude(longitude);
        emergencyEntity.setCalledForHelp(true);
        // Create an operation to add the new customer to the tablebasics table.
        TableOperation insertEmergency = TableOperation.insert(emergencyEntity);

        // Submit the operation to the table service.
        tableCollectedData.execute(insertEmergency);
    }
}
