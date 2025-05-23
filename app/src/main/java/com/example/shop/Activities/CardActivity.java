package com.example.shop.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shop.Adapter.CartAdapter;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.ArticlePanier;
import com.example.shop.Models.Panier;
import com.example.shop.Models.Users;
import com.example.shop.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;



public class CardActivity extends AppCompatActivity {
    private static final String TAG = "CardActivity";
    private ImageView goBack, wishlestPage;
    private LinearLayout homePage, profilePage;
    private RecyclerView recyclerViewCart;
    private LinearLayout emptyCartView;
    private Button btnContinueShopping, btnCheckout;
    private TextView subtotalPrice, shippingPrice, totalPrice;
    private DBHelper dbHelper;
    private CartAdapter cartAdapter;
    private List<ArticlePanier> cartItems;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        dbHelper = new DBHelper(this);

        goBack = findViewById(R.id.go_back);
        wishlestPage = findViewById(R.id.wishlest_page);
        homePage = findViewById(R.id.home_page);
        profilePage = findViewById(R.id.profile_page);
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        emptyCartView = findViewById(R.id.emptyCartView);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
        btnCheckout = findViewById(R.id.btnCheckout);
        subtotalPrice = findViewById(R.id.subtotalPrice);
        shippingPrice = findViewById(R.id.shippingPrice);
        totalPrice = findViewById(R.id.totalPrice);

        int userId = getCurrentUserId();
        Log.d(TAG, "User ID: " + userId);

        refreshCart(userId);

        goBack.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, MainActivity.class);
            startActivity(intent);
        });

        homePage.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, MainActivity.class);
            startActivity(intent);
        });

        wishlestPage.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, WishlistActivity.class);
            startActivity(intent);
        });

        profilePage.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Commande en cours de traitement", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int userId = getCurrentUserId();
        if (userId != -1) {
            refreshCart(userId);
        }
    }

    private void refreshCart(int userId) {
        int panierId = getPanierIdForUser(userId);
        Log.d(TAG, "Panier ID: " + panierId);
        cartItems = dbHelper.getArticlesByPanierId(panierId);
        Log.d(TAG, "Nombre d'articles dans le panier: " + cartItems.size());
        for (ArticlePanier article : cartItems) {
            Log.d(TAG, "Article: " + article.getProduit().getNom() + ", Quantité: " + article.getQuantite());
        }

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartItems, dbHelper);
        recyclerViewCart.setAdapter(cartAdapter);

        if (cartItems.isEmpty()) {
            emptyCartView.setVisibility(View.VISIBLE);
            recyclerViewCart.setVisibility(View.GONE);
            btnCheckout.setEnabled(false);
            Log.d(TAG, "Panier vide, afficher emptyCartView");
        } else {
            emptyCartView.setVisibility(View.GONE);
            recyclerViewCart.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(true);
            Log.d(TAG, "Panier non vide, afficher RecyclerView");
        }

        updateCartSummary();
    }

    private int getCurrentUserId() {
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        if (userId == -1) {
            Log.e(TAG, "Aucun utilisateur connecté");
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        return userId;
    }

    private int getPanierIdForUser(int userId) {
        List<Panier> paniers = dbHelper.getAllPaniers();
        Log.d(TAG, "Nombre de paniers trouvés: " + paniers.size());
        for (Panier panier : paniers) {
            Log.d(TAG, "Panier ID: " + panier.getId() + ", Utilisateur ID: " + panier.getUtilisateur().getId() + ", Actif: " + panier.isActive());
            if (panier.getUtilisateur().getId() == userId && panier.isActive()) {
                return panier.getId();
            }
        }
        Panier newPanier = new Panier();
        newPanier.setUtilisateur(new Users(userId));
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
        newPanier.setDateAjout(currentDate);
        newPanier.setDateModification(currentDate);
        newPanier.setActive(true);
        int newPanierId = (int) dbHelper.ajouterPanier(newPanier);
        Log.d(TAG, "Nouveau panier créé avec ID: " + newPanierId);
        return newPanierId;
    }

    public void updateCartSummary() {
        double subtotal = 0.0;
        double shipping = 5.0;
        for (ArticlePanier article : cartItems) {
            subtotal += article.getPrixTotal();
        }
        double total = subtotal + shipping;

        DecimalFormat df = new DecimalFormat("#.## €");
        subtotalPrice.setText(df.format(subtotal));
        shippingPrice.setText(df.format(shipping));
        totalPrice.setText(df.format(total));
        Log.d(TAG, "Résumé du panier - Sous-total: " + subtotal + ", Frais de livraison: " + shipping + ", Total: " + total);
    }
}