package com.example.jocke.incidentguardianv2;


import java.util.ArrayList;

//Interface for storing values to a list in onPostExecute in AsyncTask Class

public interface AsyncResponse {
    void processFinish(ArrayList<Object> returnList);
}
