package com.example.shop.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shop.R;

public class CardActivity  extends AppCompatActivity {

    ImageView profile_page, cart_page, wishlest_page, home_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);


 // Vérifie qu'il existe dans le layout
        wishlest_page = findViewById(R.id.wishlest_page);
        cart_page = findViewById(R.id.cart_page);
        profile_page = findViewById(R.id.profile_page);
        home_page =findViewById(R.id.home_page);


        // Vers home page
        cart_page.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, CardActivity.class); // Corrigé ici
            startActivity(intent);
        });

        // Reste sur la page actuelle (pas forcément nécessaire)
        wishlest_page.setOnClickListener(v -> {

            Intent intent = new Intent(CardActivity.this, WishlistActivity.class); // Corrigé ici
            startActivity(intent);
        });

        // Vers le panier
        cart_page.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, CardActivity.class); // Corrigé ici
            startActivity(intent);
        });

        // Vers le profil
        profile_page.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
