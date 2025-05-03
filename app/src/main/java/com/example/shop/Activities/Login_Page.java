package com.example.shop.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import  androidx.appcompat.app.AppCompatActivity;

import com.example.shop.R;

public class Login_Page extends AppCompatActivity {

    Button btnBack ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Bouton pour retourner à MainActivity
         btnBack = findViewById(R.id.btnback);
        btnBack.setOnClickListener(v -> {
            finish();
         });
    }
}