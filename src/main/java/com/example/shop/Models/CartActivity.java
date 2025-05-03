package com.example.shop.Models;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartAdapterListener {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private LinearLayout emptyCartView;
    private LinearLayout cartSummaryLayout;
    private TextView subtotalPrice, shippingPrice, totalPrice;
    private Button btnCheckout, btnContinueShopping;

    // Frais de livraison fixes (à modifier selon votre besoin)
    private static final double SHIPPING_FEE = 4.99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialisation des vues
        initViews();

        // Charger les données du panier (dans un cas réel, vous chargeriez depuis une base de données ou SharedPreferences)
        loadCartItems();

        // Configuration du RecyclerView
        setupRecyclerView();

        // Mise à jour du résumé du panier
        updateCartSummary();

        // Gérer le bouton Checkout
        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
            } else {
                // Dans un cas réel, vous lanceriez l'activité de paiement ici
                Toast.makeText(this, "Redirection vers le paiement...", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(this, CheckoutActivity.class);
                // startActivity(intent);
            }
        });

        // Gérer le bouton Continuer les achats
        btnContinueShopping.setOnClickListener(v -> {
            // Retour à l'activité précédente ou aller à la page d'accueil
            finish();
            // Ou lancer l'activité de la boutique
            // Intent intent = new Intent(this, ShopActivity.class);
            // startActivity(intent);
        });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewCart);
        emptyCartView = findViewById(R.id.emptyCartView);
        cartSummaryLayout = findViewById(R.id.cartSummaryLayout);
        subtotalPrice = findViewById(R.id.subtotalPrice);
        shippingPrice = findViewById(R.id.shippingPrice);
        totalPrice = findViewById(R.id.totalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
    }

    private void loadCartItems() {
        // Dans un cas réel, vous feriez une requête à votre base de données ou récupèreriez les données
        // depuis une source de données persistante (SharedPreferences, Room, etc.)

        // Exemple de données pour tester l'interface
        cartItems = new ArrayList<>();

        // Ajouter quelques produits au panier pour l'exemple
        cartItems.add(new CartItem(1, "Smartphone XYZ", 499.99, 1, "https://example.com/phone.jpg"));
        cartItems.add(new CartItem(2, "Écouteurs Bluetooth", 79.99, 2, "https://example.com/headphones.jpg"));
        cartItems.add(new CartItem(3, "Coque de protection", 19.99, 1, "https://example.com/case.jpg"));

        // Vérifier si le panier est vide pour afficher la vue appropriée
        updateEmptyCartView();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartItems, this);
        recyclerView.setAdapter(cartAdapter);
    }

    private void updateEmptyCartView() {
        if (cartItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyCartView.setVisibility(View.VISIBLE);
            cartSummaryLayout.setVisibility(View.GONE);
            btnCheckout.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyCartView.setVisibility(View.GONE);
            cartSummaryLayout.setVisibility(View.VISIBLE);
            btnCheckout.setVisibility(View.VISIBLE);
        }
    }

    private void updateCartSummary() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);

        // Calculer le sous-total
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
        }

        // Afficher le sous-total
        subtotalPrice.setText(formatter.format(subtotal));

        // Afficher les frais de livraison
        shippingPrice.setText(formatter.format(cartItems.isEmpty() ? 0 : SHIPPING_FEE));

        // Calculer et afficher le total
        double total = subtotal + (cartItems.isEmpty() ? 0 : SHIPPING_FEE);
        totalPrice.setText(formatter.format(total));
    }

    @Override
    public void onQuantityChanged(int position, int newQuantity) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.get(position).setQuantity(newQuantity);
            updateCartSummary();
        }
    }

    @Override
    public void onItemRemoved(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartAdapter.removeItem(position);
            updateEmptyCartView();
            updateCartSummary();
            Toast.makeText(this, "Produit supprimé du panier", Toast.LENGTH_SHORT).show();
        }
    }
}
