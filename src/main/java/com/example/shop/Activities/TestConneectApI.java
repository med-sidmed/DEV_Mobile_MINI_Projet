package com.example.shop.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shop.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestConneectApI extends AppCompatActivity {

     TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.api_test);

        textViewResult = findViewById(R.id.textViewResult);
        Button btnFetch = findViewById(R.id.btnFetch);

        btnFetch.setOnClickListener(view -> fetchData());

    }

    private void fetchData() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8087/allproduits");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                reader.close();
                runOnUiThread(() -> textViewResult.setText(result.toString()));
            } catch (Exception e) {
                runOnUiThread(() -> textViewResult.setText("Erreur : " + e.getMessage()));
            }
        }).start();
    }
}
