package com.example.ngrfitness;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final String TAG = "PicrureRecViewAdapter";

    private ArrayList<Picture> pictures = new ArrayList<>();
    private Context mContext;

    public MyAdapter(Context mContext) {

        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder: Call");
        Glide.with(mContext)
                .asBitmap()
                .load(pictures.get(position).getImageUlr())
                .into(holder.imgPicture);

    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }


    public void setPictures(ArrayList<Picture> pictures) {
        this.pictures = pictures;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout parent;
        private ImageView imgPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent=itemView.findViewById(R.id.parent);
            imgPicture=itemView.findViewById(R.id.imgPicture);
        }
    }




}
