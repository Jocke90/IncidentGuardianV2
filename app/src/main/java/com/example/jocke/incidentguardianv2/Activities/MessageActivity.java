package com.example.jocke.incidentguardianv2.Activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jocke.incidentguardianv2.R;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

        currentMessage.setText("Current message: " + readFileMessage());

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

    //This method is to write to a txt file on external storage with what message
    //the user would like to be sent to it's contacts when an emergency occurs
    public void writeToFile(String data) {

        String state;
        state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/IncidentGuardianFolder");

            if (!dir.exists()) {
                dir.mkdir();
            }

            File file = new File(dir, "EmergencyMessage.txt");

            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data.getBytes());
                fos.close();
                Toast.makeText(MessageActivity.this, "Message updated!", Toast.LENGTH_SHORT).show();
                currentMessage.setText("Current message: " + data);
                message.setText("");

            } catch (FileNotFoundException e) {

            } catch (IOException ie) {
                ie.printStackTrace();
                Toast.makeText(MessageActivity.this, "Message Update Failed!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "External Storage not found!", Toast.LENGTH_SHORT).show();
        }

    }
    //This method is to read the txt file with the message to it can be presented in a textview
    //so the user can se what current message is set
    public String readFileMessage(){
        String text = "";
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/IncidentGuardianFolder");
        File file = new File(dir, "EmergencyMessage.txt");
        StringBuilder sb = new StringBuilder();

        try{
            FileInputStream fis = new FileInputStream(file);
            if(fis != null) {
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader buffer = new BufferedReader(isr);


                while ((text = buffer.readLine()) != null) {
                    sb.append(text);
                }
            }

        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(MessageActivity.this, "Error reading file!", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }


}
