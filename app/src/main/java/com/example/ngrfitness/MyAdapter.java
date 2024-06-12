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

/**
 * Adapter do wyświetlania listy obrazów w RecyclerView.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final String TAG = "PicrureRecViewAdapter";

    private ArrayList<Picture> pictures = new ArrayList<>(); // Lista obrazów do wyświetlenia
    private Context mContext; // Kontekst aplikacji

    /**
     * Konstruktor adaptera.
     *
     * @param mContext Kontekst aplikacji.
     */
    public MyAdapter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Tworzy nowy ViewHolder, gdy nie ma istniejących, które mogą być ponownie użyte.
     *
     * @param parent   Rodzic ViewHoldera.
     * @param viewType Typ widoku.
     * @return Nowo utworzony ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_view, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Łączy dane z danym ViewHolderem.
     *
     * @param holder   ViewHolder, który ma być związany.
     * @param position Pozycja elementu w adapterze.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Call");
        Glide.with(mContext)
                .asBitmap()
                .load(pictures.get(position).getImageUlr())
                .into(holder.imgPicture);
    }

    /**
     * Zwraca liczbę elementów w adapterze.
     *
     * @return Liczba elementów w adapterze.
     */
    @Override
    public int getItemCount() {
        return pictures.size();
    }

    /**
     * Ustawia nową listę obrazów i powiadamia adapter o zmianach.
     *
     * @param pictures Lista obiektów Picture do wyświetlenia.
     */
    public void setPictures(ArrayList<Picture> pictures) {
        this.pictures = pictures;
        notifyDataSetChanged();
    }

    /**
     * Klasa ViewHolder do przechowywania i zarządzania widokami elementów listy.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout parent; // Rodzic widoku
        private ImageView imgPicture; // Widok obrazu

        /**
         * Konstruktor ViewHoldera.
         *
         * @param itemView Widok elementu listy.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            imgPicture = itemView.findViewById(R.id.imgPicture);
        }
    }
}
