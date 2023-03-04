package com.iceberg.watchrrev1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class SearchForMovie extends AppCompatActivity implements SearchInterface{

    TextView tvErrorSearchResults;
    EditText etSearchMovie;
    ImageButton ibSearchMovie;
    RecyclerView rvSearchMovieResults;
    private SharedPreferences sharedPreferences;
    private MCSData[] mcsData;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==1203){
                        Intent intent = result.getData();
                        if(intent!=null){
                            Intent i = new Intent(SearchForMovie.this, SearchForMovie.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_movie);
        String url = this.getApplicationContext().getString(R.string.my_url);

        sharedPreferences = this.getSharedPreferences("com.iceberg.watchrrev1", Context.MODE_PRIVATE);
        int user_id = sharedPreferences.getInt("user_id",-1);

        tvErrorSearchResults = findViewById(R.id.tvErrorSearchResults);
        etSearchMovie = findViewById(R.id.etSearchMovie);
        ibSearchMovie = findViewById(R.id.ibSearchMovie);
        rvSearchMovieResults = findViewById(R.id.rvSearchMovieResults);

        ibSearchMovie.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            String my_title = etSearchMovie.getText().toString().trim();
            if(my_title.length()>2){
                String built = url+"searchLike.php?t=m&n="+my_title+"&u="+user_id;
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, built,
                        response ->{
                            String[] split = response.split("~~~");
                            response = split[0];
                            if(response.equals("Success")){
                                String json_encoded = split[1];
                                try{
                                    JSONArray jsonArray = new JSONArray(json_encoded);
                                    mcsData = new MCSData[jsonArray.length()];
                                    for (int x = 0; x < jsonArray.length(); x++){
                                        MCSData this_item = new MCSData(
                                                jsonArray.getJSONObject(x).getInt("id"),
                                                jsonArray.getJSONObject(x).getString("title"),
                                                jsonArray.getJSONObject(x).getString("release_date"),
                                                jsonArray.getJSONObject(x).getString("poster"),
                                                jsonArray.getJSONObject(x).getInt("runtime"),
                                                jsonArray.getJSONObject(x).getBoolean("watchlist")
                                        );
                                        mcsData[x] = this_item;
                                    }
                                    SearchAdapter searchAdapter = new SearchAdapter(this,mcsData,this);
                                    rvSearchMovieResults.setHasFixedSize(true);
                                    LinearLayoutManager layoutManager =new LinearLayoutManager(SearchForMovie.this);
                                    rvSearchMovieResults.setLayoutManager(layoutManager);
                                    rvSearchMovieResults.setAdapter(searchAdapter);
                                }catch (Exception e){
                                    Toast.makeText(SearchForMovie.this,""+e.toString(),Toast.LENGTH_LONG).show();
                                }
                            }else if(response.equals("Nothing found")){
                                tvErrorSearchResults.setText(R.string.no_results);
                                tvErrorSearchResults.setVisibility(View.VISIBLE);
                                rvSearchMovieResults.setVisibility(View.GONE);
                            }else{
                                Toast.makeText(SearchForMovie.this,"Something went wrong",Toast.LENGTH_LONG).show();
                            }
                        }, error -> {
                    Toast.makeText(SearchForMovie.this,"An error has occurred",Toast.LENGTH_LONG).show();
                    Log.i("PUT_DATA",error.toString());
                });
                requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(SearchForMovie.this, ViewMovie.class);
        intent.putExtra("ID",mcsData[position].getId());
        intent.putExtra("TITLE",mcsData[position].getTitle());
        intent.putExtra("RUNTIME",mcsData[position].getRuntimes());
        intent.putExtra("RELEASE_DATE",mcsData[position].getRelease_date());
        intent.putExtra("POSTER",mcsData[position].getPosters());
        intent.putExtra("LASTPAGE","SearchForMovies");
        activityResultLauncher.launch(intent);
    }
}