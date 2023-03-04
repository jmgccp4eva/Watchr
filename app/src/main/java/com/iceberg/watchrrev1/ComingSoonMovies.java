package com.iceberg.watchrrev1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ComingSoonMovies extends AppCompatActivity implements ComingSoonMoviesInterface{

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==1203){
                        Intent intent = result.getData();
                        if(intent!=null){
                            Intent i = new Intent(ComingSoonMovies.this, ComingSoonMovies.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }
            }
    );


    private SharedPreferences sharedPreferences;

    MCSData[] mcsData;
    TextView tvDate;
    RecyclerView recyclerView;
    ComingSoonMoviesAdapter comingSoonMoviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon_movies);
        recyclerView = findViewById(R.id.customRecyclerViewMCS);


        String url = this.getApplicationContext().getString(R.string.my_url);

        sharedPreferences = this.getSharedPreferences("com.iceberg.watchrrev1", Context.MODE_PRIVATE);
        int user_id = sharedPreferences.getInt("user_id",-1);

        Date cDate = new Date();
        @SuppressLint("SimpleDateFormat") String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        String endDate = getEndMonthAndDay(fDate);
        String built = url+"comingsoon.php?d="+fDate+"&e="+endDate+"&u="+user_id+"&t=m";
        Log.i("PUT_DATA",built);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, built,
                response ->{
                    String[] split = response.split("~~~");
                    response = split[0];
                    String json_encoded = split[1];
                    String my_substring = json_encoded.substring(json_encoded.lastIndexOf("release_date")+"release_date".length()+3).substring(0,10);
                    if(response.equals("Success")){
                        int year_on = Integer.parseInt(fDate.substring(0,4));
                        int month_on = Integer.parseInt(fDate.substring(5,7));
                        Log.i("PUT_DATA",""+year_on);
                        Log.i("PUT_DATA",""+month_on);
                        try {
                            JSONArray jsonArray = new JSONArray(json_encoded);
                            mcsData = new MCSData[jsonArray.length()];
                            for (int x = 0; x < jsonArray.length(); x++) {
                                MCSData this_item = new MCSData(
                                        jsonArray.getJSONObject(x).getInt("id"),
                                        jsonArray.getJSONObject(x).getString("title"),
                                        jsonArray.getJSONObject(x).getString("apr_date"),
                                        jsonArray.getJSONObject(x).getString("poster"),
                                        jsonArray.getJSONObject(x).getInt("runtime"),
                                        jsonArray.getJSONObject(x).getBoolean("watchlist")
                                );
                                mcsData[x] = this_item;
                            }
                            comingSoonMoviesAdapter = new ComingSoonMoviesAdapter(ComingSoonMovies.this,mcsData,this);
                            recyclerView.setHasFixedSize(true);
                            LinearLayoutManager layoutManager =new LinearLayoutManager(ComingSoonMovies.this);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(comingSoonMoviesAdapter);
                            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                    layoutManager.getOrientation());
                            recyclerView.addItemDecoration(dividerItemDecoration);

                        }catch (Exception e){
                            Toast.makeText(ComingSoonMovies.this,""+e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }else if(response.equals("Nothing found")){
                        Toast.makeText(ComingSoonMovies.this,"No results",Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(ComingSoonMovies.this,"Something went wrong",Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            Toast.makeText(ComingSoonMovies.this,"An error has occurred",Toast.LENGTH_LONG).show();
            Log.i("PUT_DATA",error.toString());
        });
        requestQueue.add(stringRequest);
    }

    private String getEndMonthAndDay(String fDate) {
        int this_month = Integer.parseInt(fDate.substring(5,7));
        int this_year = Integer.parseInt(fDate.substring(0,4));
        String endDate = null;
        switch(this_month){
            case 1:
                if(this_year%400==0){
                    endDate = String.valueOf(this_year)+"-0"+String.valueOf(this_month+1)+"-29";
                }else if(this_year%100==0){
                    endDate = String.valueOf(this_year)+"-0"+String.valueOf(this_month+1)+"-28";
                }else if(this_year%4==0){
                    endDate = String.valueOf(this_year)+"-0"+String.valueOf(this_month+1)+"-29";
                }else{
                    endDate = String.valueOf(this_year)+"-0"+String.valueOf(this_month+1)+"-28";
                }
                break;
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                if(this_month<9){
                    endDate = String.valueOf(this_year)+"-0"+String.valueOf(this_month+1)+"-31";
                }else{
                    endDate = String.valueOf(this_year)+"-"+String.valueOf(this_month+1)+"-31";
                }
                break;
            case 3:
            case 5:
            case 8:
            case 10:
                if(this_month<9){
                    endDate = String.valueOf(this_year)+"-0"+String.valueOf(this_month+1)+"-30";
                }else{
                    endDate = String.valueOf(this_year)+"-"+String.valueOf(this_month+1)+"-30";
                }
                break;
            case 12:
                endDate = String.valueOf(this_year+1)+"-01-31";
        }
        return endDate;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(ComingSoonMovies.this, ViewMovie.class);
        intent.putExtra("ID",mcsData[position].getId());
        intent.putExtra("TITLE",mcsData[position].getTitle());
        intent.putExtra("RUNTIME",mcsData[position].getRuntimes());
        intent.putExtra("RELEASE_DATE",mcsData[position].getRelease_date());
        intent.putExtra("POSTER",mcsData[position].getPosters());
        intent.putExtra("LASTPAGE","ComingSoonMovies");
        activityResultLauncher.launch(intent);
    }
}