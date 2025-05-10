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

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "shop.db";
    private static final int DATABASE_VERSION = 3;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Products");
        db.execSQL("DROP TABLE IF EXISTS Categories");
        onCreate(db);
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }

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

    public boolean updateProduct(int id, String name, String description, double price,
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

            int rowsAffected = db.update("Products", values, "id = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating product: " + e.getMessage());
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
}