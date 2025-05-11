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

import com.example.shop.Adapter.CategorieAdapter;
import com.example.shop.Adapter.ProduitAdapter;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Categories;
import com.example.shop.Models.Produits;
import com.example.shop.R;

import java.util.List;

import kotlin.text.Regex;

public class Controller extends AppCompatActivity implements ProduitAdapter.OnProductActionListener, CategorieAdapter.OnCategoryActionListener {

    private DBHelper dbHelper;
    private RecyclerView recyclerProduits;
    private RecyclerView recyclerCategories;
    private ProduitAdapter produitAdapter;
    private CategorieAdapter categorieAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Initialize RecyclerView for products
        recyclerProduits = findViewById(R.id.recyclerProduits);
        if (recyclerProduits == null) {
            Toast.makeText(this, "Erreur : RecyclerView des produits non trouvé", Toast.LENGTH_LONG).show();
            return;
        }
        recyclerProduits.setLayoutManager(new LinearLayoutManager(this));

        // Initialize RecyclerView for categories
        recyclerCategories = findViewById(R.id.categoryList);
        if (recyclerCategories == null) {
            Toast.makeText(this, "Erreur : RecyclerView des catégories non trouvé", Toast.LENGTH_LONG).show();
            return;
        }
        recyclerCategories.setLayoutManager(new LinearLayoutManager(this));

        // Show product and category lists
        ShowProduitList();
        ShowCategorie();

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
            EditText image1 = formView.findViewById(R.id.image1Produit);
            EditText image2 = formView.findViewById(R.id.image2Produit);
            EditText image3 = formView.findViewById(R.id.image3Produit);
            EditText image4 = formView.findViewById(R.id.image4Produit);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ajouter un Produit");
            builder.setView(formView);
            builder.setPositiveButton("Ajouter", (dialog, which) -> {
                try {
                    String nomProduit = nom.getText().toString().trim();
                    String descriptionProduit = description.getText().toString().trim();
                    String prixText = prix.getText().toString().trim();
                    String quantiteText = quantite.getText().toString().trim();
                    String image1Text = image1.getText().toString().trim();
                    String image2Text = image2.getText().toString().trim();
                    String image3Text = image3.getText().toString().trim();
                    String image4Text = image4.getText().toString().trim();

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

                    boolean insert = dbHelper.insertProduct(
                            nomProduit,
                            descriptionProduit,
                            prixProduit,
                            TextUtils.isEmpty(image1Text) ? null : image1Text,
                            TextUtils.isEmpty(image2Text) ? null : image2Text,
                            TextUtils.isEmpty(image3Text) ? null : image3Text,
                            TextUtils.isEmpty(image4Text) ? null : image4Text,
                            quantiteProduit,
                            1,
                            1,
                            true
                    );

                    Toast.makeText(this, insert ? "Produit ajouté avec succès" : "Erreur lors de l'ajout",
                            Toast.LENGTH_SHORT).show();

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

                    if (insert) {
                        ShowCategorie();
                    }

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
            produitAdapter = new ProduitAdapter(produitsList, this, this);
            recyclerProduits.setAdapter(produitAdapter);
        } else {
            produitAdapter.updateData(produitsList);
        }
    }

    private void ShowCategorie() {
        List<Categories> categoriesList = dbHelper.getAllCategories();
        if (categoriesList == null || categoriesList.isEmpty()) {
            Toast.makeText(this, "Aucune catégorie trouvée", Toast.LENGTH_SHORT).show();
            return;
        }
        if (categorieAdapter == null) {
            categorieAdapter = new CategorieAdapter(categoriesList, this, this);
            recyclerCategories.setAdapter(categorieAdapter);
        } else {
            categorieAdapter.updateData(categoriesList);
        }
    }

    @Override
    public void onEditProduct(Produits produit, int position) {
        if (produit == null) {
            Toast.makeText(this, "Erreur : Produit non valide", Toast.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View formView = inflater.inflate(R.layout.add_produit, null);
        if (formView == null) {
            Toast.makeText(this, "Erreur : Impossible de charger le formulaire", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText nom = formView.findViewById(R.id.nomProduit);
        EditText description = formView.findViewById(R.id.descriptionProduit);
        EditText prix = formView.findViewById(R.id.prixProduit);
        EditText quantite = formView.findViewById(R.id.quantiteProduit);
        EditText image1 = formView.findViewById(R.id.image1Produit);
        EditText image2 = formView.findViewById(R.id.image2Produit);
        EditText image3 = formView.findViewById(R.id.image3Produit);
        EditText image4 = formView.findViewById(R.id.image4Produit);

        if (nom == null || description == null || prix == null || quantite == null ||
                image1 == null || image2 == null || image3 == null || image4 == null) {
            Toast.makeText(this, "Erreur : Champs du formulaire non trouvés", Toast.LENGTH_SHORT).show();
            return;
        }

        nom.setText(produit.getNom() != null ? produit.getNom() : "");
        description.setText(produit.getDescription() != null ? produit.getDescription() : "");
        prix.setText(String.format("%.2f", produit.getPrix()));
        quantite.setText(String.valueOf(produit.getQuantite()));
        image1.setText(produit.getImage1() != null ? produit.getImage1() : "");
        image2.setText(produit.getImage2() != null ? produit.getImage2() : "");
        image3.setText(produit.getImage3() != null ? produit.getImage3() : "");
        image4.setText(produit.getImage4() != null ? produit.getImage4() : "");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier le Produit");
        builder.setView(formView);
        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            try {
                String nomProduit = nom.getText().toString().trim();
                String descriptionProduit = description.getText().toString().trim();
                String prixText = prix.getText().toString().trim();
                String quantiteText = quantite.getText().toString().trim();
                String image1Text = image1.getText().toString().trim();
                String image2Text = image2.getText().toString().trim();
                String image3Text = image3.getText().toString().trim();
                String image4Text = image4.getText().toString().trim();

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

                boolean update = dbHelper.updateProduct(
                        produit.getId(),
                        nomProduit,
                        descriptionProduit,
                        prixProduit,
                        TextUtils.isEmpty(image1Text) ? null : image1Text,
                        TextUtils.isEmpty(image2Text) ? null : image2Text,
                        TextUtils.isEmpty(image3Text) ? null : image3Text,
                        TextUtils.isEmpty(image4Text) ? null : image4Text,
                        quantiteProduit,
                        produit.getCategorie().getId(),
                        produit.getUtilisateur().getId(),
                        produit.isActive()
                );

                Toast.makeText(this, update ? "Produit modifié avec succès" : "Erreur lors de la modification",
                        Toast.LENGTH_SHORT).show();

                if (update) {
                    ShowProduitList();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    @Override
    public void onDeleteProduct(Produits produit, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer le Produit")
                .setMessage("Êtes-vous sûr de vouloir supprimer " + produit.getNom() + " ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    boolean delete = dbHelper.deleteProduct(produit.getId());
                    Toast.makeText(this, delete ? "Produit supprimé avec succès" : "Erreur lors de la suppression",
                            Toast.LENGTH_SHORT).show();
                    if (delete) {
                        ShowProduitList();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onEditCategory(Categories category, int position) {
        if (category == null) {
            Toast.makeText(this, "Erreur : Catégorie non valide", Toast.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View formView = inflater.inflate(R.layout.add_categorie, null);
        if (formView == null) {
            Toast.makeText(this, "Erreur : Impossible de charger le formulaire", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText nom = formView.findViewById(R.id.NomCategorie);
        EditText description = formView.findViewById(R.id.DescriptionCategorie);

        if (nom == null || description == null) {
            Toast.makeText(this, "Erreur : Champs du formulaire non trouvés", Toast.LENGTH_SHORT).show();
            return;
        }

        nom.setText(category.getNom() != null ? category.getNom() : "");
        description.setText(category.getDescription() != null ? category.getDescription() : "");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier la Catégorie");
        builder.setView(formView);
        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            try {
                String nomCategorie = nom.getText().toString().trim();
                String descriptionCategorie = description.getText().toString().trim();

                if (TextUtils.isEmpty(nomCategorie)) {
                    Toast.makeText(this, "Le nom de la catégorie est requis", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean update = dbHelper.updateCategory(
                        category.getId(),
                        nomCategorie,
                        descriptionCategorie
                );

                Toast.makeText(this, update ? "Catégorie modifiée avec succès" : "Erreur lors de la modification",
                        Toast.LENGTH_SHORT).show();

                if (update) {
                    ShowCategorie();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    @Override
    public void onDeleteCategory(Categories category, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la Catégorie")
                .setMessage("Êtes-vous sûr de vouloir supprimer " + category.getNom() + " ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    boolean delete = dbHelper.deleteCategory(category.getId());
                    Toast.makeText(this, delete ? "Catégorie supprimée avec succès" : "Erreur lors de la suppression ou catégorie associée à des produits",
                            Toast.LENGTH_SHORT).show();
                    if (delete) {
                        ShowCategorie();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

}