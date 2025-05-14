package com.example.shop.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.API.CartActivity;
import com.example.shop.Adapter.FavoriteProductAdapter;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Produits;
import com.example.shop.Models.Users;
import com.example.shop.R;

import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {
    private static final String TAG = "WishlistActivity";
    private DBHelper dbHelper;
    private ImageView profile_page, cart_page, wishlest_page, btn_back;
    private RecyclerView recyclerFavorites;
    private Button btnCheckout;
    private FavoriteProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wishlist);

        // Initialisation de dbHelper
        dbHelper = new DBHelper(this);
        Log.d(TAG, "dbHelper initialisé");

        // Initialisation des vues
        btn_back = findViewById(R.id.btn_back);
        wishlest_page = findViewById(R.id.wishlest_page);
        cart_page = findViewById(R.id.cart_page);
        profile_page = findViewById(R.id.profile_page);
        recyclerFavorites = findViewById(R.id.recyclerFavorites);
        btnCheckout = findViewById(R.id.btnCheckout);

        // Configurer le bouton de retour
        btn_back.setOnClickListener(v -> finish());

        // Rester sur la page actuelle
        wishlest_page.setOnClickListener(v -> {
            // Rien à faire
        });

        // Vers le panier
        cart_page.setOnClickListener(v -> {
            Intent intent = new Intent(WishlistActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Vers le profil
        profile_page.setOnClickListener(v -> {
            Intent intent = new Intent(WishlistActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Configurer le bouton de commande
        btnCheckout.setOnClickListener(v -> {
            Toast.makeText(this, "Commande en cours...", Toast.LENGTH_SHORT).show();
            // Ajoutez ici la logique pour passer la commande
        });

        // Configurer le RecyclerView
        recyclerFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new FavoriteProductAdapter(new ArrayList<>(), this, dbHelper);
        recyclerFavorites.setAdapter(adapter);

        // Charger les favoris de manière asynchrone
        new LoadFavoritesTask().execute();
    }

    // AsyncTask pour charger les favoris
    private class LoadFavoritesTask extends AsyncTask<Void, Void, List<Produits>> {
        @Override
        protected List<Produits> doInBackground(Void... voids) {
            try {
                int userId = getCurrentUserId();
                Log.d(TAG, "Récupération des favoris pour userId: " + userId);
                return dbHelper.getFavoriteProducts(userId);
            } catch (Exception e) {
                Log.e(TAG, "Erreur lors du chargement des favoris: " + e.getMessage());
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<Produits> favoriteProducts) {
            Log.d(TAG, "Nombre de favoris chargés: " + favoriteProducts.size());
            adapter = new FavoriteProductAdapter(favoriteProducts, WishlistActivity.this, dbHelper);
            recyclerFavorites.setAdapter(adapter);
            if (favoriteProducts.isEmpty()) {
                Toast.makeText(WishlistActivity.this, "Aucun favori trouvé", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Méthode pour récupérer l'ID de l'utilisateur connecté
    private int getCurrentUserId() {
        try {
            // TODO: Implémentez la logique réelle pour récupérer l'utilisateur connecté
            String userEmail = "user@example.com"; // Remplacez par l'email de l'utilisateur connecté
            Users user = dbHelper.getUserByEmail(userEmail);
            if (user != null) {
                Log.d(TAG, "Utilisateur trouvé: " + user.getEmail() + ", ID: " + user.getId());
                return user.getId();
            } else {
                Log.w(TAG, "Aucun utilisateur trouvé pour l'email: " + userEmail);
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la récupération de l'utilisateur: " + e.getMessage());
        }
        // Valeur par défaut pour les tests
        Log.d(TAG, "Utilisation de l'userId par défaut: 1");
        return 1; // Remplacez par une gestion d'erreur appropriée
    }
}