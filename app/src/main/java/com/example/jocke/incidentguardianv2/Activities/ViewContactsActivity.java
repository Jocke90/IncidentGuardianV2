package com.example.jocke.incidentguardianv2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jocke.incidentguardianv2.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ViewContactsActivity extends AppCompatActivity
{
    Button btnBack;
    ListView contactsList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_viewcontacts);

            btnBack = (Button) findViewById(R.id.buttonBack);
            contactsList = (ListView) findViewById(R.id.listViewContacts);

            readFileContacts();

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ViewContactsActivity.this, ContactActivity.class));
                }
            });
        }

    //This method is used to read the txt file with the users contacts and the present them
    //in a listview, the listview is populated with help of an ArrayAdapter
    public String readFileContacts(){
        String text = "";
        ArrayList<String> lines = new ArrayList<>();
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/IncidentGuardianFolder");
        File file = new File(dir, "Contacts.txt");

        try{
            FileInputStream fis = new FileInputStream(file);

            if(fis != null) {
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader buffer = new BufferedReader(isr);


                while ((text = buffer.readLine()) != null) {
                    lines.add(text);
                }
                buffer.close();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lines);
                contactsList.setAdapter(adapter);
            }

        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(ViewContactsActivity.this, "Error reading file!", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return text;
    }
}
