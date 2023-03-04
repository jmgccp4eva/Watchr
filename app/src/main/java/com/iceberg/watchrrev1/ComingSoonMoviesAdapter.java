package com.iceberg.watchrrev1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.Objects;

public class ComingSoonMoviesAdapter extends RecyclerView.Adapter<ComingSoonMoviesAdapter.ViewHolder> {

    private ComingSoonMoviesInterface comingSoonMoviesInterface;
    Context context;
    public MCSData[] listData;

    public ComingSoonMoviesAdapter(Context context,MCSData[] listData,ComingSoonMoviesInterface comingSoonMoviesInterface){
        this.context=context;
        this.listData = listData;
        this.comingSoonMoviesInterface=comingSoonMoviesInterface;
    }
    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public ComingSoonMoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_item_view_mcs,parent,false);
        return new ViewHolder(view,comingSoonMoviesInterface);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ComingSoonMoviesAdapter.ViewHolder holder, int position) {
        holder.tvMCSTitle.setText(listData[position].getTitle());
        if(listData[position].getWatchlist()){
            holder.itemView.setBackgroundColor(Color.parseColor("#FFD700"));
        }else{
            holder.itemView.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }
        if(!Objects.equals(listData[position].getPosters(), "None")){
            String url = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2"+listData[position].getPosters();
            Picasso.get().load(url).into(holder.ivMCSPoster);
        }else{
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_no_image);
            holder.ivMCSPoster.setImageBitmap(bitmap);
        }
        String the_date = convertDate(listData[position].getRelease_date());
        holder.tvReleaseDate.setText(the_date);
    }

    private String convertDate(String release_date) {
        String monthName = null;
        int monthNum = Integer.parseInt(release_date.substring(5,7));
        Log.i("PUT_DATA","Here "+monthNum);
        String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        monthName = months[monthNum-1];
        String day = release_date.substring(8,10);
        return monthName+" "+day;
    }

    @Override
    public int getItemCount() {
        return listData.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMCSTitle,tvReleaseDate;
        ImageView ivMCSPoster;
        public ViewHolder(@NonNull View itemView, ComingSoonMoviesInterface comingSoonMoviesInterface) {
            super(itemView);
            tvMCSTitle = itemView.findViewById(R.id.tvTitleSearchView);
            tvReleaseDate = itemView.findViewById(R.id.tvReleaseDate);
            ivMCSPoster = itemView.findViewById(R.id.ivMCSPoster);

            itemView.setOnClickListener(v -> {
                if(comingSoonMoviesInterface!=null){
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        comingSoonMoviesInterface.onItemClick(position);
                    }
                }
            });
        }
    }
}
