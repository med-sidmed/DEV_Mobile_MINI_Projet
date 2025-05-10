package com.example.shop.Activities.ControlPannel;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.Adapter.ProduitAdapter;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Produits;
import com.example.shop.R;

import java.util.List;

public class Controller extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView recyclerProduits;
    private ProduitAdapter produitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Initialize RecyclerView
        recyclerProduits = findViewById(R.id.recyclerProduits);
        if (recyclerProduits == null) {
            Toast.makeText(this, "Erreur : RecyclerView non trouvé dans le layout", Toast.LENGTH_LONG).show();
            return;
        }
        recyclerProduits.setLayoutManager(new LinearLayoutManager(this));

        // Show product list
        ShowProduitList();

        // Initialize buttons
        Button btnAjouterProduit = findViewById(R.id.btnAjouterProduit);
        Button btnAjouterCategorie = findViewById(R.id.btnAjouterCategorie);

        // Product addition dialog
        btnAjouterProduit.setOnClickListener(v -> {
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
                    String nomProduit = nom.getText().toString().trim();
                    String descriptionProduit = description.getText().toString().trim();
                    String prixText = prix.getText().toString().trim();
                    String quantiteText = quantite.getText().toString().trim();

                    if (TextUtils.isEmpty(nomProduit)) {
                        Toast.makeText(this, "Le nom du produit est requis", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(prixText)) {
                        Toast.makeText(this, "Le prix est requis", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(quantiteText)) {
                        Toast.makeText(this, "La quantité est requise", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double prixProduit;
                    int quantiteProduit;
                    try {
                        prixProduit = Double.parseDouble(prixText);
                        quantiteProduit = Integer.parseInt(quantiteText);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Prix ou quantité invalide", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (prixProduit < 0) {
                        Toast.makeText(this, "Le prix ne peut pas être négatif", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (quantiteProduit < 0) {
                        Toast.makeText(this, "La quantité ne peut pas être négative", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Insertion produit
                    boolean insert = dbHelper.insertProduct(
                            nomProduit, descriptionProduit, prixProduit,
                            null, null, null, null,
                            quantiteProduit, 1, 1, true
                    );

                    Toast.makeText(this, insert ? "Produit ajouté avec succès" : "Erreur lors de l'ajout",
                            Toast.LENGTH_SHORT).show();

                    // Refresh product list after insertion
                    if (insert) {
                        ShowProduitList();
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("Annuler", null);
            builder.show();
        });

        // Category addition dialog
        btnAjouterCategorie.setOnClickListener(view -> {
            LayoutInflater inflater = LayoutInflater.from(this);
            View formView = inflater.inflate(R.layout.add_categorie, null);

            EditText editNom = formView.findViewById(R.id.NomCategorie);
            EditText editDescription = formView.findViewById(R.id.DescriptionCategorie);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ajouter une Catégorie");
            builder.setView(formView);
            builder.setPositiveButton("Ajouter", (dialog, which) -> {
                try {
                    String nomCategorie = editNom.getText().toString().trim();
                    String descriptionCategorie = editDescription.getText().toString().trim();

                    if (TextUtils.isEmpty(nomCategorie)) {
                        Toast.makeText(this, "Le nom de la catégorie est requis", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean insert = dbHelper.insertCategory(nomCategorie, descriptionCategorie);

                    Toast.makeText(this, insert ? "Catégorie ajoutée avec succès" : "Erreur lors de l'ajout",
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("Annuler", null);
            builder.show();
        });
    }
    private void ShowProduitList() {
        List<Produits> produitsList = dbHelper.getAllProducts();
        if (produitsList == null || produitsList.isEmpty()) {
            Toast.makeText(this, "Aucun produit trouvé", Toast.LENGTH_SHORT).show();
            return;
        }
        if (produitAdapter == null) {
            produitAdapter = new ProduitAdapter(produitsList, this);
            recyclerProduits.setAdapter(produitAdapter);
        } else {
            produitAdapter.updateData(produitsList);
        }
    }
}