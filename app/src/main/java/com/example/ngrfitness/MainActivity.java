package com.example.ngrfitness;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    private final AtomicBoolean initialLayoutComplete = new AtomicBoolean(false);
    private AdView mAdView;
    private AdView adView;
    private FrameLayout adContainerView;
    FirebaseAuth mAuth;
    ImageView test;
    TextView textView;
    Button btnTracks,buttonLogout,btnPicture,btnGallery, btnProfile, btnSteps;
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

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }

        });

        mAuth = FirebaseAuth.getInstance();
        buttonLogout = findViewById(R.id.bnt_logout);
        btnTracks = findViewById(R.id.info);
        btnPicture = findViewById(R.id.picture_btn);
        btnProfile = findViewById(R.id.profile_btn);
        btnSteps = findViewById(R.id.step_btn);
        currentUser = mAuth.getCurrentUser();
        test= findViewById(R.id.logo_pick);
        btnGallery = findViewById(R.id.gallery);
        storageReference = FirebaseStorage.getInstance().getReference();
        adContainerView = findViewById(R.id.ad_view_container);


        adContainerView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        () -> {
                            if (!initialLayoutComplete.getAndSet(true)) {
                                AdView adView = new AdView(this);
                                adView.setAdSize(getAdSize());
                                adView.setAdUnitId("ca-app-pub-3940256099942544/9214589741");

                                adContainerView.removeAllViews();
                                adContainerView.addView(adView);

                                AdRequest adRequest = new AdRequest.Builder().build();
                                adView.loadAd(adRequest);
                            }
                        });





        //request for camera permission
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            },100);
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

        btnProfile.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Profile.class);
            startActivity(intent);
            finish();
        });

        btnSteps.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Steps.class);
            startActivity(intent);
            finish();
        });

    }

    private AdSize getAdSize() {

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();


        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
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