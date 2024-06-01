package com.example.ngrfitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.ngrfitness.Data.AppDatabase;
import com.example.ngrfitness.Data.StepsDao;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends AppCompatActivity {


    TextView stepsValue;
    TextView emailValue;
    TextView nameValue;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Button btnBack;
    ImageView avatar;
    StepsDao StepsDao;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "ngr-fitness").allowMainThreadQueries().build();
        StepsDao = db.stepsDao();




        stepsValue = findViewById(R.id.krokiprawo);
        emailValue = findViewById(R.id.emailprawo);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        btnBack = findViewById(R.id.back_btn);
        avatar = findViewById(R.id.profile_picture);
        nameValue = findViewById(R.id.name);

        if(currentUser != null){
            emailValue.setText(currentUser.getEmail());
            nameValue.setText(currentUser.getDisplayName());
        }

        if(StepsDao.getAll() == null){
            stepsValue.setText("0");
        }
        else{
            stepsValue.setText(String.valueOf(StepsDao.getMax()));
        }

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });


    }







}
