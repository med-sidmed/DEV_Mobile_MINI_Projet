package com.example.shop.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shop.R;

public class WishlistActivity extends AppCompatActivity {

    ImageView profile_page, cart_page, wishlest_page, btnGoToSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wishlist);

        // Initialisation des vues
        btnGoToSecond = findViewById(R.id.btnGoToSecond); // Vérifie qu'il existe dans le layout
        wishlest_page = findViewById(R.id.wishlest_page);
        cart_page = findViewById(R.id.cart_page);
        profile_page = findViewById(R.id.profile_page);

        // Aller vers la page de connexion
        if (btnGoToSecond != null) {
            btnGoToSecond.setOnClickListener(v -> {
                Intent intent = new Intent(WishlistActivity.this, Login_Page.class); // Vérifie le nom
                startActivity(intent);
            });
        }

        // Reste sur la page actuelle (pas forcément nécessaire)
        wishlest_page.setOnClickListener(v -> {
            // Optionnel : faire un scroll to top ou ne rien faire
        });

        // Vers le panier
        cart_page.setOnClickListener(v -> {
            Intent intent = new Intent(WishlistActivity.this, CardActivity.class); // Corrigé ici
            startActivity(intent);
        });

        // Vers le profil
        profile_page.setOnClickListener(v -> {
            Intent intent = new Intent(WishlistActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
