package com.example.ngrfitness;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AppCompat extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LenguageManager lenguageManager=new LenguageManager(this);
        lenguageManager.updateResource(lenguageManager.getLang());
    }
}
