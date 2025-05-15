package com.example.shop.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.API.CartActivity;
import com.example.shop.Activities.ControlPannel.Controller;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Categories;
import com.example.shop.Models.Produits;
import com.example.shop.R;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductMainAdapter.OnWishlistClickListener,
        ProductMainAdapter.OnProductClickListener {
    private static final String TAG = "MainActivity";
    private ImageView profilePage, cartPage, wishlistPage, btnLogin, controllerPage, testApi, categoryClothes;
    private RecyclerView recyclerProducts;
    private ProductMainAdapter productAdapter;
    private LinearLayout tools, viewAll;
    private TextView emptyMessage;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Gestion des insets pour les barres système
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialiser SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialiser DBHelper
        dbHelper = new DBHelper(this);

        // Initialiser les vues
        btnLogin = findViewById(R.id.btnGoToSecond);
        wishlistPage = findViewById(R.id.wishlest_page);
        cartPage = findViewById(R.id.cart_page);
        profilePage = findViewById(R.id.profile_page);
        controllerPage = findViewById(R.id.controllerpage);
        testApi = findViewById(R.id.testapi);
        categoryClothes = findViewById(R.id.categorieClothes);
        recyclerProducts = findViewById(R.id.recyclerProducts);
        tools = findViewById(R.id.tools);
        viewAll = findViewById(R.id.ViewAll);
        emptyMessage = findViewById(R.id.emptyMessage);

        // Vérifier que toutes les vues sont initialisées
        if (emptyMessage == null) {
            Log.e(TAG, "emptyMessage TextView is null. Check activity_main.xml for missing ID emptyMessage.");
        }

        // Configurer le RecyclerView
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        List<Produits> produitsList = dbHelper.getAllProducts();
        if (produitsList == null || produitsList.isEmpty()) {
            if (emptyMessage != null) {
                emptyMessage.setVisibility(View.VISIBLE);
                emptyMessage.setText("Aucun produit trouvé. Ajoutez des produits depuis le panneau de contrôle.");
                emptyMessage.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, Controller.class);
                    startActivity(intent);
                });
                recyclerProducts.setVisibility(View.GONE);
            }
            showCustomToast("Aucun produit trouvé");
        } else {
            if (emptyMessage != null) {
                emptyMessage.setVisibility(View.GONE);
                recyclerProducts.setVisibility(View.VISIBLE);
            }
        }
        productAdapter = new ProductMainAdapter(produitsList, this, this, this, userId);
        recyclerProducts.setAdapter(productAdapter);

        // Gestion des clics sur les boutons de navigation
        profilePage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        categoryClothes.setOnClickListener(v -> {
            List<Categories> categories = dbHelper.getCategoriesByNom("Vêtements");
            if (!categories.isEmpty()) {
                Categories categorie = categories.get(0);
                Intent intent = new Intent(MainActivity.this, ProductsByCategoryActivity.class);
                intent.putExtra("category_id", categorie.getId());
                intent.putExtra("category_name", categorie.getNom());
                startActivity(intent);
            } else {
                showCustomToast("Catégorie 'Vêtements' non trouvée");
            }
        });

        tools.setOnClickListener(v -> {
            List<Categories> categories = dbHelper.getCategoriesByNom("Tools");
            if (!categories.isEmpty()) {
                Categories categorie = categories.get(0);
                Intent intent = new Intent(MainActivity.this, ProductsByCategoryActivity.class);
                intent.putExtra("category_id", categorie.getId());
                intent.putExtra("category_name", categorie.getNom());
                startActivity(intent);
            } else {
                showCustomToast("Catégorie 'Tools' non trouvée");
            }
        });

        viewAll.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductsByCategoryActivity.class);
            intent.putExtra("category_id", -1);
            intent.putExtra("category_name", "Tous les produits");
            startActivity(intent);
        });

        controllerPage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Controller.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        wishlistPage.setOnClickListener(v -> {
            if (sharedPreferences.getInt(KEY_USER_ID, -1) == -1) {
                showCustomToast("Veuillez vous connecter pour voir vos favoris");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return;
            }
            Intent intent = new Intent(MainActivity.this, WishlistActivity.class);
            startActivity(intent);
        });

        cartPage.setOnClickListener(v -> {
            if (sharedPreferences.getInt(KEY_USER_ID, -1) == -1) {
                showCustomToast("Veuillez vous connecter pour voir votre panier");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return;
            }
            Intent intent = new Intent(MainActivity.this, CartActivity.class); // Line causing the crash
            startActivity(intent);
        });

        testApi.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TestConneectApI.class); // Corrected class name
            startActivity(intent);
        });
    }

    private void showCustomToast(String message) {
        Toast toast = new Toast(this);
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(20, 20, 20, 20);
        textView.setBackgroundResource(android.R.color.darker_gray);
        textView.setTextColor(Color.WHITE);
        toast.setView(textView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onWishlistClick(Produits produit, boolean isAdded) {
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        Log.d(TAG, "Checking if user is logged in, userId=" + userId);

        if (userId == -1) {
            showCustomToast("Veuillez vous connecter pour gérer vos favoris");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        boolean success;
        if (isAdded) {
            success = dbHelper.addFavorite(userId, produit.getId());
            Log.d(TAG, "Add favorite result for product_id=" + produit.getId() + ": " + success);
            showCustomToast(success ? produit.getNom() + " ajouté aux favoris" :
                    "Erreur lors de l'ajout aux favoris");
        } else {
            success = dbHelper.removeFavorite(userId, produit.getId());
            Log.d(TAG, "Remove favorite result for product_id=" + produit.getId() + ": " + success);
            showCustomToast(success ? produit.getNom() + " retiré des favoris" :
                    "Erreur lors de la suppression des favoris");
        }
    }

    @Override
    public void onProductClick(Produits produit) {
        Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
        intent.putExtra("product_id", produit.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir la liste des produits au retour sur l'activité
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        List<Produits> produitsList = dbHelper.getAllProducts();
        if (produitsList == null || produitsList.isEmpty()) {
            if (emptyMessage != null) {
                emptyMessage.setVisibility(View.VISIBLE);
                emptyMessage.setText("Aucun produit trouvé. Ajoutez des produits depuis le panneau de contrôle.");
                emptyMessage.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, Controller.class);
                    startActivity(intent);
                });
                recyclerProducts.setVisibility(View.GONE);
            }
            showCustomToast("Aucun produit trouvé");
        } else {
            if (emptyMessage != null) {
                emptyMessage.setVisibility(View.GONE);
                recyclerProducts.setVisibility(View.VISIBLE);
            }
        }
        productAdapter.updateData(produitsList);
    }
}