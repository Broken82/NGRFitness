package com.example.ngrfitness;

import android.Manifest;
import android.content.Context;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.ngrfitness.Data.AppDatabase;
import com.example.ngrfitness.Data.StepCount;
import com.example.ngrfitness.Data.StepsDao;

public class Steps extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor stepCountSensor;
    TextView stepsTaken;
    Button pauseButton;
    StepsDao stepsDao;


    private long stepCount;
    private boolean isPaused = false;

    @Override
    protected void onStop() {

        super.onStop();
        if(stepCountSensor != null){
            StepCount stepCounts = new StepCount();
            stepCounts.steps = this.stepCount;
            stepCounts.createdAt = String.valueOf(System.currentTimeMillis());
            stepsDao.insertAll(stepCounts);

            sensorManager.unregisterListener(this);
            stepCount = 0;

            stepsTaken.setText("0");
            Toast.makeText(this, "Zapisano kroki", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {

        super.onResume();
        if(stepCountSensor != null){
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_steps);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "ngr-fitness").allowMainThreadQueries().build();
        stepsDao = db.stepsDao();



        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        stepsTaken = findViewById(R.id.stepstaken);
        pauseButton = findViewById(R.id.btn_pause);

        if(stepCountSensor == null){
            stepsTaken.setText("Urządzenie nie obsługiwane");

        }
        else{
            pauseButton.setText("Pauza");
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            if(!isPaused) {
                stepCount++;
                stepsTaken.setText(String.valueOf(stepCount));
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onPausedButtonClicked(View view){
        if(isPaused){
            isPaused = false;
            onResume();
            pauseButton.setText("Pauza");

        }
        else {
            isPaused = true;
            onStop();
            pauseButton.setText("Start");
        }
    }
}