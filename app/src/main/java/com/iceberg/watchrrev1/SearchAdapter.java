package com.iceberg.watchrrev1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private final SearchInterface searchInterface;
    Context context;
    public MCSData[] listData;

    public SearchAdapter(Context context,MCSData[] listData,SearchInterface searchInterface){
        this.context = context;
        this.listData = listData;
        this.searchInterface = searchInterface;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_item_results_from_search,parent,false);
        return new ViewHolder(view,searchInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        holder.tvTitleSearchView.setText(listData[position].getTitle());
//        if(listData[position].getWatchlist()){
//            holder.itemView.setBackgroundColor(Color.parseColor("FFD700"));
//        }else{
//            holder.itemView.setBackgroundColor(Color.parseColor("D3D3D3"));
//        }
    }

    @Override
    public int getItemCount() {
        return listData.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitleSearchView;
        public ViewHolder(@NonNull View itemView,SearchInterface searchInterface) {
            super(itemView);
            tvTitleSearchView = itemView.findViewById(R.id.tvTitleSearchView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(searchInterface!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            searchInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
