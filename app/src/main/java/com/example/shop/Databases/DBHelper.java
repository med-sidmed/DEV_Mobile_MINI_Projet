package com.example.shop.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.shop.Models.Categories;
import com.example.shop.Models.Panier;
import com.example.shop.Models.Produits;
import com.example.shop.Models.Users;
import com.example.shop.Models.ArticlePanier;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "shop.db";
    private static final int DATABASE_VERSION = 4;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProductsTable = "CREATE TABLE Products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "image1 TEXT, " +
                "image2 TEXT, " +
                "image3 TEXT, " +
                "image4 TEXT, " +
                "quantity INTEGER NOT NULL, " +
                "date_added TEXT NOT NULL, " +
                "date_modified TEXT, " +
                "is_active INTEGER NOT NULL, " +
                "category_id INTEGER NOT NULL, " +
                "user_id INTEGER NOT NULL)";
        db.execSQL(createProductsTable);

        String createCategoriesTable = "CREATE TABLE Categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT)";
        db.execSQL(createCategoriesTable);

        String CREATE_TABLE_PANIER = "CREATE TABLE panier (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "utilisateur_id INTEGER, " +
                "date_ajout TEXT, " +
                "date_modification TEXT, " +
                "is_active INTEGER, " +
                "FOREIGN KEY(utilisateur_id) REFERENCES users(id))";
        db.execSQL(CREATE_TABLE_PANIER);

        String CREATE_TABLE_ARTICLE_PANIER = "CREATE TABLE article_panier (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "produit_id INTEGER, " +
                "panier_id INTEGER, " +
                "quantite INTEGER, " +
                "prix_unitaire REAL, " +
                "prix_total REAL, " +
                "date_ajout TEXT, " +
                "date_modification TEXT, " +
                "is_active INTEGER, " +
                "FOREIGN KEY(produit_id) REFERENCES Products(id), " +
                "FOREIGN KEY(panier_id) REFERENCES panier(id))";
        db.execSQL(CREATE_TABLE_ARTICLE_PANIER);

        String CREATE_TABLE_USERS = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nom TEXT, " +
                "prenom TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "role TEXT, " +
                "adresse TEXT, " +
                "telephone TEXT, " +
                "ville TEXT, " +
                "codePostal TEXT, " +
                "pays TEXT, " +
                "image TEXT, " +
                "dateInscription TEXT)";
        db.execSQL(CREATE_TABLE_USERS);

        String CREATE_TABLE_FAVORITES = "CREATE TABLE Favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "date_added TEXT NOT NULL, " +
                "is_active INTEGER NOT NULL DEFAULT 1, " +
                "FOREIGN KEY(user_id) REFERENCES users(id), " +
                "FOREIGN KEY(product_id) REFERENCES Products(id))";
        db.execSQL(CREATE_TABLE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            // Ajouter la table Favorites si la version est inférieure à 4
            String CREATE_TABLE_FAVORITES = "CREATE TABLE Favorites (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "product_id INTEGER NOT NULL, " +
                    "date_added TEXT NOT NULL, " +
                    "is_active INTEGER NOT NULL DEFAULT 1, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id), " +
                    "FOREIGN KEY(product_id) REFERENCES Products(id))";
            db.execSQL(CREATE_TABLE_FAVORITES);
        }
        // Si d'autres modifications sont nécessaires à l'avenir, elles peuvent être ajoutées ici
    }

    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    // Méthode pour récupérer les produits favoris d'
    public List<Produits> getFavoriteProducts(int userId) {
        List<Produits> favoriteProducts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT p.* FROM Products p " +
                    "INNER JOIN Favorites f ON p.id = f.product_id " +
                    "WHERE f.user_id = ? AND f.is_active = 1 AND p.is_active = 1";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
            Log.d(TAG, "Nombre de produits favoris trouvés pour user_id " + userId + ": " + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    Produits p = new Produits();
                    p.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    p.setNom(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    p.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    p.setPrix(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                    p.setQuantite(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                    p.setImage1(cursor.getString(cursor.getColumnIndexOrThrow("image1")));
                    p.setImage2(cursor.getString(cursor.getColumnIndexOrThrow("image2")));
                    p.setImage3(cursor.getString(cursor.getColumnIndexOrThrow("image3")));
                    p.setImage4(cursor.getString(cursor.getColumnIndexOrThrow("image4")));
                    p.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);
                    p.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                    p.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                    favoriteProducts.add(p);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la récupération des produits favoris: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return favoriteProducts;
    }

    // Méthode pour ajouter un produit aux favoris
    public boolean addFavorite(int userId, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Vérifier si le favori existe déjà
            Cursor cursor = db.rawQuery("SELECT id FROM Favorites WHERE user_id = ? AND product_id = ? AND is_active = 1",
                    new String[]{String.valueOf(userId), String.valueOf(productId)});
            if (cursor.moveToFirst()) {
                cursor.close();
                Log.d(TAG, "Produit " + productId + " déjà dans les favoris de l'utilisateur " + userId);
                return false; // Favori déjà existant
            }
            cursor.close();

            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("product_id", productId);
            values.put("date_added", getCurrentDate());
            values.put("is_active", 1);

            long result = db.insertOrThrow("Favorites", null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "Favori ajouté: user_id=" + userId + ", product_id=" + productId);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'ajout du favori: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Méthode pour supprimer un produit des favoris
    public boolean removeFavorite(int userId, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            int rowsAffected = db.delete("Favorites", "user_id = ? AND product_id = ?",
                    new String[]{String.valueOf(userId), String.valueOf(productId)});
            db.setTransactionSuccessful();
            Log.d(TAG, "Favori supprimé: user_id=" + userId + ", product_id=" + productId + ", rowsAffected=" + rowsAffected);
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la suppression du favori: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Les autres méthodes existantes (non modifiées) sont omises pour brevité
    // Inclure ici les méthodes comme insertProduct, updateProduct, deleteProduct, etc.

    public boolean insertProduct(String name, String description, double price, String image1, String image2,
                                 String image3, String image4, int quantity, int categoryId, int userId, boolean isActive) {
        if (name == null || name.trim().isEmpty()) {
            Log.e(TAG, "Product name cannot be empty");
            return false;
        }
        if (price < 0) {
            Log.e(TAG, "Price cannot be negative");
            return false;
        }
        if (quantity < 0) {
            Log.e(TAG, "Quantity cannot be negative");
            return false;
        }
        // Vérifier si categoryId et userId existent
        if (!categoryExists(categoryId)) {
            Log.e(TAG, "Category ID does not exist");
            return false;
        }
        if (!userExists(userId)) {
            Log.e(TAG, "User ID does not exist");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("description", description);
            values.put("price", price);
            values.put("image1", image1);
            values.put("image2", image2);
            values.put("image3", image3);
            values.put("image4", image4);
            values.put("quantity", quantity);
            values.put("category_id", categoryId);
            values.put("user_id", userId);
            values.put("is_active", isActive ? 1 : 0);
            values.put("date_added", getCurrentDate());
            values.put("date_modified", getCurrentDate());

            long result = db.insertOrThrow("Products", null, values);
            db.setTransactionSuccessful();
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting product: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private boolean categoryExists(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM Categories WHERE id = ?", new String[]{String.valueOf(categoryId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    private boolean userExists(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }
    public boolean updateProduct(int id, String name, String description, double price,
                                 String image1, String image2, String image3, String image4,
                                 int quantity, long categoryId, long userId, boolean isActive) {
        Log.d("DBHelper", "Updating product ID: " + id + ", Name: " + name);
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("description", description);
            values.put("price", price);
            values.put("image1", image1);
            values.put("image2", image2);
            values.put("image3", image3);
            values.put("image4", image4);
            values.put("quantity", quantity);
            values.put("date_modified", getCurrentDate());
            values.put("is_active", isActive ? 1 : 0);
            values.put("category_id", categoryId);
            values.put("user_id", userId);

            int rowsAffected = db.update("Products", values, "id = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            Log.d("DBHelper", "Update rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("DBHelper", "Error updating product: " + e.getMessage(), e);
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            int rowsAffected = db.delete("Products", "id = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting product: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Produits> getAllProducts() {
        List<Produits> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM Products WHERE is_active = 1", null);
            if (cursor.moveToFirst()) {
                do {
                    Produits p = new Produits();
                    p.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    p.setNom(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    p.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    p.setPrix(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                    p.setQuantite(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                    p.setImage1(cursor.getString(cursor.getColumnIndexOrThrow("image1")));
                    p.setImage2(cursor.getString(cursor.getColumnIndexOrThrow("image2")));
                    p.setImage3(cursor.getString(cursor.getColumnIndexOrThrow("image3")));
                    p.setImage4(cursor.getString(cursor.getColumnIndexOrThrow("image4")));
                    p.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);
                    p.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                    p.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                    products.add(p);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching products: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return products;
    }

    public boolean insertCategory(String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("description", description);

            long result = db.insertOrThrow("Categories", null, values);
            db.setTransactionSuccessful();
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting category: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Categories> getCategoriesByNom(String nom) {
        List<Categories> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT name, description FROM Categories WHERE name LIKE ?",
                    new String[]{"%" + nom + "%"});
            if (cursor.moveToFirst()) {
                do {
                    Categories categorie = new Categories();
                    categorie.setNom(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    categorie.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    categories.add(categorie);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching categories by name: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return categories;
    }

    public Integer getIdCategoriesByNom(String nom) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Integer categoryId = null;
        try {
            cursor = db.rawQuery("SELECT id FROM Categories WHERE name LIKE ?",
                    new String[]{"%" + nom + "%"});
            if (cursor.moveToFirst()) {
                categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            }
        } catch (Exception e) {
            InputStreamReader(TAG, "Error fetching category ID by name: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return categoryId;
    }

    private void InputStreamReader(String tag, String s) {
    }

    public Categories GetCategorieByType(String type) {
        List<Categories> categories = getCategoriesByNom(type);
        return categories.isEmpty() ? null : categories.get(0);
    }

    public List<Categories> getAllCategories() {
        List<Categories> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM Categories", null);
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    categories.add(new Categories(id, name, description));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching categories: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return categories;
    }

    public boolean updateCategory(int id, String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("description", description);

            int rowsAffected = db.update("Categories", values, "id = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating category: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Products WHERE category_id = ?", new String[]{String.valueOf(id)});
            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                cursor.close();
                Log.w(TAG, "Cannot delete category ID " + id + " because it has associated products");
                return false;
            }
            cursor.close();

            int rowsAffected = db.delete("Categories", "id = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting category: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public long ajouterPanier(Panier panier) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("utilisateur_id", panier.getUtilisateur().getId());
            values.put("date_ajout", panier.getDateAjout());
            values.put("date_modification", panier.getDateModification());
            values.put("is_active", panier.isActive() ? 1 : 0);

            long id = db.insertOrThrow("panier", null, values);
            db.setTransactionSuccessful();
            return id;
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'ajout du panier: " + e.getMessage());
            return -1;
        } finally {
            db.endTransaction();
            db.close();
        }
    }
    public boolean supprimerPanier(int panierId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int rowsAffected = db.delete("panier", "id = ?", new String[]{String.valueOf(panierId)});
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public List<Panier> getAllPaniers() {
        List<Panier> paniers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM panier WHERE is_active = 1", null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Panier panier = new Panier();
                    panier.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    panier.setUtilisateur(new Users(cursor.getInt(cursor.getColumnIndexOrThrow("utilisateur_id"))));
                    panier.setDateAjout(cursor.getString(cursor.getColumnIndexOrThrow("date_ajout")));
                    panier.setDateModification(cursor.getString(cursor.getColumnIndexOrThrow("date_modification")));
                    panier.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);
                    paniers.add(panier);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return paniers;
    }


    public boolean ajouterArticlePanier(ArticlePanier article) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("produit_id", article.getProduit().getId());
            values.put("panier_id", article.getPanier().getId());
            values.put("quantite", article.getQuantite());
            values.put("prix_unitaire", article.getPrixUnitaire());
            values.put("prix_total", article.getPrixTotal());
            values.put("date_ajout", article.getDateAjout());
            values.put("date_modification", article.getDateModification());
            values.put("is_active", article.isActive() ? 1 : 0);

            long result = db.insertOrThrow("article_panier", null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "Article added to panier: produit_id=" + article.getProduit().getId() + ", panier_id=" + article.getPanier().getId());
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'ajout de l'article au panier: " + e.getMessage(), e);
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    public void ajouterUtilisateur(Users user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nom", user.getNom());
        values.put("prenom", user.getPrenom());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("role", user.getRole());
        values.put("adresse", user.getAdresse());
        values.put("telephone", user.getTelephone());
        values.put("ville", user.getVille());
        values.put("codePostal", user.getCodePostal());
        values.put("pays", user.getPays());
        values.put("image", user.getImage());
        values.put("dateInscription", user.getDateInscription());

        db.insert("users", null, values);
        db.close();
    }

    public Users getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", null, "email=?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Users user = new Users();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setNom(cursor.getString(cursor.getColumnIndex("nom")));
            user.setPrenom(cursor.getString(cursor.getColumnIndex("prenom")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setRole(cursor.getString(cursor.getColumnIndex("role")));
            user.setAdresse(cursor.getString(cursor.getColumnIndex("adresse")));
            user.setTelephone(cursor.getString(cursor.getColumnIndex("telephone")));
            user.setVille(cursor.getString(cursor.getColumnIndex("ville")));
            user.setCodePostal(cursor.getString(cursor.getColumnIndex("codePostal")));
            user.setPays(cursor.getString(cursor.getColumnIndex("pays")));
            user.setImage(cursor.getString(cursor.getColumnIndex("image")));
            user.setDateInscription(cursor.getString(cursor.getColumnIndex("dateInscription")));
            cursor.close();
            return user;
        }
        return null;
    }

    public List<Produits> getProductsByCategory(int categoryId) {
        List<Produits> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM Products WHERE is_active = 1 AND category_id = ?",
                    new String[]{String.valueOf(categoryId)});
            if (cursor.moveToFirst()) {
                do {
                    Produits p = new Produits();
                    p.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    p.setNom(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    p.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    p.setPrix(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                    p.setQuantite(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                    p.setImage1(cursor.getString(cursor.getColumnIndexOrThrow("image1")));
                    p.setImage2(cursor.getString(cursor.getColumnIndexOrThrow("image2")));
                    p.setImage3(cursor.getString(cursor.getColumnIndexOrThrow("image3")));
                    p.setImage4(cursor.getString(cursor.getColumnIndexOrThrow("image4")));
                    p.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);
                    p.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                    p.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                    products.add(p);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching products by category: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return products;
    }

    public List<Produits> searchProducts(String query) {
        List<Produits> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM Products WHERE is_active = 1 AND name LIKE ?",
                    new String[]{"%" + query + "%"});
            if (cursor.moveToFirst()) {
                do {
                    Produits p = new Produits();
                    p.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    p.setNom(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    p.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    p.setPrix(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                    p.setQuantite(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                    p.setImage1(cursor.getString(cursor.getColumnIndexOrThrow("image1")));
                    p.setImage2(cursor.getString(cursor.getColumnIndexOrThrow("image2")));
                    p.setImage3(cursor.getString(cursor.getColumnIndexOrThrow("image3")));
                    p.setImage4(cursor.getString(cursor.getColumnIndexOrThrow("image4")));
                    p.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);
                    p.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                    p.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                    products.add(p);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching products: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return products;
    }

    public Categories getCategoryById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Categories category = null;

        try {
            cursor = db.rawQuery("SELECT id, nom, description FROM Categories WHERE id = ?",
                    new String[]{String.valueOf(id)});

            if (cursor != null && cursor.moveToFirst()) {
                category = new Categories(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nom")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description"))
                );
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching category by id " + id + ": " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Do not close db; let SQLiteOpenHelper manage it
        }

        return category;
    }


    public Produits getProductById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Produits produit = null;
        try {
            cursor = db.rawQuery("SELECT * FROM Products WHERE id = ? AND is_active = 1",
                    new String[]{String.valueOf(productId)});
            if (cursor.moveToFirst()) {
                produit = new Produits();
                produit.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                produit.setNom(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                produit.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                produit.setPrix(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                produit.setQuantite(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                produit.setImage1(cursor.getString(cursor.getColumnIndexOrThrow("image1")));
                produit.setImage2(cursor.getString(cursor.getColumnIndexOrThrow("image2")));
                produit.setImage3(cursor.getString(cursor.getColumnIndexOrThrow("image3")));
                produit.setImage4(cursor.getString(cursor.getColumnIndexOrThrow("image4")));
                produit.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);
                produit.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                produit.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la récupération du produit: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return produit;
    }


    public boolean isFavorite(int userId, int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT id FROM Favorites WHERE user_id = ? AND product_id = ? AND is_active = 1",
                    new String[]{String.valueOf(userId), String.valueOf(productId)});
            boolean isFavorite = cursor.moveToFirst();
            Log.d(TAG, "Produit " + productId + " est favori pour user_id " + userId + ": " + isFavorite);
            return isFavorite;
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la vérification du favori: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }



    public Users getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", null, "id=?", new String[]{String.valueOf(userId)}, null, null, null);
        Users user = null;
        try {
            if (cursor != null && cursor.moveToFirst()) {
                user = new Users();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                user.setNom(cursor.getString(cursor.getColumnIndexOrThrow("nom")));
                user.setPrenom(cursor.getString(cursor.getColumnIndexOrThrow("prenom")));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
                user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
                user.setAdresse(cursor.getString(cursor.getColumnIndexOrThrow("adresse")));
                user.setTelephone(cursor.getString(cursor.getColumnIndexOrThrow("telephone")));
                user.setVille(cursor.getString(cursor.getColumnIndexOrThrow("ville")));
                user.setCodePostal(cursor.getString(cursor.getColumnIndexOrThrow("codePostal")));
                user.setPays(cursor.getString(cursor.getColumnIndexOrThrow("pays")));
                user.setImage(cursor.getString(cursor.getColumnIndexOrThrow("image")));
                user.setDateInscription(cursor.getString(cursor.getColumnIndexOrThrow("dateInscription")));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching user by ID: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return user;
    }

    public List<ArticlePanier> getArticlesByPanierId(int panierId) {
        List<ArticlePanier> articles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM article_panier WHERE panier_id = ? AND is_active = 1",
                    new String[]{String.valueOf(panierId)});
            Log.d(TAG, "Nombre d'articles trouvés pour panier_id " + panierId + ": " + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    ArticlePanier article = new ArticlePanier();
                    article.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    article.setProduit(getProductById(cursor.getInt(cursor.getColumnIndexOrThrow("produit_id"))));
                    article.setPanier(new Panier(cursor.getInt(cursor.getColumnIndexOrThrow("panier_id"))));
                    article.setQuantite(cursor.getInt(cursor.getColumnIndexOrThrow("quantite")));
                    article.setPrixUnitaire(cursor.getDouble(cursor.getColumnIndexOrThrow("prix_unitaire")));
                    article.setPrixTotal(cursor.getDouble(cursor.getColumnIndexOrThrow("prix_total")));
                    article.setDateAjout(cursor.getString(cursor.getColumnIndexOrThrow("date_ajout")));
                    article.setDateModification(cursor.getString(cursor.getColumnIndexOrThrow("date_modification")));
                    article.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);
                    articles.add(article);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching articles by panier ID: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return articles;
    }
    public boolean supprimerArticlePanier(int articleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            int rowsAffected = db.delete("article_panier", "id = ?", new String[]{String.valueOf(articleId)});
            db.setTransactionSuccessful();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la suppression de l'article du panier: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

}