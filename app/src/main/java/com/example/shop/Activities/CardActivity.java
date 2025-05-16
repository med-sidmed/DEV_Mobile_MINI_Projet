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

import com.bumptech.glide.Glide;
import com.example.shop.API.CartAdapter;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.ArticlePanier;
import com.example.shop.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CardActivity extends AppCompatActivity {
    private static final String TAG = "CardActivity";
    private ImageView wishlest_page, goBack;
    private LinearLayout home_page, profile_page;
    private RecyclerView recyclerViewCart;
    private LinearLayout emptyCartView;
    private Button btnContinueShopping, btnCheckout;
    private TextView subtotalPrice, shippingPrice, totalPrice;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        // Initialiser SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);

        // Initialiser DBHelper
        dbHelper = new DBHelper(this);

        // Initialiser les vues
        wishlest_page = findViewById(R.id.wishlest_page);
        profile_page = findViewById(R.id.profile_page);
        goBack = findViewById(R.id.go_back);
        home_page = findViewById(R.id.home_page);
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        emptyCartView = findViewById(R.id.emptyCartView);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
        btnCheckout = findViewById(R.id.btnCheckout);
        subtotalPrice = findViewById(R.id.subtotalPrice);
        shippingPrice = findViewById(R.id.shippingPrice);
        totalPrice = findViewById(R.id.totalPrice);

        // Configurer le RecyclerView
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        List<ArticlePanier> cartItems = dbHelper.getCartItems(userId);
        CartAdapter adapter = new CartAdapter(cartItems, this);
        recyclerViewCart.setAdapter(adapter);

        // Gérer l'état vide
        if (cartItems == null || cartItems.isEmpty()) {
            emptyCartView.setVisibility(View.VISIBLE);
            recyclerViewCart.setVisibility(View.GONE);
            btnCheckout.setEnabled(false);
        } else {
            emptyCartView.setVisibility(View.GONE);
            recyclerViewCart.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(true);
        }

        // Calculer et afficher les prix
        updateCartSummary(cartItems);

        // Configurer les clics
        goBack.setOnClickListener(v -> finish()); // Retour à l'activité précédente
        home_page.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, MainActivity.class);
            startActivity(intent);
        });
        wishlest_page.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, WishlistActivity.class);
            startActivity(intent);
        });
        profile_page.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(CardActivity.this, MainActivity.class);
            startActivity(intent);
        });
        btnCheckout.setOnClickListener(v -> {
            if (userId == -1) {
                Toast.makeText(this, "Veuillez vous connecter pour passer la commande", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CardActivity.this, LoginActivity.class);
                startActivity(intent);
                return;
            }
            // TODO: Implementer la logique de commande (par ex., naviguer vers une activité de paiement)
            Toast.makeText(this, "Commande en cours de traitement", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateCartSummary(List<ArticlePanier> cartItems) {
        double subtotal = 0.0;
        double shipping = 5.0; // Exemple : frais fixes
        if (cartItems != null) {
            for (ArticlePanier item : cartItems) {
                subtotal += item.getPrixTotal();
            }
        }
        double total = subtotal + shipping;

        DecimalFormat df = new DecimalFormat("#.## €");
        subtotalPrice.setText(df.format(subtotal));
        shippingPrice.setText(df.format(shipping));
        totalPrice.setText(df.format(total));
    }
}