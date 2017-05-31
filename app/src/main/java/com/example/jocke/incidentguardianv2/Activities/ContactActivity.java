package com.example.jocke.incidentguardianv2.Activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jocke.incidentguardianv2.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ContactActivity extends AppCompatActivity{

    EditText name;
    EditText phoneNr;
    Button btnAddContact;
    Button btnBack;
    Button btnViewContacts;

    String contactInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        name = (EditText) findViewById(R.id.editTextName);
        phoneNr = (EditText) findViewById(R.id.editTextPhoneNr);
        btnAddContact = (Button) findViewById(R.id.buttonAdd);
        btnBack = (Button) findViewById(R.id.buttonBack);
        btnViewContacts = (Button) findViewById(R.id.buttonViewContacts);

        btnViewContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactActivity.this, ViewContactsActivity.class));
            }
        });

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactInfo = name.getText().toString().trim() + "-" + phoneNr.getText().toString().trim();
                writeToFile(contactInfo);
                name.setText("");
                phoneNr.setText("");
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactActivity.this, MenuActivity.class));
            }
        });



    }
    public void writeToFile(String data) {

            String state;
            state = Environment.getExternalStorageState();

            if(Environment.MEDIA_MOUNTED.equals(state)) {
                File root = Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/IncidentGuardianFolder");

                if (!dir.exists()) {
                    dir.mkdir();
                }

                File file = new File(dir, "Contacts.txt");

                try {
                    FileOutputStream fos = new FileOutputStream(file, true);
                    OutputStreamWriter osr = new OutputStreamWriter(fos);
                    osr.append(data);
                    osr.append("\n");
                    osr.flush();
                    osr.close();
                    Toast.makeText(ContactActivity.this, "Contact added!", Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {

                } catch (IOException ie) {
                    ie.printStackTrace();
                    Toast.makeText(ContactActivity.this, "Message Update Failed!", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "External Storage not found!", Toast.LENGTH_SHORT).show();
            }

        }

    }


