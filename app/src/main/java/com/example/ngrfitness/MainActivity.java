package com.example.ngrfitness;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatDelegate;
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

public class MainActivity extends AppCompat {
    private final AtomicBoolean initialLayoutComplete = new AtomicBoolean(false);
    private AdView mAdView;
    private AdView adView;
    private FrameLayout adContainerView;
    private UiModeManager uiModeManager;
    private boolean isNightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseAuth mAuth;
    ImageView test;
    TextView textView;
    Button btnTheme,buttonLogout,btnPicture,btnGallery, btnProfile, btnSteps,btnMap,btnPL,btnENG;
    FirebaseUser currentUser;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri image;



    /**
     * Metoda wywoływana na początku cyklu życia aktywności.
     * Sprawdza, czy użytkownik jest zalogowany.
     */
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Jeśli użytkownik jest zalogowany, przejdź do głównej aktywności
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Metoda wywoływana podczas tworzenia aktywności.
     *
     * @param savedInstanceState Zapisany stan instancji.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Włącz wyświetlanie krawędzi do krawędzi
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        // Inicjalizacja Firebase
        FirebaseApp.initializeApp(MainActivity.this);

        // Ustaw wcięcia okna dla wyświetlania krawędzi do krawędzi
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicjalizacja reklam mobilnych
        MobileAds.initialize(this, initializationStatus -> {});

        // Utwórz kanał powiadomień
        createNotificationChannel();

        // Inicjalizacja elementów interfejsu użytkownika
        mAuth = FirebaseAuth.getInstance();
        buttonLogout = findViewById(R.id.bnt_logout);
        btnTheme = findViewById(R.id.theme);
        btnPicture = findViewById(R.id.picture_btn);
        btnProfile = findViewById(R.id.profile_btn);
        btnSteps = findViewById(R.id.step_btn);
        btnMap = findViewById(R.id.map_btn);
        currentUser = mAuth.getCurrentUser();
        test = findViewById(R.id.logo_pick);
        btnGallery = findViewById(R.id.gallery);
        storageReference = FirebaseStorage.getInstance().getReference();
        adContainerView = findViewById(R.id.ad_view_container);
        btnENG = findViewById(R.id.eng_btn);
        btnPL = findViewById(R.id.pl_btn);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isNightMode = sharedPreferences.getBoolean("nightMode", false);

        // Ustawienie trybu nocnego
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // Inicjalizacja widoku reklamy
        adContainerView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> {
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

        // Żądanie uprawnień do korzystania z kamery
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }

        // Listener dla przycisku wylogowania
        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // Listener dla przycisku zmiany motywu
        btnTheme.setOnClickListener(view -> {
            if (isNightMode) {
                Log.d("ThemeSwitch", "Switching to Day Mode");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("nightMode", false);
            } else {
                Log.d("ThemeSwitch", "Switching to Night Mode");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("nightMode", true);
            }
            editor.apply();
        });

        // Listener dla przycisku robienia zdjęcia
        btnPicture.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 100);
        });

        // Listener dla przycisku otwierania galerii
        btnGallery.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Gallery.class);
            startActivity(intent);
            finish();
        });

        // Listener dla przycisku otwierania profilu
        btnProfile.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Profile.class);
            startActivity(intent);
            finish();
        });

        // Listener dla przycisku wyświetlania kroków
        btnSteps.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Steps.class);
            startActivity(intent);
            finish();
        });

        // Listener dla przycisku otwierania mapy
        btnMap.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GoogleMaps.class);
            startActivity(intent);
            finish();
        });

        // Inicjalizacja menedżera języka
        LenguageManager lang = new LenguageManager(this);

        // Listener dla przycisku zmiany języka na polski
        btnPL.setOnClickListener(view -> {
            lang.updateResource("pl");
            recreate();
        });

        // Listener dla przycisku zmiany języka na angielski
        btnENG.setOnClickListener(view -> {
            lang.updateResource("en");
            recreate();
        });
    }

    /**
     * Metoda do uzyskania rozmiaru reklamy.
     *
     * @return AdSize Rozmiar reklamy.
     */
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

    /**
     * Metoda do tworzenia kanału powiadomień.
     */
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NGRFitnessReminderChannel";
            String description = MainActivity.this.getResources().getString(R.string.przypominacz);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyNGRFitness", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Metoda wywoływana po zakończeniu aktywności wynikowej.
     *
     * @param requestCode Kod żądania.
     * @param resultCode  Kod wyniku.
     * @param data        Dane wynikowe.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "val", null);
            Uri uri = Uri.parse(path);
            StorageReference ref = storageReference.child(currentUser.getEmail() + "/" + UUID.randomUUID().toString());
            ref.putFile(uri);

            Toast.makeText(this, MainActivity.this.getResources().getString(R.string.zrobiono_zdj), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, MainActivity.this.getResources().getString(R.string.zrobiono_zdj), Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}