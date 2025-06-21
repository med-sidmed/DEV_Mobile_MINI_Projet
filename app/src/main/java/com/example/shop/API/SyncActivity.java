package com.example.shop.API;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shop.Activities.MainActivity;
import com.example.shop.R;

public class SyncActivity extends AppCompatActivity {

    private Button btnSync, btnBack;
    private TextView tvSyncStatus;
    private ProgressBar progressBar;
    private ApiSyncManager syncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        // Initialize views
        btnSync = findViewById(R.id.btnSync);
        btnBack = findViewById(R.id.btnBack);
        tvSyncStatus = findViewById(R.id.tvSyncStatus);
        progressBar = findViewById(R.id.progressBar);

        // Initialize sync manager
        try {
            syncManager = new ApiSyncManager(this);
        } catch (ApiSyncManager.SyncException e) {
            throw new RuntimeException(e);
        }

        // Clear focus to prevent IME issues
        getWindow().getDecorView().clearFocus();

        // Configure sync button
        btnSync.setOnClickListener(v -> startSynchronization());

        // Configure back button
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(SyncActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void startSynchronization() {
        if (isFinishing() || isDestroyed()) {
            Log.w("SyncActivity", "Activité terminée, abandon de la synchronisation");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSync.setEnabled(false);
        tvSyncStatus.setText("Statut : Synchronisation en cours...");

        syncManager.synchronizeData(new ApiSyncManager.SyncCallback() {
            @Override
            public void onSuccess(String message) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnSync.setEnabled(true);
                        tvSyncStatus.setText("Statut : " + message);
                        Toast.makeText(SyncActivity.this, message, Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnSync.setEnabled(true);
                        tvSyncStatus.setText("Statut : Échec - " + error);
                        Toast.makeText(SyncActivity.this, "Échec de la synchronisation : " + error, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

}