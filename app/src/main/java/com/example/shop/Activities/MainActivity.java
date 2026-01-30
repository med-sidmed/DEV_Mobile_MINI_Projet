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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.Activities.CartActivity;
import com.example.shop.Activities.ControlPannel.Controller;
import com.example.shop.Utils.AppExecutors;
import com.example.shop.Models.Categories;
import com.example.shop.Models.Produits;
import com.example.shop.Databases.DBHelper;
import com.example.shop.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements ProductMainAdapter.OnWishlistClickListener,
        ProductMainAdapter.OnProductClickListener {
    private static final String TAG = "MainActivity";
    private ImageView btnLogin, controllerPage, testApi, categoryClothes;
    private BottomNavigationView bottomNavigationView;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNavigation), (v, insets) -> {
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
        controllerPage = findViewById(R.id.controllerpage);
        testApi = findViewById(R.id.testapi);
        categoryClothes = findViewById(R.id.categorieClothes);
        recyclerProducts = findViewById(R.id.recyclerProducts);
        tools = findViewById(R.id.tools);
        viewAll = findViewById(R.id.ViewAll);
        emptyMessage = findViewById(R.id.emptyMessage);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Vérifier que toutes les vues sont initialisées
        if (emptyMessage == null) {
            Log.e(TAG, "emptyMessage TextView is null. Check activity_main.xml for missing ID emptyMessage.");
        }

        // Configurer le RecyclerView
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);

        // Asynchronous loading
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<Produits> produitsList = dbHelper.getAllProducts();
            Set<Integer> favoriteIds = userId != -1 ? dbHelper.getFavoriteProductIds(userId) : new HashSet<>();

            AppExecutors.getInstance().mainThread().execute(() -> {
                if (produitsList == null || produitsList.isEmpty()) {
                    if (emptyMessage != null) {
                        emptyMessage.setVisibility(View.VISIBLE);
                        emptyMessage
                                .setText("Aucun produit trouvé. Ajoutez des produits depuis le panneau de contrôle.");
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
                productAdapter = new ProductMainAdapter(produitsList, this, this, this, userId, favoriteIds);
                recyclerProducts.setAdapter(productAdapter);
            });
        });

        // Configurer la navigation basse
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_wishlist) {
                startActivity(new Intent(this, WishlistActivity.class));
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // Gestion des clics sur les boutons de navigation (legacy removal)
        // Les listeners sur profilePage, cartPage, wishlistPage ont été remplacés par
        // bottomNavigationView

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

        AppExecutors.getInstance().diskIO().execute(() -> {
            boolean success;
            if (isAdded) {
                success = dbHelper.addFavorite(userId, produit.getId());
                AppExecutors.getInstance().mainThread().execute(() -> {
                    Log.d(TAG, "Add favorite result for product_id=" + produit.getId() + ": " + success);
                    showCustomToast(
                            success ? produit.getNom() + " ajouté aux favoris" : "Erreur lors de l'ajout aux favoris");
                });
            } else {
                success = dbHelper.removeFavorite(userId, produit.getId());
                AppExecutors.getInstance().mainThread().execute(() -> {
                    Log.d(TAG, "Remove favorite result for product_id=" + produit.getId() + ": " + success);
                    showCustomToast(success ? produit.getNom() + " retiré des favoris"
                            : "Erreur lors de la suppression des favoris");
                });
            }
        });
    }

    @Override
    public void onProductClick(Produits produit) {
        Intent intent = new Intent(MainActivity.this, ProduitDetailActivity.class);
        intent.putExtra("product_id", produit.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir la liste des produits au retour sur l'activité
        AppExecutors.getInstance().diskIO().execute(() -> {
            int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
            List<Produits> produitsList = dbHelper.getAllProducts();
            Set<Integer> favoriteIds = userId != -1 ? dbHelper.getFavoriteProductIds(userId) : new HashSet<>();

            AppExecutors.getInstance().mainThread().execute(() -> {
                if (produitsList == null || produitsList.isEmpty()) {
                    if (emptyMessage != null) {
                        emptyMessage.setVisibility(View.VISIBLE);
                        emptyMessage
                                .setText("Aucun produit trouvé. Ajoutez des produits depuis le panneau de contrôle.");
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
                if (productAdapter != null) {
                    productAdapter.updateData(produitsList);
                }
            });
        });
    }
}