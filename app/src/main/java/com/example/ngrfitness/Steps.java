package com.example.ngrfitness;

import static android.icu.number.NumberFormatter.with;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.ngrfitness.Data.AppDatabase;
import com.example.ngrfitness.Data.StepCount;
import com.example.ngrfitness.Data.StepsDao;

public class Steps extends AppCompat implements SensorEventListener {

    SensorManager sensorManager;
    Sensor stepCountSensor;
    TextView stepsTaken;
    Button pauseButton, backBtn;
    StepsDao stepsDao;


    private long stepCount = 0;
    private boolean isPaused = false;

    @Override
    protected void onStop() {

        super.onStop();
        if (stepCountSensor != null) {
            StepCount stepCounts = new StepCount();
            stepCounts.steps = this.stepCount;
            stepCounts.createdAt = String.valueOf(System.currentTimeMillis());
            stepsDao.insertAll(stepCounts);

            sensorManager.unregisterListener(this);
            stepCount = 0;

            stepsTaken.setText("0");
            Toast.makeText(this, Steps.this.getResources().getString(R.string.zapisano_kroki), Toast.LENGTH_SHORT).show();
            NotificationManagerCompat.from(this).cancelAll();
        }
    }


    @Override
    protected void onResume() {

        super.onResume();
        if (stepCountSensor != null) {
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
            CreateNotification(Steps.this.getResources().getString(R.string.dziasiec_krokow), Steps.this.getResources().getString(R.string.chodz), 1);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_steps);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(Steps.this, MainActivity.class);
                startActivity(intent);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "ngr-fitness").allowMainThreadQueries().build();
        stepsDao = db.stepsDao();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {

        }

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                1);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        stepsTaken = findViewById(R.id.stepstaken);
        pauseButton = findViewById(R.id.btn_pause);

        backBtn = findViewById(R.id.btn_back);

        if (stepCountSensor == null) {
            stepsTaken.setText(Steps.this.getResources().getString(R.string.nie_obsl));

        } else {
            isPaused = false;
            onResume();
            pauseButton.setText(Steps.this.getResources().getString(R.string.pauza));
        }

        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (!isPaused) {
                stepCount++;
                stepsTaken.setText(String.valueOf(stepCount));

                if(stepCount > 10){
                    CreateNotification(Steps.this.getResources().getString(R.string.dziesiec_udalo),
                            Steps.this.getResources().getString(R.string.gratulacje), 2);
                    NotificationManagerCompat.from(this).cancel(1);

                }
            }
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void onPausedButtonClicked(View view) {
        if (isPaused) {
            isPaused = false;
            onResume();
            pauseButton.setText(Steps.this.getResources().getString(R.string.pauza));

        } else {
            isPaused = true;
            onStop();
            pauseButton.setText("Start");
        }
    }

    public void CreateNotification(String text, String title, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notifyNGRFitness")
                .setContentText(text)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    2);
            return;
        }



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManagerCompat.from(this).notify(id, builder.build());

        }


    }
