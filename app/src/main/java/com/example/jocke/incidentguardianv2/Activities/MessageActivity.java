package com.example.jocke.incidentguardianv2.Activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jocke.incidentguardianv2.R;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MessageActivity extends AppCompatActivity {

    Button btnUpdateMessage;
    Button btnBack;
    EditText message;
    TextView currentMessage;
    String messageData;
    Context context;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        btnUpdateMessage = (Button) findViewById(R.id.buttonUpdate);
        btnBack = (Button) findViewById(R.id.buttonBack);
        message = (EditText) findViewById(R.id.editTextMessage);
        currentMessage = (TextView) findViewById(R.id.textViewCurrentMessage);

        currentMessage.setText("Current message: " + readFile());

        btnUpdateMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageData = message.getText().toString();
                writeToFile(messageData);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, MenuActivity.class));
            }
        });
    }
    public void writeToFile(String data) {
        try {
            FileOutputStream fos = openFileOutput("emergency-text.txt", Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
            Toast.makeText(MessageActivity.this, "Message Updated!", Toast.LENGTH_SHORT).show();
            currentMessage.setText("Current Message: " + data);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            Toast.makeText(MessageActivity.this, "Message Update Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    public String readFile(){
        String text = "";

        try{
            FileInputStream fis = openFileInput("emergency-text.txt");
            if(fis != null) {
                int size = fis.available();
                byte[] buffer = new byte[size];
                fis.read(buffer);
                fis.close();
                text = new String(buffer);
            }
            else{
                currentMessage.setText("No message set yet.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MessageActivity.this, "Error readinig file!", Toast.LENGTH_SHORT).show();
        }
        return text;
    }
}
