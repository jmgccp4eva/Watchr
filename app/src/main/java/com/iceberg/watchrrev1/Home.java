package com.iceberg.watchrrev1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class Home extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = this.getSharedPreferences("com.iceberg.watchrrev1", Context.MODE_PRIVATE);
        int user_id = sharedPreferences.getInt("user_id",-1);
        String user_name = sharedPreferences.getString("user_name","");
        String email = sharedPreferences.getString("email","");
        Boolean isRegistered = sharedPreferences.getBoolean("isRegistered",false);
        Boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);
        Log.i("PUT_DATA","On Home, User ID: "+user_id);
        if(isLoggedIn && isRegistered){
            Log.i("PUT_DATA","Logged in and Registered");
        }
        Log.i("PUT_DATA",user_name+" "+email);
    }
}