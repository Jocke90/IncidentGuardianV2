package com.example.jocke.incidentguardianv2.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.example.jocke.incidentguardianv2.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=incidentguardian;AccountKey=X9Cygj3SSBlAz5zrHbCEivfSb/lh3PoDwKmFXaNB9ZH+aD4REG0OwnmHUPYGeOeDPQcPDIB0wkxoyFGCsdh4Gw==;EndpointSuffix=core.windows.net";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        final WebView webView = (WebView) findViewById(R.id.loginScreen);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(webView, url);
                Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();

                if(url.equals("https://projectincidentguardian.azurewebsites.net/Default")){
                    String[] cookieData = getCookie("https://projectincidentguardian.azurewebsites.net/Default", "LoginStatus");
                    test(cookieData[1]);

                    if(cookieData[0].equals("Succeed")){
                        startActivity(new Intent(MainActivity.this,MenuActivity.class));
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webView.loadUrl("https://projectincidentguardian.azurewebsites.net/Account/Login");
    }

    private void test(String username){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Username", username);
        editor.commit();


        SharedPreferences thisPref = this.getPreferences(Context.MODE_PRIVATE);
        String result = sharedPref.getString("Username", "Nada");
        Log.d("READ PREF", result);
    }

    public String[] getCookie(String siteName,String CookieName){
        String CookieValue = null;
        String[] returnString = new String[2];
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        String[] temp=cookies.split(";");

        for (String ar1 : temp ){

            if(ar1.contains(CookieName)){
                String[] temp1 = ar1.split("=");
                String as = temp1[2] + "&" + temp1[3];
                String[] temp3 = as.split("&");

                for(int i = 0; i < temp3.length; i++){

                    Log.d("COOKIELOOP", temp3[i]);
                }

                returnString[0] = temp3[0];
                returnString[1] = temp3[2];
                //CookieValue = temp1[2];
                Log.d("COOKIE", returnString[0]);
                Log.d("COOKIE", returnString[1]);
                break;
            }
        }
        return returnString;
    }


}
