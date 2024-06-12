package com.example.ngrfitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import com.google.firebase.storage.ListResult;

/**
 * Klasa aktywności obsługująca wyświetlanie galerii obrazów przechowywanych w Firebase Storage.
 */
public class Gallery extends AppCompatActivity {

    private RecyclerView pictureRecView; // RecyclerView do wyświetlania obrazów
    private FirebaseAuth mAuth; // Instancja uwierzytelniania Firebase
    private FirebaseUser currentUser; // Aktualnie uwierzytelniony użytkownik
    private MyAdapter adapter; // Adapter do RecyclerView
    private Button backBtn; // Przycisk do powrotu do głównej aktywności

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

        setContentView(R.layout.activity_gallery);

        // Ustaw wcięcia okna dla wyświetlania krawędzi do krawędzi
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicjalizacja elementów UI
        backBtn = findViewById(R.id.back_btn);
        adapter = new MyAdapter(this);
        pictureRecView = findViewById(R.id.picturesRecycle);
        pictureRecView.setAdapter(adapter);
        pictureRecView.setLayoutManager(new LinearLayoutManager(this));

        // Inicjalizacja uwierzytelniania Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Odniesienie do Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child(currentUser.getEmail());

        // Wylistowanie wszystkich obrazów z Firebase Storage
        imagesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    ArrayList<Picture> pictures = new ArrayList<>();
                    // Początkowy obraz dodany ręcznie
                    pictures.add(new Picture(1, "https://i1.sndcdn.com/avatars-5YhOoeqkl8R1QTtE-VPEy0Q-t1080x1080.jpg"));

                    // Pętla przez wszystkie elementy i pobranie URL-a do pobrania
                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            String imageUrl = downloadUrl.toString();
                            pictures.add(new Picture(1, imageUrl));
                        });
                    }
                    adapter.setPictures(pictures);
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(this, Gallery.this.getResources().getString(R.string.nie_dziala), Toast.LENGTH_SHORT).show();
                });

        // Obsługa kliknięcia przycisku powrotu
        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
