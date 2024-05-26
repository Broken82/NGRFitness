package com.example.ngrfitness;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.Nullable;
import android.Manifest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    ImageView test;
    TextView textView;
    Button btnTracks,buttonLogout,btnPicture,btnGallery;
    FirebaseUser currentUser;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(MainActivity.this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        buttonLogout = findViewById(R.id.bnt_logout);
        btnTracks = findViewById(R.id.info);
        btnPicture = findViewById(R.id.picture_btn);
        textView = findViewById(R.id.user_details);
        currentUser = mAuth.getCurrentUser();
        test= findViewById(R.id.logo_pick);
        btnGallery = findViewById(R.id.gallery);
        storageReference = FirebaseStorage.getInstance().getReference();



        //request for camera permission
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            },100);
        }

        if(currentUser == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        else {
            textView.setText(currentUser.getEmail());
        }

        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        btnTracks.setOnClickListener(view -> {
            Toast.makeText(this, "Track Button", Toast.LENGTH_SHORT).show();
        });
        btnPicture.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,100);
        });

        btnGallery.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Gallery.class);
            startActivity(intent);
            finish();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 100&& resultCode==RESULT_OK){
            Bitmap bitmap= (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,"val",null);
            Uri uri=Uri.parse(path);
            StorageReference ref = storageReference.child(currentUser.getEmail()+"/" + UUID.randomUUID().toString());
            ref.putFile(uri);
            //StorageReference reference = storageRef.child("images/"+ UUID.randomUUID().toString());
            //reference.putFile(uri);


            Toast.makeText(this, "Zrobiono zdjęcie", Toast.LENGTH_SHORT).show();
            //Trzeba zrobić zapis do firebasa i do galerii
        }
        else{
            Toast.makeText(this, "Nie dodano zdjęcia", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode,resultCode,data);
        }


    }



}