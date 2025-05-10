package com.example.shop.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shop.R;

public class ProfileActivity extends AppCompatActivity {

    ImageView profile_page, wishlest_page, btnWishlest;
    LinearLayout home_page,cart_page;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        home_page = findViewById(R.id.home_page);
        cart_page = findViewById(R.id.cart_page);
        wishlest_page = findViewById(R.id.wishlistpage);


        wishlest_page.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, WishlistActivity.class);
            startActivity(intent);
        });

        cart_page.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CardActivity.class);
            startActivity(intent);
        });

        home_page.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView go_back =findViewById(R.id.go_back);

        go_back.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });


    }
}
