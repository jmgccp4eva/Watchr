package com.iceberg.watchrrev1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.Objects;

public class ViewMovie extends AppCompatActivity {

    Button btnReturnToComingSoonMoviesList;
    ImageView ivPosterViewMovie,ivW2W1,ivW2W2,ivW2W3,ivWatchlistIconViewMovie,ivIgnoreTitleViewMovie;
    TextView tvRuntimeViewMovie, tvReleaseDateViewMovie,tvTitleViewMovie,tvNoStreamersViewMovie,
            tvSynopsisViewMovie,tvGenreViewMovie,tvWatchlistStatus,tvIgnoreListStatus,tvIggy2;
    private SharedPreferences sharedPreferences;
    boolean isOnWatchlist, isOnIgnoreList;
    int user_id,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_movie);
        String url2 = this.getApplicationContext().getString(R.string.my_url);
        Intent intent = getIntent();
        String lastPage = intent.getStringExtra("LASTPAGE");
        Log.i("PUT_DATA",lastPage);
        sharedPreferences = this.getSharedPreferences("com.iceberg.watchrrev1", Context.MODE_PRIVATE);
        user_id = sharedPreferences.getInt("user_id",-1);
        String user_name = sharedPreferences.getString("user_name","");
        String email = sharedPreferences.getString("email","");
        Boolean isRegistered = sharedPreferences.getBoolean("isRegistered",false);
        Boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);

        Intent i = getIntent();
        id = i.getIntExtra("ID",-1);
        String runtime = String.valueOf(i.getIntExtra("RUNTIME",0))+" minutes";
        String poster = i.getStringExtra("POSTER");
        String release_date = convertDate(i.getStringExtra("RELEASE_DATE"));
        String title = i.getStringExtra("TITLE");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        ivIgnoreTitleViewMovie = findViewById(R.id.ivIgnoreTitleViewMovie);
        ivWatchlistIconViewMovie = findViewById(R.id.ivWatchlistIconViewMovie);
        ivPosterViewMovie = findViewById(R.id.ivPosterViewMovie);
        tvRuntimeViewMovie = findViewById(R.id.tvRuntimeViewMovie);
        tvReleaseDateViewMovie = findViewById(R.id.tvReleaseDateViewMovie);
        tvTitleViewMovie = findViewById(R.id.tvTitleViewMovie);
        tvNoStreamersViewMovie = findViewById(R.id.tvNoStreamersViewMovie);
        tvSynopsisViewMovie = findViewById(R.id.tvSynopsisViewMovie);
        tvGenreViewMovie = findViewById(R.id.tvGenreViewMovie);
        ivW2W1 = findViewById(R.id.ivW2W1);
        ivW2W2 = findViewById(R.id.ivW2W2);
        ivW2W3 = findViewById(R.id.ivW2W3);
        tvWatchlistStatus = findViewById(R.id.tvWatchlistStatus);
        tvIgnoreListStatus = findViewById(R.id.tvIgnoreOrNotViewMovie);
        tvIggy2 = findViewById(R.id.tvMessageViewMovie);
        btnReturnToComingSoonMoviesList = findViewById(R.id.btnReturnToComingSoonMoviesList);

        tvNoStreamersViewMovie.setVisibility(View.INVISIBLE);

        tvRuntimeViewMovie.setText(runtime);
        tvReleaseDateViewMovie.setText(release_date);
        tvTitleViewMovie.setText(title);

        if(!Objects.equals(poster, "None")){
            String url = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2"+poster;
            Picasso.get().load(url).into(ivPosterViewMovie);
        }else{
            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_no_image);
            ivPosterViewMovie.setImageBitmap(bitmap);
        }

        String built = url2+"getMovieGenres.php?i="+id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, built,
                response ->{
                    String[] split = response.split("~~~");
                    response = split[0];
                    String json_encoded = split[1];
                    if(response.equals("Success")){
                        String genres = "";
                        try{
                            JSONArray jsonArray = new JSONArray(json_encoded);
                            int count=0;
                            for(int x=0;x<jsonArray.length();x++){
                                if(x>0 && x<3){
                                    genres = genres + ", ";
                                }
                                if(count<3) {
                                    genres = genres + jsonArray.getJSONObject(x).getString("name");
                                }
                                count+=1;
                            }
                        }catch (Exception e){
                            Toast.makeText(ViewMovie.this,""+e.toString(),Toast.LENGTH_LONG).show();
                        }
                        tvGenreViewMovie.setText(genres);
                        Log.i("PUT_DATA","Put genres "+genres);
                    }else if(response.equals("Nothing found")){
                        Toast.makeText(ViewMovie.this,"No results",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(ViewMovie.this,"Something went wrong",Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            Toast.makeText(ViewMovie.this,"That didn't work",Toast.LENGTH_LONG).show();
            Log.i("PUT_DATA",error.toString());
        });
        requestQueue.add(stringRequest);

        String built2 = url2+"getStreamingProviders.php?i="+id;
        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, built2,
                response2 ->{
                    Log.i("PUT_DATA",""+response2);
                    String[] split2 = response2.split("~~~");
                    response2 = split2[0];
                    if(!Objects.equals(response2, "No records found")) {
                        String json_encoded2 = split2[1];
                        if (response2.equals("Success")) {
                            tvNoStreamersViewMovie.setVisibility(View.GONE);
                            int count1 = 0;
                            try {
                                JSONArray jsonArray2 = new JSONArray(json_encoded2);
                                int margins;
                                if (jsonArray2.length() == 3) {
                                    margins = (width - 300) / 4;
                                    tvNoStreamersViewMovie.setVisibility(View.GONE);
                                    ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) ivW2W1.getLayoutParams();
                                    ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) ivW2W2.getLayoutParams();
                                    ConstraintLayout.LayoutParams params3 = (ConstraintLayout.LayoutParams) ivW2W3.getLayoutParams();
                                    params1.setMarginStart(margins);
                                    params2.setMarginStart(margins);
                                    params3.setMarginStart(margins);
                                    params3.setMarginEnd(margins);
                                    ivW2W1.setVisibility(View.VISIBLE);
                                    ivW2W2.setVisibility(View.VISIBLE);
                                    ivW2W3.setVisibility(View.VISIBLE);
                                    for (int x = 0; x < jsonArray2.length(); x++) {
                                        String this_url = "https://www.themoviedb.org/t/p/original" + jsonArray2.getJSONObject(x).getString("logo");
                                        if (count1 == 0) {
                                            Picasso.get().load(this_url).into(ivW2W1);
                                        } else if (count1 == 1) {
                                            Picasso.get().load(this_url).into(ivW2W2);
                                        } else if (count1 == 2) {
                                            Picasso.get().load(this_url).into(ivW2W3);
                                        }
                                        count1 += 1;
                                    }
                                } else if (jsonArray2.length() == 2) {
                                    margins = (width - 200) / 3;
                                    ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) ivW2W1.getLayoutParams();
                                    ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) ivW2W2.getLayoutParams();
                                    params1.setMarginStart(margins);
                                    params2.setMarginStart(margins);
                                    params2.setMarginEnd(margins);
                                    for (int x = 0; x < jsonArray2.length(); x++) {
                                        String this_url = "https://www.themoviedb.org/t/p/original" + jsonArray2.getJSONObject(x).getString("logo");
                                        if (count1 == 0) {
                                            Picasso.get().load(this_url).into(ivW2W1);
                                        } else if (count1 == 1) {
                                            Picasso.get().load(this_url).into(ivW2W2);
                                        }
                                        count1 += 1;
                                    }
                                    tvNoStreamersViewMovie.setVisibility(View.GONE);
                                    ivW2W1.setVisibility(View.VISIBLE);
                                    ivW2W2.setVisibility(View.VISIBLE);
                                    ivW2W2.setVisibility(View.GONE);
                                } else if (jsonArray2.length() == 1) {
                                    margins = (width - 100) / 2;
                                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ivW2W1.getLayoutParams();
                                    params.setMarginStart(margins);
                                    params.setMarginEnd(margins);
                                    for (int x = 0; x < jsonArray2.length(); x++) {
                                        String this_url = "https://www.themoviedb.org/t/p/original" + jsonArray2.getJSONObject(x).getString("logo");
                                        if (count1 == 0) {
                                            Picasso.get().load(this_url).into(ivW2W1);
                                        }
                                        count1 += 1;
                                    }
                                    tvNoStreamersViewMovie.setVisibility(View.GONE);
                                    ivW2W1.setVisibility(View.VISIBLE);
                                    ivW2W2.setVisibility(View.GONE);
                                    ivW2W2.setVisibility(View.GONE);
                                } else {
                                    tvNoStreamersViewMovie.setVisibility(View.VISIBLE);
                                    ivW2W1.setVisibility(View.GONE);
                                    ivW2W2.setVisibility(View.GONE);
                                    ivW2W3.setVisibility(View.GONE);
                                }
                                Log.i("PUT_DATA", "Put pictures");
                            } catch (Exception e) {
                                Toast.makeText(ViewMovie.this, "" + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        } else if (response2.equals("No records found")) {
                            tvNoStreamersViewMovie.setVisibility(View.VISIBLE);
                            ivW2W3.setVisibility(View.GONE);
                            ivW2W2.setVisibility(View.GONE);
                            ivW2W1.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(ViewMovie.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        ivW2W1.setVisibility(View.GONE);
                        ivW2W2.setVisibility(View.GONE);
                        ivW2W3.setVisibility(View.GONE);
                        tvNoStreamersViewMovie.setVisibility(View.VISIBLE);
                    }
                }, error -> {
            Toast.makeText(ViewMovie.this,"That didn't work",Toast.LENGTH_LONG).show();
            Log.i("PUT_DATA",error.toString());
        });
        requestQueue2.add(stringRequest2);

        String built3 = url2+"getSynopsis.php?i="+id;
        RequestQueue requestQueue3 = Volley.newRequestQueue(this);
        StringRequest stringRequest3 = new StringRequest(Request.Method.GET, built3,
                response3 ->{
                    String[] split3 = response3.split("~~~");
                    response3 = split3[0];
                    String json_encoded3 = split3[1];
                    if(response3.equals("Success")){
                        try{
                            JSONArray jsonArray3 = new JSONArray(json_encoded3);
                            Log.i("PUT_DATA",""+jsonArray3);
                            String overview = jsonArray3.getJSONObject(0).getString("overview");
                            tvSynopsisViewMovie.setText(overview);
                        }catch(Exception e){
                            Toast.makeText(ViewMovie.this,""+e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }else if(response3.equals("No records found")){
                        tvSynopsisViewMovie.setText("No synopsis available");
                    }else{
                        Toast.makeText(ViewMovie.this,"Something went wrong",Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            Toast.makeText(ViewMovie.this,"That didn't work",Toast.LENGTH_LONG).show();
            Log.i("PUT_DATA",error.toString());
        });
        requestQueue3.add(stringRequest3);

        String built4 = url2+"getWatchlist.php?i="+id+"&t=m&u="+user_id;
        Log.i("PUT_DATA",built4);
        RequestQueue requestQueue4 = Volley.newRequestQueue(this);
        StringRequest stringRequest4 = new StringRequest(Request.Method.GET, built4,
                response4 ->{
                    if(response4.equals("No records found")){
                        ivWatchlistIconViewMovie.setBackgroundResource(R.drawable.ic_add);
                        tvWatchlistStatus.setText(R.string.currently_not_in_your_watchlist_click_to_add_it);
                        isOnWatchlist=false;
                    }else{
                        ivWatchlistIconViewMovie.setBackgroundResource(R.drawable.ic_remove);
                        tvWatchlistStatus.setText(R.string.this_title_is_on_your_watchlist_click_sign_to_remove_it);
                        isOnWatchlist=true;
                    }
                }, error -> {
            Toast.makeText(ViewMovie.this,"That didn't work "+error.toString(),Toast.LENGTH_LONG).show();
            Log.i("PUT_DATA",error.toString());
        });
        requestQueue4.add(stringRequest4);

        String built5 = url2+"getIgnoreList.php?i="+id+"&t=m&u="+user_id;
        Log.i("PUT_DATA",built5);
        RequestQueue requestQueue5 = Volley.newRequestQueue(this);
        StringRequest stringRequest5 = new StringRequest(Request.Method.GET, built5,
                response5 -> {
                    if (response5.equals("No records found")) {
                        ivIgnoreTitleViewMovie.setBackgroundResource(R.drawable.ic_ignore);
                        tvIgnoreListStatus.setText(R.string.ignore_this_title);
                        tvIggy2.setVisibility(View.VISIBLE);
                        isOnIgnoreList = false;
                    } else {
                        ivIgnoreTitleViewMovie.setBackgroundResource(R.drawable.ic_unignore);
                        tvIgnoreListStatus.setText(R.string.remove_from_ignore);
                        tvIggy2.setVisibility(View.GONE);
                        isOnWatchlist = true;
                    }
                }, error -> {
            Toast.makeText(ViewMovie.this,"That didn't work "+error.toString(),Toast.LENGTH_LONG).show();
            Log.i("PUT_DATA",error.toString());
        });
        requestQueue5.add(stringRequest5);

        btnReturnToComingSoonMoviesList.setOnClickListener(v -> {
            Intent intent12 =new Intent();
            setResult(1203, intent12);
            finish();
        });

        ivWatchlistIconViewMovie.setOnClickListener(v -> {
            if(!isOnWatchlist){
                String builtIV1 = url2+"a2wl.php?i="+id+"&u="+user_id+"&t=m";
                RequestQueue requestQueueIV1 = Volley.newRequestQueue(this);
                StringRequest stringRequestIV1 = new StringRequest(Request.Method.GET, builtIV1,
                        responseIV1 ->{
                            if(responseIV1.equals("Success")){
                                ivWatchlistIconViewMovie.setBackgroundResource(R.drawable.ic_remove);
                                tvWatchlistStatus.setText(R.string.this_title_is_on_your_watchlist_click_sign_to_remove_it);
                                isOnWatchlist=true;
                            }else{
                                Toast.makeText(getApplicationContext(),"Failed to add to your watchlist",Toast.LENGTH_LONG).show();
                            }
                        }, error -> {
                    Toast.makeText(ViewMovie.this,"That didn't work",Toast.LENGTH_LONG).show();
                    Log.i("PUT_DATA",error.toString());
                });
                requestQueueIV1.add(stringRequestIV1);
            }else{
                String builtIV2 = url2+"rfwl.php?i="+id+"&u="+user_id+"&t=m";
                RequestQueue requestQueueIV2 = Volley.newRequestQueue(this);
                StringRequest stringRequestIV2 = new StringRequest(Request.Method.GET, builtIV2,
                        responseIV2 ->{
                            if(responseIV2.equals("Success")){
                                ivWatchlistIconViewMovie.setBackgroundResource(R.drawable.ic_add);
                                tvWatchlistStatus.setText(R.string.currently_not_in_your_watchlist_click_to_add_it);
                                isOnWatchlist=false;
                            }else{
                                Toast.makeText(getApplicationContext(),"Failed to remove from your watchlist",Toast.LENGTH_LONG).show();
                            }
                        }, error -> {
                    Toast.makeText(ViewMovie.this,"That didn't work",Toast.LENGTH_LONG).show();
                    Log.i("PUT_DATA",error.toString());
                });
                requestQueueIV2.add(stringRequestIV2);
            }
        });

        ivIgnoreTitleViewMovie.setOnClickListener(v -> {
            if(!isOnIgnoreList){
                String builtIL1 = url2+"a2il.php?i="+id+"&u="+user_id+"&t=m";
                RequestQueue requestQueueIL1 = Volley.newRequestQueue(this);
                StringRequest stringRequestIL1 = new StringRequest(Request.Method.GET, builtIL1,
                        responseIL1 ->{
                            if(responseIL1.equals("Success")){
                                ivIgnoreTitleViewMovie.setBackgroundResource(R.drawable.ic_unignore);
                                tvIggy2.setVisibility(View.GONE);
                                tvIgnoreListStatus.setText(R.string.remove_from_ignore);
                                isOnIgnoreList = true;
                            }else{
                                Toast.makeText(getApplicationContext(),"Failed to add to ignored titles",Toast.LENGTH_LONG).show();
                            }
                        }, error -> {
                    Toast.makeText(ViewMovie.this,"That didn't work",Toast.LENGTH_LONG).show();
                    Log.i("PUT_DATA",error.toString());
                });
                requestQueueIL1.add(stringRequestIL1);
            }else{
                String builtIL2 = url2+"rfil.php?i="+id+"&u="+user_id+"&t=m";
                RequestQueue requestQueueIL2 = Volley.newRequestQueue(this);
                StringRequest stringRequestIL2 = new StringRequest(Request.Method.GET, builtIL2,
                        responseIL2 -> {
                            if(responseIL2.equals("Success")){
                                ivIgnoreTitleViewMovie.setBackgroundResource(R.drawable.ic_ignore);
                                tvIggy2.setVisibility(View.VISIBLE);
                                tvIgnoreListStatus.setText(R.string.ignore_this_title);
                                isOnIgnoreList = false;
                            }else{
                                Toast.makeText(getApplicationContext(),"Failed to remove from ignored titles",Toast.LENGTH_LONG).show();
                            }
                        }, error -> {
                    Toast.makeText(ViewMovie.this,"That didn't work",Toast.LENGTH_LONG).show();
                    Log.i("PUT_DATA",error.toString());
                });
                requestQueueIL2.add(stringRequestIL2);
            }
        });
    }

    private String convertDate(String release_date) {
        String year = release_date.substring(0,4);
        String month = release_date.substring(5,7);
        String day = release_date.substring(8,10);
        String[] months = {"January","February","March","April","May","June","July","August","Septemeber","October","November","December"};
        month = months[Integer.parseInt(month)-1];
        return month+" "+day+", "+year;
    }
}