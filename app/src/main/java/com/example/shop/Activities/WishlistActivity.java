package com.example.shop.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.ArticlePanier;
import com.example.shop.Models.Panier;
import com.example.shop.Models.Produits;
import com.example.shop.Models.Users;
import com.example.shop.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WishlistActivity extends AppCompatActivity implements ProductMainAdapter.OnWishlistClickListener,
        ProductMainAdapter.OnProductClickListener {
    private static final String TAG = "WishlistActivity";
    private DBHelper dbHelper;
    private ImageView profilePage, cartPage, wishlistPage, btnBack;
    private RecyclerView recyclerFavorites;
    private Button btnAddToCart;
    private TextView emptyMessage;
    private ProductMainAdapter adapter;
    private SharedPreferences sharedPreferences;
    private List<Produits> favoriteProducts;

    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wishlist);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Check if user is logged in
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        if (userId == -1) {
            Toast.makeText(this, "Veuillez vous connecter pour voir vos favoris", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Initialize views
        btnBack = findViewById(R.id.btn_back);
        wishlistPage = findViewById(R.id.wishlest_page);
        cartPage = findViewById(R.id.cart_page);
        profilePage = findViewById(R.id.profile_page);
        recyclerFavorites = findViewById(R.id.recyclerFavorites);
        btnAddToCart = findViewById(R.id.btnCheckout);
        emptyMessage = findViewById(R.id.emptyMessage);
        Log.d(TAG, "emptyMessage initialized: " + (emptyMessage != null));

        // Fetch favorite products
        favoriteProducts = dbHelper.getFavoriteProducts(userId);
        Log.d(TAG, "Favorite products count: " + (favoriteProducts != null ? favoriteProducts.size() : 0));

        // Configure RecyclerView
        recyclerFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductMainAdapter(favoriteProducts, this, this, this, userId);
        recyclerFavorites.setAdapter(adapter);

        // Handle empty favorites list
        if (favoriteProducts == null || favoriteProducts.isEmpty()) {
            if (emptyMessage != null) {
                emptyMessage.setVisibility(View.VISIBLE);
                recyclerFavorites.setVisibility(View.GONE);
            } else {
                Log.e(TAG, "emptyMessage TextView is null. Check activity_wishlist.xml for missing ID emptyMessage.");
            }
            btnAddToCart.setEnabled(false);
            Toast.makeText(this, "Aucun favori trouvé", Toast.LENGTH_SHORT).show();
        } else {
            if (emptyMessage != null) {
                emptyMessage.setVisibility(View.GONE);
                recyclerFavorites.setVisibility(View.VISIBLE);
            }
            btnAddToCart.setEnabled(true);
        }

        // Set up click listeners
        btnBack.setOnClickListener(v -> finish());

        wishlistPage.setOnClickListener(v -> {
            // Already on wishlist page
        });

        cartPage.setOnClickListener(v -> {
            Intent intent = new Intent(WishlistActivity.this, CardActivity.class);
            startActivity(intent);
        });

        profilePage.setOnClickListener(v -> {
            Intent intent = new Intent(WishlistActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnAddToCart.setOnClickListener(v -> {
            // Refresh favorite products
            favoriteProducts = dbHelper.getFavoriteProducts(userId);
            Log.d(TAG, "btnAddToCart clicked, favorite products count: " + (favoriteProducts != null ? favoriteProducts.size() : 0));
            if (favoriteProducts == null || favoriteProducts.isEmpty()) {
                Toast.makeText(this, "Aucun produit à ajouter au panier", Toast.LENGTH_SHORT).show();
                return;
            }
            addFavoritesToCart(userId, favoriteProducts);
        });
    }

    private void addFavoritesToCart(int userId, List<Produits> favoriteProducts) {
        try {
            // Get or create cart
            int panierId = getOrCreatePanier(userId);
            if (panierId == -1) {
                Log.e(TAG, "Failed to create or retrieve cart");
                Toast.makeText(this, "Erreur lors de la création du panier", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Using panier_id: " + panierId);

            // Add each favorite product to the cart
            int addedCount = 0;
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
            for (Produits produit : favoriteProducts) {
                Log.d(TAG, "Attempting to add product: " + produit.getNom() + ", ID: " + produit.getId());
                // Comment out isProductInCart for testing
                // if (!isProductInCart(panierId, produit.getId())) {
                ArticlePanier article = new ArticlePanier();
                article.setProduit(produit);
                article.setPanier(new Panier(panierId));
                article.setQuantite(1);
                article.setPrixUnitaire(produit.getPrix());
                article.setPrixTotal(produit.getPrix() * 1);
                article.setDateAjout(currentDate);
                article.setDateModification(currentDate);
                article.setActive(true);

                boolean success = dbHelper.ajouterArticlePanier(article);
                if (success) {
                    addedCount++;
                    Log.d(TAG, "Added product to cart: " + produit.getNom() + ", panier_id: " + panierId);
                } else {
                    Log.e(TAG, "Failed to add product to cart: " + produit.getNom());
                }
                // } else {
                //     Log.d(TAG, "Product already in cart: " + produit.getNom());
                // }
            }

            Toast.makeText(this, addedCount + " produit(s) ajouté(s) au panier", Toast.LENGTH_SHORT).show();
            if (addedCount > 0) {
                Intent intent = new Intent(this, CardActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding favorites to cart: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur lors de l'ajout au panier", Toast.LENGTH_SHORT).show();
        }
    }

    private int getOrCreatePanier(int userId) {
        List<Panier> paniers = dbHelper.getAllPaniers();
        for (Panier panier : paniers) {
            if (panier.getUtilisateur().getId() == userId && panier.isActive()) {
                Log.d(TAG, "Found existing panier_id: " + panier.getId());
                return panier.getId();
            }
        }
        Panier newPanier = new Panier();
        newPanier.setUtilisateur(new Users(userId));
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
        newPanier.setDateAjout(currentDate);
        newPanier.setDateModification(currentDate);
        newPanier.setActive(true);
        long panierId = dbHelper.ajouterPanier(newPanier);
        Log.d(TAG, "Created new panier with ID: " + panierId);
        return (int) panierId;
    }

    private boolean isProductInCart(int panierId, int productId) {
        List<ArticlePanier> articles = dbHelper.getArticlesByPanierId(panierId);
        for (ArticlePanier article : articles) {
            if (article.getProduit().getId() == productId && article.isActive()) {
                return true;
            }
        }
        return false;
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
        if (success) {
            favoriteProducts = dbHelper.getFavoriteProducts(userId);
            adapter.updateData(favoriteProducts);
            if (favoriteProducts == null || favoriteProducts.isEmpty()) {
                if (emptyMessage != null) {
                    emptyMessage.setVisibility(View.VISIBLE);
                    recyclerFavorites.setVisibility(View.GONE);
                }
                btnAddToCart.setEnabled(false);
            } else {
                if (emptyMessage != null) {
                    emptyMessage.setVisibility(View.GONE);
                    recyclerFavorites.setVisibility(View.VISIBLE);
                }
                btnAddToCart.setEnabled(true);
            }
        }
    }

    @Override
    public void onProductClick(Produits produit) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id", produit.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        if (userId != -1) {
            favoriteProducts = dbHelper.getFavoriteProducts(userId);
            adapter.updateData(favoriteProducts);
            if (favoriteProducts == null || favoriteProducts.isEmpty()) {
                if (emptyMessage != null) {
                    emptyMessage.setVisibility(View.VISIBLE);
                    recyclerFavorites.setVisibility(View.GONE);
                }
                btnAddToCart.setEnabled(false);
            } else {
                if (emptyMessage != null) {
                    emptyMessage.setVisibility(View.GONE);
                    recyclerFavorites.setVisibility(View.VISIBLE);
                }
                btnAddToCart.setEnabled(true);
            }
        }
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
}