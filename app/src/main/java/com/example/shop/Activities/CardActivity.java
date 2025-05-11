package com.example.shop.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shop.R;

public class CardActivity  extends AppCompatActivity {

    ImageView  cart_page, wishlest_page,GoBack;

    LinearLayout home_page,profile_page;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);


 // Vérifie qu'il existe dans le layout
        wishlest_page = findViewById(R.id.wishlest_page);
         profile_page = findViewById(R.id.profile_page);
         GoBack=findViewById(R.id.go_back);
       home_page=findViewById(R.id.home_page);


        GoBack.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, MainActivity.class); // Corrigé ici
            startActivity(intent);
        });

        home_page.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, MainActivity.class); // Corrigé ici
            startActivity(intent);
        });


        // Reste sur la page actuelle (pas forcément nécessaire)
        wishlest_page.setOnClickListener(v -> {

            Intent intent = new Intent(CardActivity.this, WishlistActivity.class); // Corrigé ici
            startActivity(intent);
        });

        // Vers le profil
        profile_page.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
