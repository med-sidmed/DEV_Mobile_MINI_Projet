package com.example.shop.Activities;

import android.annotation.SuppressLint;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.shop.Utils.AppExecutors;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Produits;
import com.example.shop.R;

import java.util.Objects;

public class ProduitDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProduitDetailActivity";
    private DBHelper dbHelper;
    private Produits product;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_product_detail);
        } catch (Exception e) {
            Log.e(TAG, "Error setting content view: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur de chargement de la page", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DBHelper(this);

        // Get product ID from intent
        int productId = getIntent().getIntExtra("product_id", -1);
        Log.d(TAG, "Received product_id: " + productId);
        if (productId == -1) {
            Log.e(TAG, "Invalid product_id");
            Toast.makeText(this, "Produit non trouvé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load product
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                final Produits loadedProduct = dbHelper.getProductById(productId);
                AppExecutors.getInstance().mainThread().execute(() -> {
                    if (loadedProduct == null) {
                        Log.e(TAG, "Product not found for ID: " + productId);
                        Toast.makeText(this, "Produit non trouvé", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    product = loadedProduct;
                    setupProductUI();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error loading product with ID " + productId + ": " + e.getMessage(), e);
                AppExecutors.getInstance().mainThread().execute(() -> {
                    Toast.makeText(this, "Erreur de chargement du produit", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void setupProductUI() {
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Initialize views
        ImageView imageViewProduct = findViewById(R.id.imageViewProduct);
        TextView textViewProductName = findViewById(R.id.textViewProductName);
        TextView textViewPrice = findViewById(R.id.textViewPrice);
        TextView textViewAvailability = findViewById(R.id.textViewAvailability);
        TextView textViewDescription = findViewById(R.id.textViewDescription);
        Button buttonAddToCart = findViewById(R.id.buttonAddToCart);
        FloatingActionButton fabWishlist = findViewById(R.id.fabWishlist);

        // Check for null views
        if (imageViewProduct == null || textViewProductName == null || textViewPrice == null ||
                textViewAvailability == null || textViewDescription == null || buttonAddToCart == null
                || fabWishlist == null) {
            Log.e(TAG, "One or more essential views are null");
            Toast.makeText(this, "Erreur d'initialisation de la page", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate views
        textViewProductName.setText(product.getNom() != null ? product.getNom() : "N/A");
        textViewPrice.setText(String.format("%.2f €", product.getPrix()));
        textViewAvailability.setText(product.getStock() > 0 ? "En stock" : "Rupture de stock");
        textViewAvailability.setBackgroundResource(
                product.getStock() > 0 ? R.drawable.availability_badge : R.drawable.out_of_stock_badge);
        textViewDescription
                .setText(product.getDescription() != null ? product.getDescription() : "Aucune description disponible");

        // Load images
        if (product.getImage1() != null && !product.getImage1().isEmpty()) {
            Glide.with(this)
                    .load(product.getImage1())
                    .placeholder(R.drawable.image_not_found)
                    .into(imageViewProduct);
        }

        // Wishlist click
        fabWishlist.setOnClickListener(v -> {
            Toast.makeText(this, "Ajouté aux favoris", Toast.LENGTH_SHORT).show();
        });

        buttonAddToCart.setOnClickListener(v -> {
            Toast.makeText(this, product.getNom() + " ajouté au panier", Toast.LENGTH_SHORT).show();
            // Implement add to cart logic here
        });
    }
}