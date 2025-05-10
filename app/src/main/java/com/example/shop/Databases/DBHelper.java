package com.example.shop.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.shop.Models.Categories;
import com.example.shop.Models.Produits;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shop.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TAG = "DBHelper";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys = ON;");

        // Create Users table
        String sqlUsers = "CREATE TABLE Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "email TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "created_at TEXT NOT NULL," +
                "is_active INTEGER NOT NULL DEFAULT 1" +
                ");";
        db.execSQL(sqlUsers);

        // Create Categories table
        String sqlCategories = "CREATE TABLE Categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL UNIQUE," +
                "description TEXT" +
                ");";
        db.execSQL(sqlCategories);

        // Create Products table
        String sqlProducts = "CREATE TABLE Products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "price REAL NOT NULL CHECK(price >= 0)," +
                "image1 TEXT," +
                "image2 TEXT," +
                "image3 TEXT," +
                "image4 TEXT," +
                "quantity INTEGER NOT NULL CHECK(quantity >= 0)," +
                "date_added TEXT NOT NULL," +
                "date_modified TEXT," +
                "is_active INTEGER NOT NULL DEFAULT 1," +
                "category_id INTEGER NOT NULL," +
                "user_id INTEGER NOT NULL," +
                "FOREIGN KEY (category_id) REFERENCES Categories(id) ON DELETE RESTRICT," +
                "FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE RESTRICT" +
                ");";
        db.execSQL(sqlProducts);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS Products");
            db.execSQL("DROP TABLE IF EXISTS Categories");
            db.execSQL("DROP TABLE IF EXISTS Users");
            onCreate(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // Product Operations
    public boolean insertProduct(String name, String description, double price,
                                 String image1, String image2, String image3, String image4,
                                 int quantity, long categoryId, long userId, boolean isActive) {
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
            values.put("date_added", getCurrentDate());
            values.put("date_modified", (String) null);
            values.put("is_active", isActive ? 1 : 0);
            values.put("category_id", categoryId);
            values.put("user_id", userId);

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

    public boolean updateProduct(long id, String name, String description, double price,
                                 String image1, String image2, String image3, String image4,
                                 int quantity, long categoryId, long userId, boolean isActive) {
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

            int rows = db.update("Products", values, "id = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating product: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean deleteProduct(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            int rows = db.delete("Products", "id = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting product: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Category Operations
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

    public boolean updateCategory(int id, String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("description", description);

            int rows = db.update("Categories", values, "id = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            return rows > 0;
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
            int rows = db.delete("Categories", "id = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting category: " + e.getMessage());
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
                    p.setPrix((float) cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                    p.setQuantite(cursor0(cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))));
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

    private int cursor0(int quantity) {
        return 0;
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

    // User Operations
    public boolean insertUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("email", email);
            values.put("password", password); // Note: In production, hash the password
            values.put("created_at", getCurrentDate());
            values.put("is_active", 1);

            long result = db.insertOrThrow("Users", null, values);
            db.setTransactionSuccessful();
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting user: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Utility method for current date
    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}