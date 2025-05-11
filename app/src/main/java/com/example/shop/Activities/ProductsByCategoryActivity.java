package com.example.shop.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.Adapter.ProduitAdapter;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Produits;
import com.example.shop.R;

import java.util.List;

public class ProductsByCategoryActivity extends AppCompatActivity implements ProductMainAdapter.OnWishlistClickListener {

    private RecyclerView recyclerProducts;
    private ProductMainAdapter productAdapter;
    private DBHelper dbHelper;
    private TextView textViewCategoryName;
    private int categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_by_category);

        // Initialiser DBHelper
        dbHelper = new DBHelper(this);

        // Récupérer les données de l'intent
        Intent intent = getIntent();
        categoryId = intent.getIntExtra("category_id", -1);
        categoryName = intent.getStringExtra("category_name");

        // Initialiser les vues
        textViewCategoryName = findViewById(R.id.textViewCategoryName);
        recyclerProducts = findViewById(R.id.recyclerProducts);

        // Afficher le nom de la catégorie
        textViewCategoryName.setText(categoryName != null ? categoryName : "Produits");

        // Configurer le RecyclerView
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));
        List<Produits> produitsList = dbHelper.getProductsByCategory(categoryId);
        if (produitsList == null || produitsList.isEmpty()) {
            Toast.makeText(this, "Aucun produit trouvé dans cette catégorie", Toast.LENGTH_SHORT).show();
        }
        productAdapter = new ProductMainAdapter(produitsList, this, this);
        recyclerProducts.setAdapter(productAdapter);
    }

    @Override
    public void onWishlistClick(Produits produit, boolean isAdded) {
        Toast.makeText(this, isAdded ? "Ajouté à la liste de souhaits" : "Supprimé de la liste de souhaits", Toast.LENGTH_SHORT).show();
    }
}