package com.example.ngrfitness;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gallery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.pictures);
        List<GalleryItem> pictures = new ArrayList<GalleryItem>();
        //sztuczne dodanie zdjęć
        pictures.add(new GalleryItem(BitmapFactory.decodeResource(getResources(),R.drawable.logo)));
        pictures.add(new GalleryItem(BitmapFactory.decodeResource(getResources(),R.drawable.logo)));
        pictures.add(new GalleryItem(BitmapFactory.decodeResource(getResources(),R.drawable.logo)));
        pictures.add(new GalleryItem(BitmapFactory.decodeResource(getResources(),R.drawable.logo)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(),pictures));

    }
}