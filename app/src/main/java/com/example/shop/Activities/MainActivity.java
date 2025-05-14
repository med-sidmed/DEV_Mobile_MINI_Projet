package com.example.shop.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.Activities.ControlPannel.Controller;
import com.example.shop.Adapter.ProduitAdapter;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Categories;
import com.example.shop.Models.Produits;
import com.example.shop.R;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductMainAdapter.OnWishlistClickListener {

    private ImageView profile_page, cart_page, wishlest_page, btnGoToSecond, controllerpage, testapi, CategorieCoothes;
    private RecyclerView recyclerProducts;
    private ProductMainAdapter productAdapter;
    private LinearLayout tools,viewAll;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialiser DBHelper
        dbHelper = new DBHelper(this);


        // Initialiser les vues
        btnGoToSecond = findViewById(R.id.btnGoToSecond);
        wishlest_page = findViewById(R.id.wishlest_page);
        cart_page = findViewById(R.id.cart_page);
        profile_page = findViewById(R.id.profile_page);
        controllerpage = findViewById(R.id.controllerpage);
        testapi = findViewById(R.id.testapi);


//        les Categories
        CategorieCoothes = findViewById(R.id.categorieClothes);
        recyclerProducts = findViewById(R.id.recyclerProducts);
        tools=findViewById(R.id.tools);
        viewAll =findViewById(R.id.ViewAll);

        // Configurer le RecyclerView
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2)); // 2 colonnes
        List<Produits> produitsList = dbHelper.getAllProducts();
        if (produitsList == null || produitsList.isEmpty()) {
            Toast.makeText(this, "Aucun produit trouvé", Toast.LENGTH_SHORT).show();
        }
        productAdapter = new ProductMainAdapter(produitsList, this, this);
        recyclerProducts.setAdapter(productAdapter);

        // Gestion des clics sur les boutons de navigation
        profile_page.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        CategorieCoothes.setOnClickListener(v -> {
            // Récupérer l'ID de la catégorie "Vêtements"
            List<Categories> categories = dbHelper.getCategoriesByNom("Vêtements");
            if (!categories.isEmpty()) {
                Categories categorie = categories.get(0); // Prendre la première correspondance
                Intent intent = new Intent(MainActivity.this, ProductsByCategoryActivity.class);
                intent.putExtra("category_id", categorie.getId());
                intent.putExtra("category_name", categorie.getNom());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Catégorie 'Vêtements' non trouvée", Toast.LENGTH_SHORT).show();
            }
        });

        tools.setOnClickListener(v -> {
            // Récupérer l'ID de la catégorie "Vêtements"
            List<Categories> categories = dbHelper.getCategoriesByNom("tools");
            if (!categories.isEmpty()) {
                Categories categorie = categories.get(0); // Prendre la première correspondance
                Intent intent = new Intent(MainActivity.this, ProductsByCategoryActivity.class);
                intent.putExtra("category_id", categorie.getId());
                intent.putExtra("category_name", categorie.getNom());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Catégorie 'Tools' non trouvée", Toast.LENGTH_SHORT).show();
            }
        });

        viewAll.setOnClickListener(v -> {
             List<Categories> categories = dbHelper.getAllCategories();
            if (!categories.isEmpty()) {
                Categories categorie = categories.get(0); // Prendre la première correspondance
                Intent intent = new Intent(MainActivity.this, ProductsByCategoryActivity.class);
                intent.putExtra("category_id", categorie.getId());
                intent.putExtra("category_name", categorie.getNom());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Catégorie  non trouvée", Toast.LENGTH_SHORT).show();
            }
        });


        controllerpage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Controller.class);
            startActivity(intent);
        });

        btnGoToSecond.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Login_Page.class);
            startActivity(intent);
        });

        wishlest_page.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WishlistActivity.class);
            startActivity(intent);
        });

        cart_page.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CardActivity.class);
            startActivity(intent);
        });

        testapi.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TestConneectApI.class);
            startActivity(intent);
        });
    }

    @Override
    public void onWishlistClick(Produits produit, boolean isAdded) {
        // TODO: Implémenter la logique pour ajouter/supprimer le produit de la liste de souhaits
        Toast.makeText(this, isAdded ? "Ajouté à la liste de souhaits" : "Supprimé de la liste de souhaits", Toast.LENGTH_SHORT).show();
    }















}