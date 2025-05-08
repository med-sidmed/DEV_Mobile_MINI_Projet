package com.example.shop.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shop.Databases.DBHelper;
import com.example.shop.R;

public class AjouterProduitActivity extends AppCompatActivity {

    private EditText nomEditText, descriptionEditText, prixEditText, quantiteEditText;
    private Button ajouterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_produit);

        // Initialiser les vues
        nomEditText = findViewById(R.id.nomProduit);
        descriptionEditText = findViewById(R.id.descriptionProduit);
        prixEditText = findViewById(R.id.prixProduit);
        quantiteEditText = findViewById(R.id.quantiteProduit);
        ajouterButton = findViewById(R.id.btnAjouterProduit);

        ajouterButton.setOnClickListener(v -> {
            try {
                String nom = nomEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                double prix = Double.parseDouble(prixEditText.getText().toString().trim());
                int quantite = Integer.parseInt(quantiteEditText.getText().toString().trim());

                // Création de l’instance DBHelper avec le contexte
                DBHelper db_produit = new DBHelper(AjouterProduitActivity.this);

                // Insertion du produit (images null, ids fictifs ici : 1 pour catégorie et utilisateur)
                boolean insertProduit = db_produit.insert_produit(
                        nom, description, prix,
                        null, null, null, null,
                        quantite, 1, 1, true
                );

                if (insertProduit) {
                    Toast.makeText(this, "Produit ajouté avec succès", Toast.LENGTH_SHORT).show();
                    // Optionnel : vider les champs
                    nomEditText.setText("");
                    descriptionEditText.setText("");
                    prixEditText.setText("");
                    quantiteEditText.setText("");
                } else {
                    Toast.makeText(this, "Erreur lors de l'ajout du produit", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Veuillez remplir tous les champs correctement", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
