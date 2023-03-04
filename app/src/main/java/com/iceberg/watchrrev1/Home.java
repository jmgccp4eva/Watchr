package com.iceberg.watchrrev1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class Home extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    Button btnNewEpisodes, btnNewMovies,btnYourWatchlist,btnSearchMovie,btnSearchSeries,btnComingSoonMovies;

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

        btnYourWatchlist = findViewById(R.id.btnYourWatchlist);
        btnSearchMovie = findViewById(R.id.btnSearchMovie);
        btnComingSoonMovies = findViewById(R.id.btnComingSoonMovies);
        btnNewEpisodes = findViewById(R.id.btnNewEpisodes);
        btnNewMovies = findViewById(R.id.btnNewMovies);
        btnSearchSeries = findViewById(R.id.btnSearchSeries);

        btnComingSoonMovies.setOnClickListener(v -> {
            Intent i = new Intent(Home.this, ComingSoonMovies.class);
            startActivity(i);
        });
        btnSearchMovie.setOnClickListener(v -> {
            Intent i = new Intent(Home.this,SearchForMovie.class);
            startActivity(i);
        });
    }
}