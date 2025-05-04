package com.example.shop.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "produits", null,1 );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Activer les clés étrangères
        db.execSQL("PRAGMA foreign_keys = ON;");

        String sql = "CREATE TABLE Produits (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    nom TEXT NOT NULL," +
                "    description TEXT," +
                "    prix REAL NOT NULL," +
                "    image1 TEXT," +
                "    image2 TEXT," +
                "    image3 TEXT," +
                "    image4 TEXT," +
                "    quantite INTEGER NOT NULL," +
                "    dateAjout TEXT NOT NULL," +
                "    dateModification TEXT," +
                "    isActive INTEGER NOT NULL DEFAULT 1," +
                "    categorie_id INTEGER," +
                "    utilisateur_id INTEGER," +
                "    FOREIGN KEY (categorie_id) REFERENCES Categories(id)," +
                "    FOREIGN KEY (utilisateur_id) REFERENCES Users(id)" +
                ");";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists produits");
    }


    public long insert_produit(String nom, String description, double prix,
                               String image1, String image2, String image3, String image4,
                               int quantite, long categorie_id, long utilisateur_id, boolean isActive) {
        // Obtenir une instance de la base de données en mode écriture
        SQLiteDatabase db = this.getWritableDatabase();

        // Activer les clés étrangères
        db.execSQL("PRAGMA foreign_keys = ON;");

        // Créer un objet ContentValues pour stocker les valeurs
        ContentValues values = new ContentValues();

        // Ajouter les valeurs des colonnes
        values.put("nom", nom);
        values.put("description", description);
        values.put("prix", prix);
        values.put("image1", image1);
        values.put("image2", image2);
        values.put("image3", image3);
        values.put("image4", image4);
        values.put("quantite", quantite);

        // Formater la date actuelle pour dateAjout (format YYYY-MM-DD)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        values.put("dateAjout", currentDate);

        // dateModification peut être null lors de l'insertion
        values.putNull("dateModification");

        // isActive (1 pour true, 0 pour false)
        values.put("isActive", isActive ? 1 : 0);

        // Ajouter les IDs des clés étrangères
        values.put("categorie_id", categorie_id);
        values.put("utilisateur_id", utilisateur_id);

        // Insérer la ligne dans la table Produits
        long newRowId = db.insert("Produits", null, values);

        // Fermer la connexion à la base de données
        db.close();

        // Retourner l'ID de la nouvelle ligne insérée (ou -1 en cas d'échec)
        return newRowId;
    }




    // Méthode pour mettre à jour un produit
    public int update_produit(long id, String nom, String description, double prix,
                              String image1, String image2, String image3, String image4,
                              int quantite, long categorie_id, long utilisateur_id, boolean isActive) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys = ON;");

        ContentValues values = new ContentValues();
        values.put("nom", nom);
        values.put("description", description);
        values.put("prix", prix);
        values.put("image1", image1);
        values.put("image2", image2);
        values.put("image3", image3);
        values.put("image4", image4);
        values.put("quantite", quantite);

        // Mettre à jour dateModification avec la date actuelle
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        values.put("dateModification", currentDate);

        values.put("isActive", isActive ? 1 : 0);
        values.put("categorie_id", categorie_id);
        values.put("utilisateur_id", utilisateur_id);

        // Mettre à jour la ligne correspondant à l'ID
        int rowsAffected = db.update("Produits", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    // Méthode pour supprimer un produit
    public int delete_produit(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys = ON;");

        // Supprimer la ligne correspondant à l'ID
        int rowsAffected = db.delete("Produits", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }


}
