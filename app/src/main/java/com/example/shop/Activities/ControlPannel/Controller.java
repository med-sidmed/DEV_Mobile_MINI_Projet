package com.example.shop.Activities.ControlPannel;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.Activities.MainActivity;
import com.example.shop.Adapter.CategorieAdapter;
import com.example.shop.Adapter.ProduitAdapter;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Categories;
import com.example.shop.Models.Produits;
import com.example.shop.R;

import java.util.List;

public class Controller extends AppCompatActivity implements ProduitAdapter.OnProductActionListener, CategorieAdapter.OnCategoryActionListener {

    private static final String TAG = "Controller";
    private static final int STORAGE_PERMISSION_CODE = 100;
    private DBHelper dbHelper;
    private RecyclerView recyclerProduits;
    private RecyclerView recyclerCategories;
    private ProduitAdapter produitAdapter;
    private CategorieAdapter categorieAdapter;
    Button retourner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        retourner=findViewById(R.id.retourner);

        retourner.setOnClickListener(v->{
            Intent intent=new Intent(Controller.this, MainActivity.class);
            startActivity(intent);
        });

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

        // Request permissions before loading products
        requestStoragePermissions();

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
            CheckBox clothes = formView.findViewById(R.id.vetement);
            CheckBox digitals = formView.findViewById(R.id.digital);
            CheckBox outils = formView.findViewById(R.id.outils);

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

                    // Validate image URLs
                    if (!image1Text.isEmpty() && !image1Text.matches("^(https?://.*|file://.*)$")) {
                        Toast.makeText(this, "URL de l'image 1 invalide", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!image2Text.isEmpty() && !image2Text.matches("^(https?://.*|file://.*)$")) {
                        Toast.makeText(this, "URL de l'image 2 invalide", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!image3Text.isEmpty() && !image3Text.matches("^(https?://.*|file://.*)$")) {
                        Toast.makeText(this, "URL de l'image 3 invalide", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!image4Text.isEmpty() && !image4Text.matches("^(https?://.*|file://.*)$")) {
                        Toast.makeText(this, "URL de l'image 4 invalide", Toast.LENGTH_SHORT).show();
                        return;
                    }

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
                        Log.e(TAG, "Invalid number format: " + e.getMessage());
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

                    // Determine category ID based on checkbox
                    int CategorieId = -1;
                    if (clothes.isChecked()) {
                        Integer id = dbHelper.getIdCategoriesByNom("Vêtements");
                        if (id != null) {
                            CategorieId = id;
                        }
                    } else if (outils.isChecked()) {
                        Integer id = dbHelper.getIdCategoriesByNom("Outils");
                        if (id != null) {
                            CategorieId = id;
                        }
                    } else if (digitals.isChecked()) {
                        Integer id = dbHelper.getIdCategoriesByNom("Digital");
                        if (id != null) {
                            CategorieId = id;
                        }
                    }

                    if (CategorieId == -1) {
                        Toast.makeText(this, "Veuillez sélectionner une catégorie valide", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "No valid category selected or category not found");
                        return;
                    }

                    // Get user ID from SharedPreferences
                    int userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("userId", -1);
                    if (userId == -1) {
                        Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "No user ID found in SharedPreferences");
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
                            CategorieId,
                            userId,
                            true
                    );

                    if (insert) {
                        Toast.makeText(this, "Produit ajouté avec succès", Toast.LENGTH_SHORT).show();
                        ShowProduitList();
                    } else {
                        Toast.makeText(this, "Erreur lors de l'ajout du produit", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to insert product into database");
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Exception during product insertion: " + e.getMessage(), e);
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

                    if (insert) {
                        Toast.makeText(this, "Catégorie ajoutée avec succès", Toast.LENGTH_SHORT).show();
                        ShowCategorie();
                    } else {
                        Toast.makeText(this, "Erreur lors de l'ajout de la catégorie", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to insert category into database");
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Exception during category insertion: " + e.getMessage(), e);
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
            Log.d(TAG, "No products found in database");
            return;
        }
        if (produitAdapter == null) {
            produitAdapter = new ProduitAdapter(produitsList, this, this);
            recyclerProduits.setAdapter(produitAdapter);
        } else {
            produitAdapter.updateData(produitsList);
        }
        Log.d(TAG, "Loaded " + produitsList.size() + " products");
    }

    private void ShowCategorie() {
        List<Categories> categoriesList = dbHelper.getAllCategories();
        if (categoriesList == null || categoriesList.isEmpty()) {
            Toast.makeText(this, "Aucune catégorie trouvée", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "No categories found in database");
            return;
        }
        if (categorieAdapter == null) {
            categorieAdapter = new CategorieAdapter(categoriesList, this, this);
            recyclerCategories.setAdapter(categorieAdapter);
        } else {
            categorieAdapter.updateData(categoriesList);
        }
        Log.d(TAG, "Loaded " + categoriesList.size() + " categories");
    }

    @Override
    public void onEditProduct(Produits produit, int position) {
        if (produit == null) {
            Toast.makeText(this, "Erreur : Produit non valide", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Invalid product for editing");
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View formView = inflater.inflate(R.layout.add_produit, null);
        if (formView == null) {
            Toast.makeText(this, "Erreur : Impossible de charger le formulaire", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to inflate edit product form");
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
        CheckBox clothes = formView.findViewById(R.id.vetement);
        CheckBox digitals = formView.findViewById(R.id.digital);
        CheckBox outils = formView.findViewById(R.id.outils);

        if (nom == null || description == null || prix == null || quantite == null ||
                image1 == null || image2 == null || image3 == null || image4 == null) {
            Toast.makeText(this, "Erreur : Champs du formulaire non trouvés", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "One or more form fields not found");
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

        // Pre-check the appropriate category checkbox
        int currentCategoryId = produit.getCategorie().getId();
        Categories category = dbHelper.getCategoryById(currentCategoryId);
        if (category != null) {
            String categoryName = category.getNom();
            if ("Vêtements".equals(categoryName)) {
                clothes.setChecked(true);
            } else if ("Outils".equals(categoryName)) {
                outils.setChecked(true);
            } else if ("Digital".equals(categoryName)) {
                digitals.setChecked(true);
            }
        }

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

                // Validate image URLs
                if (!image1Text.isEmpty() && !image1Text.matches("^(https?://.*|file://.*)$")) {
                    Toast.makeText(this, "URL de l'image 1 invalide", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!image2Text.isEmpty() && !image2Text.matches("^(https?://.*|file://.*)$")) {
                    Toast.makeText(this, "URL de l'image 2 invalide", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!image3Text.isEmpty() && !image3Text.matches("^(https?://.*|file://.*)$")) {
                    Toast.makeText(this, "URL de l'image 3 invalide", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!image4Text.isEmpty() && !image4Text.matches("^(https?://.*|file://.*)$")) {
                    Toast.makeText(this, "URL de l'image 4 invalide", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                    Log.e(TAG, "Invalid number format: " + e.getMessage());
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

                // Determine category ID for update
                int CategorieId = -1;
                if (clothes.isChecked()) {
                    Integer id = dbHelper.getIdCategoriesByNom("Vêtements");
                    if (id != null) {
                        CategorieId = id;
                    }
                } else if (outils.isChecked()) {
                    Integer id = dbHelper.getIdCategoriesByNom("Outils");
                    if (id != null) {
                        CategorieId = id;
                    }
                } else if (digitals.isChecked()) {
                    Integer id = dbHelper.getIdCategoriesByNom("Digital");
                    if (id != null) {
                        CategorieId = id;
                    }
                }

                if (CategorieId == -1) {
                    Toast.makeText(this, "Veuillez sélectionner une catégorie valide", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "No valid category selected for update");
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
                        CategorieId,
                        produit.getUtilisateur().getId(),
                        produit.isActive()
                );

                if (update) {
                    Toast.makeText(this, "Produit modifié avec succès", Toast.LENGTH_SHORT).show();
                    ShowProduitList();
                } else {
                    Toast.makeText(this, "Erreur lors de la modification du produit", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to update product in database");
                }

            } catch (Exception e) {
                Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Exception during product update: " + e.getMessage(), e);
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
                    if (delete) {
                        Toast.makeText(this, "Produit supprimé avec succès", Toast.LENGTH_SHORT).show();
                        ShowProduitList();
                    } else {
                        Toast.makeText(this, "Erreur lors de la suppression du produit", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to delete product from database");
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onEditCategory(Categories category, int position) {
        if (category == null) {
            Toast.makeText(this, "Erreur : Catégorie non valide", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Invalid category for editing");
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View formView = inflater.inflate(R.layout.add_categorie, null);
        if (formView == null) {
            Toast.makeText(this, "Erreur : Impossible de charger le formulaire", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to inflate edit category form");
            return;
        }

        EditText nom = formView.findViewById(R.id.NomCategorie);
        EditText description = formView.findViewById(R.id.DescriptionCategorie);

        if (nom == null || description == null) {
            Toast.makeText(this, "Erreur : Champs du formulaire non trouvés", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "One or more form fields not found");
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

                if (update) {
                    Toast.makeText(this, "Catégorie modifiée avec succès", Toast.LENGTH_SHORT).show();
                    ShowCategorie();
                } else {
                    Toast.makeText(this, "Erreur lors de la modification de la catégorie", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to update category in database");
                }

            } catch (Exception e) {
                Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Exception during category update: " + e.getMessage(), e);
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
                    if (delete) {
                        Toast.makeText(this, "Catégorie supprimée avec succès", Toast.LENGTH_SHORT).show();
                        ShowCategorie();
                    } else {
                        Toast.makeText(this, "Erreur lors de la suppression ou catégorie associée à des produits", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to delete category from database");
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void requestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions = new String[]{android.Manifest.permission.READ_MEDIA_IMAGES};
            } else {
                permissions = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE};
            }
            if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, STORAGE_PERMISSION_CODE);
            } else {
                ShowProduitList();
                ShowCategorie();
            }
        } else {
            ShowProduitList();
            ShowCategorie();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ShowProduitList();
            ShowCategorie();
        } else {
            Toast.makeText(this, "Permission de stockage refusée", Toast.LENGTH_SHORT).show();
        }
    }
}