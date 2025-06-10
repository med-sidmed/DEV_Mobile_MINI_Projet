//package com.example.shop.API;
//
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.shop.Activities.MainActivity;
//import com.example.shop.R;
//
//
//public class SyncActivity extends AppCompatActivity {
//
//    private Button btnSync, btnBack;
//    private TextView tvSyncStatus;
//    private ProgressBar progressBar;
//        private  AppCompatActivity  syncManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sync);
//
//        // Initialiser les vues
//        btnSync = findViewById(R.id.btnSync);
//        btnBack = findViewById(R.id.btnBack);
//        tvSyncStatus = findViewById(R.id.tvSyncStatus);
//        progressBar = findViewById(R.id.progressBar);
//
//
//        // Configurer le bouton de synchronisation
//        btnSync.setOnClickListener(v -> startSynchronization());
//
//        // Configurer le bouton de retour
//        btnBack.setOnClickListener(v -> {
//            Intent intent = new Intent(SyncActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        });
//    }
//
//    private void startSynchronization() {
//        // Afficher la barre de progression
//        progressBar.setVisibility(View.VISIBLE);
//        btnSync.setEnabled(false);
//        tvSyncStatus.setText("Statut : Synchronisation en cours...");
//
//        // Lancer la synchronisation
////     syncManager.synchronizeData(new ApiSyncManager.SyncCallback() {
////            @Override
////            public void onSuccess(String message) {
////                runOnUiThread(() -> {
////                    progressBar.setVisibility(View.GONE);
////                    btnSync.setEnabled(true);
////                    tvSyncStatus.setText("Statut : " + message);
////                    Toast.makeText(SyncActivity.this, message, Toast.LENGTH_SHORT).show();
////                });
////            }
////
////            @Override
////            public void onFailure(String error) {
////                runOnUiThread(() -> {
////                    progressBar.setVisibility(View.GONE);
////                    btnSync.setEnabled(true);
////                    tvSyncStatus.setText("Statut : Échec - " + error);
////                    Toast.makeText(SyncActivity.this, error, Toast.LENGTH_LONG).show();
////                });
////            }
////        });
//    }
//}