package com.example.shop.Activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shop.Databases.DBHelper;
import com.example.shop.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        Button btnAjouterProduit = findViewById(R.id.btnAjouterProduit);

        btnAjouterProduit.setOnClickListener(v -> {
            // Inflater le layout du formulaire (réutilise ton layout add_produit)
            LayoutInflater inflater = LayoutInflater.from(this);
            View formView = inflater.inflate(R.layout.add_produit, null);

            EditText nom = formView.findViewById(R.id.nomProduit);
            EditText description = formView.findViewById(R.id.descriptionProduit);
            EditText prix = formView.findViewById(R.id.prixProduit);
            EditText quantite = formView.findViewById(R.id.quantiteProduit);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ajouter un Produit");
            builder.setView(formView);
            builder.setPositiveButton("Ajouter", (dialog, which) -> {
                try {
                    String nomProduit = nom.getText().toString();
                    String descriptionProduit = description.getText().toString();
                    double prixProduit = Double.parseDouble(prix.getText().toString());
                    int quantiteProduit = Integer.parseInt(quantite.getText().toString());

                    DBHelper db = new DBHelper(this);
                    boolean insert = db.insert_produit(
                            nomProduit, descriptionProduit, prixProduit,
                            null, null, null, null,
                            quantiteProduit, 1, 1, true
                    );

                    if (insert) {
                        Toast.makeText(this, "Produit ajouté avec succès", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
                    }

                }  catch (Exception e) {
                e.printStackTrace(); // ou Log.e("ProfileActivity", "Erreur", e);
                Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
            builder.setNegativeButton("Annuler", null);
            builder.show();
        });

        // Tu peux réactiver et corriger cette partie si tu utilises les boutons ci-dessous :
        /*
        ImageView wishlest_page = findViewById(R.id.wishlest_page);
        ImageView GoBack = findViewById(R.id.go_back);
        ImageView home_page = findViewById(R.id.home_page);

        GoBack.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        home_page.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        wishlest_page.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, WishlistActivity.class);
            startActivity(intent);
        });
        */
    }
}
