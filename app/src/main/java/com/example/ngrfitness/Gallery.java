package com.example.ngrfitness;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class Gallery extends AppCompatActivity {


    private RecyclerView pictureRecView;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private MyAdapter adapter;
    private Button backBtn;
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
        backBtn=findViewById(R.id.back_btn);
        adapter = new MyAdapter(this);
        pictureRecView = findViewById(R.id.picturesRecycle);
        pictureRecView.setAdapter(adapter);
        pictureRecView.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();



        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child(currentUser.getEmail());

        imagesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    ArrayList<Picture>pictures =new ArrayList<>();
                    //I don't know why but it must be here
                    pictures.add(new Picture(1,"https://i1.sndcdn.com/avatars-5YhOoeqkl8R1QTtE-VPEy0Q-t1080x1080.jpg"));
                    for (StorageReference item : listResult.getItems()) {

                        item.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            String imageUrl = downloadUrl.toString();
                            pictures.add(new Picture(1,imageUrl));



                        });
                    }
                    adapter.setPictures(pictures);
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(this, "Nie działa ", Toast.LENGTH_SHORT).show();
                });

        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

    }
}