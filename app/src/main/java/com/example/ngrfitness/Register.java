package com.example.ngrfitness;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Klasa reprezentująca aktywność rejestracji użytkownika.
 */
public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextNick; // Pola edycyjne do wprowadzania e-maila, hasła i pseudonimu
    Button buttonReg; // Przycisk do rejestracji
    FirebaseAuth mAuth; // Instancja FirebaseAuth do uwierzytelniania użytkowników
    ProgressBar progressBar; // Pasek postępu widoczny podczas rejestracji
    TextView textView; // Tekst umożliwiający przejście do logowania

    /**
     * Metoda wywoływana na początku cyklu życia aktywności.
     * Sprawdza, czy użytkownik jest zalogowany.
     */
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
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

        setContentView(R.layout.activity_register);

        // Ustaw wcięcia okna dla wyświetlania krawędzi do krawędzi
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicjalizacja elementów interfejsu użytkownika
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextNick = findViewById(R.id.nickname);
        buttonReg = findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        // Ustaw listener dla tekstu umożliwiającego przejście do logowania
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // Ustaw listener dla przycisku rejestracji
        buttonReg.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email, password, nickname;
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());
            nickname = String.valueOf(editTextNick.getText());

            // Sprawdź, czy e-mail jest pusty
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Register.this, Register.this.getResources().getString(R.string.wpisz_mail), Toast.LENGTH_SHORT).show();
                return;
            }

            // Sprawdź, czy hasło jest puste
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Register.this, Register.this.getResources().getString(R.string.wpisz_haslo), Toast.LENGTH_SHORT).show();
                return;
            }

            // Utwórz nowego użytkownika za pomocą Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        /**
                         * Metoda wywoływana po zakończeniu zadania rejestracji.
                         *
                         * @param task Zadanie rejestracji.
                         */
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null) {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(nickname)
                                            .setPhotoUri(Uri.parse("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS5Flyp08L_jlfA0D5Dydp8N74NTwByOZtKbw&s"))
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(Register.this, Register.this.getResources().getString(R.string.rejestr_sukces),
                                                                Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(Register.this, Register.this.getResources().getString(R.string.rejestr_sukces_blad),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(Register.this, Register.this.getResources().getString(R.string.rejestr_blad),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}
