package com.example.shop.Activities;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Produits;
import com.example.shop.R;

import java.io.File;

public class ProductDetailActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private TextView textViewName, textViewDescription, textViewPrice, textViewQuantity;
    private ImageView imageViewProduct;
    private Button btnAddToCart;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_produit);

        dbHelper = new DBHelper(this);
        textViewName = findViewById(R.id.productName);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewQuantity = findViewById(R.id.textViewQuantity);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        btnAddToCart = findViewById(R.id.btnAjouterCategorie);

        int productId = getIntent().getIntExtra("product_id", -1);
        Produits produit = dbHelper.getProductById(productId); // Ajouter cette méthode dans DBHelper

        if (produit != null) {
            textViewName.setText(produit.getNom());
            textViewDescription.setText(produit.getDescription());
            textViewPrice.setText(String.format("%.2f €", produit.getPrix()));
            textViewQuantity.setText("Quantité : " + produit.getQuantite());

            loadImage(imageViewProduct, produit.getImage1());



            btnAddToCart.setOnClickListener(v -> {
                // TODO: Implémenter l'ajout au panier
                Toast.makeText(this, "Produit ajouté au panier", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "Produit non trouvé", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadImage(ImageView imageView, String imageSource) {
        if (imageSource == null || imageSource.isEmpty()) {
            imageView.setImageResource(R.drawable.image_not_found);
            imageView.setVisibility(View.GONE);
            Log.d(TAG, "Image source is null or empty");
            return;
        }

        imageView.setVisibility(View.VISIBLE);
        Log.d(TAG, "Loading image: " + imageSource);

        // Handle drawable resources
        if (imageSource.startsWith("drawable://")) {
            String drawableName = imageSource.replace("drawable://", "");
            int resourceId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
            if (resourceId != 0) {
                Glide.with(this)
                        .load(resourceId)
                        .placeholder(R.drawable.notification_icon)
                        .error(R.drawable.image_not_found)
                        .listener(new RequestListener<>() {
                            @Override
                            public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e(TAG, "Failed to load drawable: " + drawableName + ", Error: " + (e != null ? e.getMessage() : "Unknown"));
                                imageView.setImageResource(R.drawable.image_not_found);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d(TAG, "Drawable loaded successfully: " + drawableName);
                                return false;
                            }
                        })
                        .into(imageView);
            } else {
                Log.e(TAG, "Drawable resource not found: " + drawableName);
                imageView.setImageResource(R.drawable.image_not_found);
            }
            return;
        }

        // Handle URLs and URIs
        Object glideSource;
        if (imageSource.startsWith("http://") || imageSource.startsWith("https://")) {
            glideSource = imageSource;
        } else if (imageSource.startsWith("file://")) {
            glideSource = android.net.Uri.parse(imageSource);
        } else {
            // Assume it's a local file path (not recommended, but for backward compatibility)
            File file = new File(imageSource);
            if (!file.exists()) {
                Log.e(TAG, "File does not exist: " + imageSource);
                imageView.setImageResource(R.drawable.image_not_found);
                return;
            }
            glideSource = android.net.Uri.fromFile(file);
        }

        Glide.with(this)
                .load(glideSource)
                .placeholder(R.drawable.notification_icon)
                .error(R.drawable.image_not_found)
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Failed to load image: " + imageSource + ", Error: " + (e != null ? e.getMessage() : "Unknown"));
                        imageView.setImageResource(R.drawable.image_not_found);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Image loaded successfully: " + imageSource);
                        return false;
                    }
                })
                .into(imageView);
    }

}