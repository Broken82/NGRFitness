package com.example.ngrfitness;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Steps extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor stepCountSensor;
    TextView stepsTaken;
    Button pauseButton;


    private long stepCount;
    private boolean isPaused = false;

    @Override
    protected void onStop() {

        super.onStop();
        if(stepCountSensor != null){
            sensorManager.unregisterListener(this);
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

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        stepsTaken = findViewById(R.id.stepstaken);
        pauseButton = findViewById(R.id.btn_pause);

        if(stepCountSensor == null){
            stepsTaken.setText("Urządzenie nie obsługiwane");

        }


    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            stepCount = (long) event.values[0];
            stepsTaken.setText((int) stepCount);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onPausedButtonClicked(View view){
        if(isPaused){
            isPaused = false;
            pauseButton.setText("Pauza");

        }
        else {
            isPaused = true;
            pauseButton.setText("Start");
        }
    }
}
