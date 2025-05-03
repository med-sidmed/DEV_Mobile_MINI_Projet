package com.example.shop.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shop.R;


public class MainActivity extends AppCompatActivity {
    ImageView profile_page,cart_page,wishlest_page,btnGoToSecond,btnWishlest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

          btnGoToSecond = findViewById(R.id.btnGoToSecond);
         wishlest_page=findViewById(R.id.wishlest_page);
         cart_page=findViewById(R.id.cart_page);
         profile_page=findViewById(R.id.profile_page);
        btnWishlest=findViewById(R.id.btnWishlest);

       ImageView testapi=findViewById(R.id.testapi);

        btnGoToSecond.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Login_Page.class);
            startActivity(intent);
        });

        testapi.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TestConneectApI.class);
            startActivity(intent);
        });


        wishlest_page.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WishlistActivity.class);
            startActivity(intent);
        });

        cart_page.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CardActivity.class);
            startActivity(intent);
        });


        profile_page.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });




//        wishlestcolor
        btnWishlest.setOnClickListener(v -> {
            Object tag = btnWishlest.getTag();

            if (tag == null || !(boolean) tag) {
                btnWishlest.setImageResource(R.drawable.wishlist);
                btnWishlest.setTag(true);
            } else {
                btnWishlest.setImageResource(R.drawable.ic_heart_outline);
                btnWishlest.setTag(false);
            }
        });






    }



}