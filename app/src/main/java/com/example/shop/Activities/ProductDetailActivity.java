package com.example.shop.Activities;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Produits;
import com.example.shop.R;

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

            if (produit.getImage1() != null && !produit.getImage1().isEmpty()) {
                Glide.with(this)
                        .load(produit.getImage1())
                        .placeholder(R.drawable.notification_icon)
                        .error(R.drawable.image_not_found)
                        .into(imageViewProduct);
            } else {
                imageViewProduct.setImageResource(R.drawable.image_not_found);
            }

            btnAddToCart.setOnClickListener(v -> {
                // TODO: Implémenter l'ajout au panier
                Toast.makeText(this, "Produit ajouté au panier", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "Produit non trouvé", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}