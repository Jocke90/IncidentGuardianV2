package com.example.jocke.incidentguardianv2;


import android.os.AsyncTask;

import com.example.jocke.incidentguardianv2.Activities.MainActivity;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.*;

import java.util.UUID;


public class DataStorageClass extends AsyncTask<String, Void ,Void> {

    protected static CloudTableClient tableClient;
    protected static CloudTable tableCollectedData;
    protected final static String tableSensors = "CollectedData";

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

}
