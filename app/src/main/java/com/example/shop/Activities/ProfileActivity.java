package com.example.shop.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shop.R;

public class ProfileActivity extends AppCompatActivity {
//    ImageView profile_page, cart_page, wishlest_page,GoBack,home_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);



//        // Vérifie qu'il existe dans le layout
//        wishlest_page = findViewById(R.id.wishlest_page);
//        GoBack=findViewById(R.id.go_back);
//        home_page=findViewById(R.id.home_page);


//        GoBack.setOnClickListener(v -> {
//            Intent intent = new Intent(ProfileActivity.this, MainActivity.class); // Corrigé ici
//            startActivity(intent);
//        });
//
//        home_page.setOnClickListener(v -> {
//            Intent intent = new Intent(ProfileActivity.this, MainActivity.class); // Corrigé ici
//            startActivity(intent);
//        });
//
//        // Reste sur la page actuelle (pas forcément nécessaire)
//        wishlest_page.setOnClickListener(v -> {
//            Intent intent = new Intent(ProfileActivity.this, WishlistActivity.class); // Corrigé ici
//            startActivity(intent);
//        });


    }
}