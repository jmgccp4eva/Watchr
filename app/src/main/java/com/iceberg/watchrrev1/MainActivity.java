package com.iceberg.watchrrev1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getSharedPreferences("com.iceberg.watchrrev1", Context.MODE_PRIVATE);
        String user_name = sharedPreferences.getString("user_name","");
        int user_id = sharedPreferences.getInt("user_id",-1);
        String email = sharedPreferences.getString("email","");
        Log.i("PUT_DATA","ID: "+user_id);
        Log.i("PUT_DATA",user_name);
        Intent i;
        if(user_id==-1){
            i = new Intent(MainActivity.this,LogIn.class);
        }else{
            i = new Intent(MainActivity.this,Home.class);
        }
        startActivity(i);
        finish();
    }
}