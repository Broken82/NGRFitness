package com.example.ngrfitness;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompat {

    TextInputEditText editTextEmail, editTextPassword; // Pola edycyjne do wprowadzania e-maila i hasła
    Button buttonLogin; // Przycisk do logowania
    FirebaseAuth mAuth; // Instancja FirebaseAuth do uwierzytelniania użytkowników
    ProgressBar progressBar; // Pasek postępu widoczny podczas logowania
    TextView textView; // Tekst umożliwiający rejestrację

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

        setContentView(R.layout.activity_login);

        // Ustaw wcięcia okna dla wyświetlania krawędzi do krawędzi
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicjalizacja elementów interfejsu użytkownika
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);

        // Ustaw listener dla tekstu rejestracji
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        });

        // Ustaw listener dla przycisku logowania
        buttonLogin.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email, password;
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());

            // Sprawdź, czy e-mail jest pusty
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Login.this, Login.this.getResources().getString(R.string.wpisz_mail), Toast.LENGTH_SHORT).show();
                return;
            }

            // Sprawdź, czy hasło jest puste
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, Login.this.getResources().getString(R.string.wpisz_haslo), Toast.LENGTH_SHORT).show();
                return;
            }

            // Uwierzytelnij użytkownika za pomocą Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        /**
                         * Metoda wywoływana po zakończeniu zadania logowania.
                         *
                         * @param task Zadanie logowania.
                         */
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Logowanie zakończone sukcesem
                                Toast.makeText(Login.this, Login.this.getResources().getString(R.string.logowanie_zakon),
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Logowanie nieudane
                                Toast.makeText(Login.this, Login.this.getResources().getString(R.string.logowanie_nieudane),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}
