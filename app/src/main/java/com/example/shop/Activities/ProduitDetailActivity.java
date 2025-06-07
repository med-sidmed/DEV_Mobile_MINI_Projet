package com.example.shop.Activities;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Produits;
import com.example.shop.R;

import java.util.Objects;

public class ProduitDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProduitDetailActivity";
    private DBHelper dbHelper;
    private Produits product;
    private int quantity = 1;

    Context context;

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
        try {
            product = dbHelper.getProductById(productId);
        } catch (Exception e) {
            Log.e(TAG, "Error loading product with ID " + productId + ": " + e.getMessage(), e);
            Toast.makeText(this, "Erreur de chargement du produit", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (product == null) {
            Log.e(TAG, "Product not found for ID: " + productId);
            Toast.makeText(this, "Produit non trouvé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            Log.e(TAG, "Toolbar not found");
            finish();
            return;
        }
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize views
        ImageView imageViewProduct = findViewById(R.id.imageViewProduct);
        ImageView imageViewSecondary1 = findViewById(R.id.imageViewSecondary1);
        ImageView imageViewSecondary2 = findViewById(R.id.imageViewSecondary2);
        TextView textViewProductName = findViewById(R.id.textViewProductName);
        TextView textViewPrice = findViewById(R.id.textViewPrice);
        TextView textViewAvailability = findViewById(R.id.textViewAvailability);
        TextView textViewQuantity = findViewById(R.id.textViewQuantity);
        TextView textViewStockInfo = findViewById(R.id.textViewStockInfo);
        TextView textViewDescription = findViewById(R.id.textViewDescription);
        TextView textViewWeight = findViewById(R.id.textViewWeight);
        TextView textViewDimensions = findViewById(R.id.textViewDimensions);
        TextView textViewColor = findViewById(R.id.textViewColor);
        TextView textViewReference = findViewById(R.id.textViewReference);
        Button buttonMinus = findViewById(R.id.buttonMinus);
        Button buttonPlus = findViewById(R.id.buttonPlus);
        Button buttonAddToCart = findViewById(R.id.buttonAddToCart);

        // Check for null views
        if (imageViewProduct == null || textViewProductName == null || textViewPrice == null ||
                textViewAvailability == null || textViewQuantity == null || textViewStockInfo == null ||
                textViewDescription == null || textViewWeight == null || textViewDimensions == null ||
                textViewColor == null || textViewReference == null || buttonMinus == null ||
                buttonPlus == null || buttonAddToCart == null) {
            Log.e(TAG, "One or more views are null");
            Toast.makeText(this, "Erreur d'initialisation de la page", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate views
        // Populate views
        textViewProductName.setText(product.getNom() != null ? product.getNom() : "N/A");
        textViewPrice.setText(String.format("%.2f €", product.getPrix()));
        textViewAvailability.setText(product.getQuantite() > 0 ? "En stock" : "Rupture de stock");
        textViewAvailability.setBackgroundResource(product.getQuantite() > 0 ? R.drawable.availability_badge : R.drawable.out_of_stock_badge);
        textViewQuantity.setText(String.valueOf(quantity));
        textViewStockInfo.setText(product.getQuantite() + " disponibles");
        textViewDescription.setText(product.getDescription() != null ? product.getDescription() : "Aucune description disponible");
// Temporarily disable fields not in database
        textViewWeight.setText("N/A"); // poids not in Products table
        textViewDimensions.setText("N/A"); // dimensions not in Products table
        textViewColor.setText("N/A"); // couleur not in Products table
        textViewReference.setText("N/A"); // reference not in Products table


        // Load images
        loadImage(product.getImage1(), imageViewProduct);
        loadImage(product.getImage2(), imageViewSecondary1);
        loadImage(product.getImage3(), imageViewSecondary2);

        Log.d(TAG, "Image1: " + product.getImage1());
        Log.d(TAG, "Image2: " + product.getImage2());
        Log.d(TAG, "Image3: " + product.getImage3());

        // Quantity controls
        buttonMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                textViewQuantity.setText(String.valueOf(quantity));
            }
        });

        buttonPlus.setOnClickListener(v -> {
            if (quantity < product.getStock()) {
                quantity++;
                textViewQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Stock maximum atteint", Toast.LENGTH_SHORT).show();
            }
        });

        // Add to cart
        buttonAddToCart.setOnClickListener(v -> {
            Toast.makeText(this, quantity + " " + product.getNom() + " ajouté(s) au panier", Toast.LENGTH_SHORT).show();

        });
    }

    // Méthode utilitaire pour charger une image à partir d'une URL ou d'un nom de ressource drawable
    private void loadImage(Object imageSource, ImageView imageView) {
        if (imageSource == null) {
            Glide.with(this)
                    .load(R.drawable.image_not_found)
                    .into(imageView);
            return;
        }

        String imageStr = imageSource.toString().trim();

        // 🔥 Enlève le préfixe "drawable://" s'il existe
        if (imageStr.startsWith("drawable://")) {
            imageStr = imageStr.replace("drawable://", "");
        }

        // Si c’est une URL distante
        if (imageStr.startsWith("http://") || imageStr.startsWith("https://")) {
            Glide.with(this)
                    .load(imageStr)
                    .placeholder(R.drawable.image_not_found)
                    .into(imageView);
        } else {
            // Sinon on suppose que c'est une ressource dans drawable
            int resId = getResources().getIdentifier(imageStr, "drawable", getPackageName());
            if (resId != 0) {
                Glide.with(this)
                        .load(resId)
                        .placeholder(R.drawable.image_not_found)
                        .into(imageView);
            } else {
                Glide.with(this)
                        .load(R.drawable.image_not_found)
                        .into(imageView);
            }
        }
    }




}
