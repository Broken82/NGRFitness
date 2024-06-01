package com.example.ngrfitness;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Steps extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "Steps";

    SensorManager sensorManager;
    Sensor accelerometer;
    TextView stepsTaken;
    Button pauseButton;

    private boolean isRunning = true;
    private long stepCount = 0;
    private float[] lastAccelerometer = new float[3];
    private float[] currentAccelerometer = new float[3];
    private float[] accelerometerDelta = new float[3];
    private boolean firstUpdate = true;
    private static final float SHAKE_THRESHOLD = 1.5f; //mozna zmienic

    @Override
    protected void onStop() {
        super.onStop();
        if (accelerometer != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        stepsTaken = findViewById(R.id.stepstaken);
        pauseButton = findViewById(R.id.btn_pause);

        if (accelerometer == null) {
            stepsTaken.setText("Urządzenie nie obsługuje akcelerometru.");
            Log.d(TAG, "Sensor akcelerometru nie jest dostępny.");
        } else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "Sensor akcelerometru zarejestrowany.");
        }

        pauseButton.setOnClickListener(this::onPausedButtonClicked);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isRunning && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            updateAccelerometer(event.values);
            if (isStepDetected()) {
                stepCount++;
                stepsTaken.setText(String.valueOf(stepCount));
                Log.d(TAG, "Kroki: " + stepCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void updateAccelerometer(float[] values) {
        System.arraycopy(currentAccelerometer, 0, lastAccelerometer, 0, currentAccelerometer.length);
        System.arraycopy(values, 0, currentAccelerometer, 0, values.length);
        if (firstUpdate) {
            firstUpdate = false;
            System.arraycopy(currentAccelerometer, 0, lastAccelerometer, 0, currentAccelerometer.length);
        }

        for (int i = 0; i < 3; i++) {
            accelerometerDelta[i] = Math.abs(lastAccelerometer[i] - currentAccelerometer[i]);
        }
    }

    private boolean isStepDetected() {
        float delta = (float) Math.sqrt(
                accelerometerDelta[0] * accelerometerDelta[0] +
                        accelerometerDelta[1] * accelerometerDelta[1] +
                        accelerometerDelta[2] * accelerometerDelta[2]
        );

        return delta > SHAKE_THRESHOLD;
    }

    public void onPausedButtonClicked(View view) {
        if (isRunning) {
            isRunning = false;
            pauseButton.setText("Start");
            sensorManager.unregisterListener(this, accelerometer);
            Log.d(TAG, "Sensor akcelerometru zatrzymany.");
        } else {
            isRunning = true;
            pauseButton.setText("Pauza");
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "Sensor akcelerometru wznowiony.");
        }
    }
}
