package com.example.ngrfitness;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.Nullable;
import android.Manifest;
public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    ImageView test;
    TextView textView;
    Button btnTracks,buttonLogout,btnPicture,btnGallery;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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

            test.setImageBitmap(bitmap);
            Toast.makeText(this, "Zrobiono zdjęcie", Toast.LENGTH_SHORT).show();
            //Trzeba zrobić zapis do firebasa i do galerii
        }
        else{
            Toast.makeText(this, "Nie dodano zdjęcia", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode,resultCode,data);
        }


    }

}