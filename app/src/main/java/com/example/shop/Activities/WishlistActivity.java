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

import java.util.List;

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

    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wishlist);

        // Initialiser SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Vérifier si l'utilisateur est connecté
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        if (userId == -1) {
            Toast.makeText(this, "Veuillez vous connecter pour voir vos favoris", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialiser DBHelper
        dbHelper = new DBHelper(this);

        // Initialiser les vues
        btnBack = findViewById(R.id.btn_back);
        wishlistPage = findViewById(R.id.wishlest_page);
        cartPage = findViewById(R.id.cart_page);
        profilePage = findViewById(R.id.profile_page);
        recyclerFavorites = findViewById(R.id.recyclerFavorites);
        btnAddToCart = findViewById(R.id.btnCheckout);
        emptyMessage = findViewById(R.id.emptyMessage);
        Log.d(TAG, "emptyMessage initialized: " + (emptyMessage != null));

        // Configurer le RecyclerView
        recyclerFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        List<Produits> favoriteProducts = dbHelper.getFavoriteProducts(userId);
        adapter = new ProductMainAdapter(favoriteProducts, this, this, this, userId);
        recyclerFavorites.setAdapter(adapter);

        // Gérer l'affichage si la liste est vide
        if (favoriteProducts == null || favoriteProducts.isEmpty()) {
            if (emptyMessage != null) {
                emptyMessage.setVisibility(View.VISIBLE);
                recyclerFavorites.setVisibility(View.GONE);
            } else {
                Log.e(TAG, "emptyMessage TextView is null. Check activity_wishlist.xml for missing ID emptyMessage.");
            }
            Toast.makeText(this, "Aucun favori trouvé", Toast.LENGTH_SHORT).show();
        } else {
            if (emptyMessage != null) {
                emptyMessage.setVisibility(View.GONE);
                recyclerFavorites.setVisibility(View.VISIBLE);
            }
        }

        // Configurer les clics
        btnBack.setOnClickListener(v -> finish());

        wishlistPage.setOnClickListener(v -> {
            // Rester sur la page actuelle
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
            if (favoriteProducts == null || favoriteProducts.isEmpty()) {
                Toast.makeText(this, "Aucun produit à ajouter au panier", Toast.LENGTH_SHORT).show();
                return;
            }
            addFavoritesToCart(userId, favoriteProducts);
        });
    }

    private void addFavoritesToCart(int userId, List<Produits> favoriteProducts) {
        try {
            // Créer un panier si nécessaire
            Panier panier = getOrCreatePanier(userId);
            if (panier == null) {
                Toast.makeText(this, "Erreur lors de la création du panier", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ajouter chaque produit favori au panier
            for (Produits produit : favoriteProducts) {
                dbHelper.ajouterArticlePanier(new ArticlePanier(
                        produit,
                        panier,
                        1, // Quantité par défaut
                        produit.getPrix(),
                        produit.getPrix(),
                        new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()),
                        null,
                        true
                ));
            }
            Toast.makeText(this, "Produits ajoutés au panier", Toast.LENGTH_SHORT).show();
            // Rediriger vers le panier
            Intent intent = new Intent(this, CardActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'ajout des favoris au panier: " + e.getMessage());
            Toast.makeText(this, "Erreur lors de l'ajout au panier", Toast.LENGTH_SHORT).show();
        }
    }

    private Panier getOrCreatePanier(int userId) {
        List<Panier> paniers = dbHelper.getAllPaniers();
        for (Panier panier : paniers) {
            if (panier.getUtilisateur().getId() == userId && panier.isActive()) {
                return panier;
            }
        }
        // Créer un nouveau panier
        Panier newPanier = new Panier();
        newPanier.setUtilisateur(new Users(userId));
        newPanier.setDateAjout(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
        newPanier.setActive(true);
        dbHelper.ajouterPanier(newPanier);
        return newPanier;
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
            if (success) {
                // Rafraîchir la liste des favoris
                List<Produits> favoriteProducts = dbHelper.getFavoriteProducts(userId);
                adapter.updateData(favoriteProducts);
                if (favoriteProducts == null || favoriteProducts.isEmpty()) {
                    if (emptyMessage != null) {
                        emptyMessage.setVisibility(View.VISIBLE);
                        recyclerFavorites.setVisibility(View.GONE);
                    }
                } else {
                    if (emptyMessage != null) {
                        emptyMessage.setVisibility(View.GONE);
                        recyclerFavorites.setVisibility(View.VISIBLE);
                    }
                }
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
        // Rafraîchir la liste des favoris
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        if (userId != -1) {
            List<Produits> favoriteProducts = dbHelper.getFavoriteProducts(userId);
            adapter.updateData(favoriteProducts);
            if (favoriteProducts == null || favoriteProducts.isEmpty()) {
                if (emptyMessage != null) {
                    emptyMessage.setVisibility(View.VISIBLE);
                    recyclerFavorites.setVisibility(View.GONE);
                }
            } else {
                if (emptyMessage != null) {
                    emptyMessage.setVisibility(View.GONE);
                    recyclerFavorites.setVisibility(View.VISIBLE);
                }
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