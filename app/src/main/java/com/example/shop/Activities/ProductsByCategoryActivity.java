package com.example.shop.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Produits;
import com.example.shop.R;

import java.util.List;

public class ProductsByCategoryActivity extends AppCompatActivity implements ProductMainAdapter.OnWishlistClickListener, ProductMainAdapter.OnProductClickListener {

    private RecyclerView recyclerProducts;
    private ProductMainAdapter productAdapter;
    private DBHelper dbHelper;
    private TextView textViewCategoryName;
    private int categoryId;
    private String categoryName;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_by_category);

        // Initialiser SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialiser DBHelper
        dbHelper = new DBHelper(this);

        // Initialiser les vues
        ImageView goBack = findViewById(R.id.go_back);
        textViewCategoryName = findViewById(R.id.textViewCategoryName);
        recyclerProducts = findViewById(R.id.recyclerProducts);

        // Configurer le bouton de retour
        goBack.setOnClickListener(v -> {
            Intent intent = new Intent(ProductsByCategoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Terminer l'activité pour éviter l'accumulation dans la pile
        });

        // Récupérer les données de l'intent
        Intent intent = getIntent();
        categoryId = intent.getIntExtra("category_id", -1);
        categoryName = intent.getStringExtra("category_name");

        // Afficher le nom de la catégorie
        textViewCategoryName.setText(categoryName != null ? categoryName : "Produits");

        // Configurer le RecyclerView
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        List<Produits> produitsList = dbHelper.getProductsByCategory(categoryId);
        if (produitsList == null || produitsList.isEmpty()) {
            Toast.makeText(this, "Aucun produit trouvé dans cette catégorie", Toast.LENGTH_SHORT).show();
            recyclerProducts.setVisibility(View.GONE);
        } else {
            recyclerProducts.setVisibility(View.VISIBLE);
        }
        productAdapter = new ProductMainAdapter(produitsList, this, this, this, userId);
        recyclerProducts.setAdapter(productAdapter);
    }

    @Override
    public void onWishlistClick(Produits produit, boolean isAdded) {
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        if (userId == -1) {
            Toast.makeText(this, "Veuillez vous connecter pour gérer vos favoris", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        boolean success;
        if (isAdded) {
            success = dbHelper.addFavorite(userId, produit.getId());
            Toast.makeText(this, success ? produit.getNom() + " ajouté aux favoris" :
                    "Erreur lors de l'ajout aux favoris", Toast.LENGTH_SHORT).show();
        } else {
            success = dbHelper.removeFavorite(userId, produit.getId());
            Toast.makeText(this, success ? produit.getNom() + " retiré des favoris" :
                    "Erreur lors de la suppression des favoris", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProductClick(Produits produit) {
        Intent intent = new Intent(this, ProduitDetailActivity.class);
        intent.putExtra("product_id", produit.getId());
        startActivity(intent);
    }
}