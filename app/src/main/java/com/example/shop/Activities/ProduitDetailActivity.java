package com.example.shop.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class ProduitDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProduitDetailActivity";
    private DBHelper dbHelper;
    private Produits product;
    private int quantity = 1; // Quantité initiale

    // Déclaration des vues
    private ImageView imageViewProduct, imageViewSecondary1, imageViewSecondary2;
    private TextView textViewProductName, textViewPrice, textViewAvailability, textViewQuantity,
            textViewStockInfo, textViewDescription, textViewWeight, textViewDimensions,
            textViewColor, textViewReference;
    private Button buttonMinus, buttonPlus, buttonAddToCart;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialiser DBHelper
        dbHelper = new DBHelper(this);

        // Configurer la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialiser les vues
        imageViewProduct = findViewById(R.id.imageViewProduct);
        imageViewSecondary1 = findViewById(R.id.imageViewSecondary1);
        imageViewSecondary2 = findViewById(R.id.imageViewSecondary2);
        textViewProductName = findViewById(R.id.textViewProductName);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewAvailability = findViewById(R.id.textViewAvailability);
        textViewQuantity = findViewById(R.id.textViewQuantity);
        textViewStockInfo = findViewById(R.id.textViewStockInfo);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewWeight = findViewById(R.id.textViewWeight);
        textViewDimensions = findViewById(R.id.textViewDimensions);
        textViewColor = findViewById(R.id.textViewColor);
        textViewReference = findViewById(R.id.textViewReference);
        buttonMinus = findViewById(R.id.buttonMinus);
        buttonPlus = findViewById(R.id.buttonPlus);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);

        // Log pour vérifier les vues
        Log.d(TAG, "imageViewProduct: " + (imageViewProduct != null));
        Log.d(TAG, "imageViewSecondary1: " + (imageViewSecondary1 != null));
        Log.d(TAG, "imageViewSecondary2: " + (imageViewSecondary2 != null));
        Log.d(TAG, "textViewProductName: " + (textViewProductName != null));
        Log.d(TAG, "textViewPrice: " + (textViewPrice != null));
        Log.d(TAG, "textViewAvailability: " + (textViewAvailability != null));
        Log.d(TAG, "textViewQuantity: " + (textViewQuantity != null));
        Log.d(TAG, "textViewStockInfo: " + (textViewStockInfo != null));
        Log.d(TAG, "textViewDescription: " + (textViewDescription != null));
        Log.d(TAG, "textViewWeight: " + (textViewWeight != null));
        Log.d(TAG, "textViewDimensions: " + (textViewDimensions != null));
        Log.d(TAG, "textViewColor: " + (textViewColor != null));
        Log.d(TAG, "textViewReference: " + (textViewReference != null));
        Log.d(TAG, "buttonMinus: " + (buttonMinus != null));
        Log.d(TAG, "buttonPlus: " + (buttonPlus != null));
        Log.d(TAG, "buttonAddToCart: " + (buttonAddToCart != null));

        // Vérifier si toutes les vues sont initialisées
        if (imageViewProduct == null || imageViewSecondary1 == null || imageViewSecondary2 == null ||
                textViewProductName == null || textViewPrice == null || textViewAvailability == null ||
                textViewQuantity == null || textViewStockInfo == null || textViewDescription == null ||
                textViewWeight == null || textViewDimensions == null || textViewColor == null ||
                textViewReference == null || buttonMinus == null || buttonPlus == null ||
                buttonAddToCart == null) {
            Log.e(TAG, "One or more views are null");
            Toast.makeText(this, "Erreur d'initialisation de la page", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Récupérer l'ID du produit depuis l'Intent
        int productId = getIntent().getIntExtra("product_id", -1);
        Log.d(TAG, "Received product_id: " + productId);
        if (productId == -1) {
            Log.e(TAG, "Invalid product_id");
            Toast.makeText(this, "Produit non trouvé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Récupérer le produit depuis la base de données
        product = dbHelper.getProductById(productId);
        if (product == null) {
            Log.e(TAG, "Product not found for ID: " + productId);
            Toast.makeText(this, "Produit non trouvé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Afficher les détails du produit
        textViewProductName.setText(product.getNom());
        textViewPrice.setText(String.format("€%.2f", product.getPrix()));
        textViewAvailability.setText(product.getQuantite() > 0 ? "En stock" : "Rupture de stock");
        textViewQuantity.setText(String.valueOf(quantity));
        textViewStockInfo.setText("Stock: " + product.getQuantite());
        textViewDescription.setText(product.getDescription() != null ? product.getDescription() : "Pas de description");
        textViewWeight.setText("Poids: Non spécifié"); // À ajuster selon les données réelles
        textViewDimensions.setText("Dimensions: Non spécifiées");
        textViewColor.setText("Couleur: Non spécifiée");
        textViewReference.setText("Référence: PROD" + product.getId());

        // Charger les images avec Glide
        if (product.getImage1() != null && !product.getImage1().isEmpty()) {
            int imageResId = getResources().getIdentifier(product.getImage1(), "drawable", getPackageName());
            Log.d(TAG, "Loading image1: " + product.getImage1() + ", resId: " + imageResId);
            Glide.with(this)
                    .load(imageResId)
                    .placeholder(R.drawable.image_not_found)
                    .error(R.drawable.image_not_found)
                    .into(imageViewProduct);
        } else {
            imageViewProduct.setImageResource(R.drawable.image_not_found);
        }

        if (product.getImage2() != null && !product.getImage2().isEmpty()) {
            int imageResId = getResources().getIdentifier(product.getImage2(), "drawable", getPackageName());
            Log.d(TAG, "Loading image2: " + product.getImage2() + ", resId: " + imageResId);
            Glide.with(this)
                    .load(imageResId)
                    .placeholder(R.drawable.image_not_found)
                    .error(R.drawable.image_not_found)
                    .into(imageViewSecondary1);
        } else {
            imageViewSecondary1.setVisibility(View.GONE);
        }

        if (product.getImage3() != null && !product.getImage3().isEmpty()) {
            int imageResId = getResources().getIdentifier(product.getImage3(), "drawable", getPackageName());
            Log.d(TAG, "Loading image3: " + product.getImage3() + ", resId: " + imageResId);
            Glide.with(this)
                    .load(imageResId)
                    .placeholder(R.drawable.image_not_found)
                    .error(R.drawable.image_not_found)
                    .into(imageViewSecondary2);
        } else {
            imageViewSecondary2.setVisibility(View.GONE);
        }

        // Gestion des boutons de quantité
        buttonMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                textViewQuantity.setText(String.valueOf(quantity));
            }
        });

        buttonPlus.setOnClickListener(v -> {
            if (quantity < product.getQuantite()) {
                quantity++;
                textViewQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Quantité maximale en stock atteinte", Toast.LENGTH_SHORT).show();
            }
        });

        // Gestion du bouton "Ajouter au panier"
        buttonAddToCart.setOnClickListener(v -> {
            // TODO: Implémenter la logique d'ajout au panier
            Toast.makeText(this, "Produit ajouté au panier: " + quantity + " x " + product.getNom(), Toast.LENGTH_SHORT).show();
            // Exemple: dbHelper.ajouterArticlePanier(new ArticlePanier(...));
        });
    }
}