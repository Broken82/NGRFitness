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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.ngrfitness.Data.AppDatabase;
import com.example.ngrfitness.Data.StepsDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Klasa reprezentująca aktywność profilu użytkownika.
 */
public class Profile extends AppCompatActivity {

    TextView stepsValue; // Tekstowy widok do wyświetlania liczby kroków
    TextView emailValue; // Tekstowy widok do wyświetlania e-maila użytkownika
    TextView nameValue; // Tekstowy widok do wyświetlania imienia użytkownika

    FirebaseAuth mAuth; // Instancja FirebaseAuth do uwierzytelniania użytkowników
    FirebaseUser currentUser; // Aktualnie zalogowany użytkownik
    Button btnBack; // Przycisk do powrotu do głównej aktywności
    ImageView avatar; // Widok obrazu profilowego użytkownika
    StepsDao StepsDao; // DAO do zarządzania danymi kroków w bazie danych
    private ListAdapter adapter; // Adapter do RecyclerView
    private RecyclerView listRecView; // RecyclerView do wyświetlania listy kroków

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

        setContentView(R.layout.activity_profile);

        // Ustaw wcięcia okna dla wyświetlania krawędzi do krawędzi
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicjalizacja bazy danych Room
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "ngr-fitness").allowMainThreadQueries().build();
        StepsDao = db.stepsDao();

        // Inicjalizacja elementów interfejsu użytkownika
        stepsValue = findViewById(R.id.krokiprawo);
        emailValue = findViewById(R.id.emailprawo);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        btnBack = findViewById(R.id.back_btn);
        avatar = findViewById(R.id.profile_picture);
        nameValue = findViewById(R.id.name);

        listRecView = findViewById(R.id.listRecycle);
        adapter = new ListAdapter(StepsDao.getAll());
        listRecView.setAdapter(adapter);
        listRecView.setLayoutManager(new LinearLayoutManager(this));

        // Ustawienie wartości e-maila i imienia użytkownika
        if (currentUser != null) {
            emailValue.setText(currentUser.getEmail());
            nameValue.setText(currentUser.getDisplayName());
        }

        // Ustawienie liczby kroków
        if (StepsDao.getAll() == null) {
            stepsValue.setText("0");
        } else {
            stepsValue.setText(String.valueOf(StepsDao.getMax()));
        }

        // Listener dla przycisku powrotu
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
