package com.example.shop.API;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;


import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.ArticlePanier;
import com.example.shop.Models.Categories;
import com.example.shop.Models.Favorites;
import com.example.shop.Models.Panier;
import com.example.shop.Models.Produits;
import com.example.shop.Models.Users;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiSyncManager {
    private static final String TAG = "ApiSyncManager";
    private final DBHelper dbHelper;
    private final ApiService apiService;
    private final SharedPreferences prefs;

    public interface SyncCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public ApiSyncManager(Context context) {
        dbHelper = new DBHelper(context);
        apiService = ApiClient.getApiService(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void synchronizeData(SyncCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Sync users
                    syncUsers();
                    // Sync categories
                    syncCategories();
                    // Sync products
                    syncProducts();
                    // Sync paniers
                    syncPaniers();
                    // Sync article paniers
                    syncArticlePaniers();
                    // Sync favorites
                    syncFavorites();
                    // Clean orphaned cart items
                    cleanOrphanedCartItems();
                    return "Synchronisation terminée avec succès";
                } catch (Exception e) {
                    Log.e(TAG, "Synchronisation échouée : " + e.getMessage());
                    return "Erreur : " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result.startsWith("Erreur")) {
                    callback.onFailure(result);
                } else {
                    callback.onSuccess(result);
                }
            }
        }.execute();
    }

    private void syncUsers() {
        apiService.getAllUsers(getToken()).enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Users user : response.body()) {
                        dbHelper.ajouterUtilisateur(user);
                    }
                    prefs.edit().putString("last_user_sync", getCurrentDate()).apply();
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                Log.e(TAG, "Échec de la synchronisation des utilisateurs : " + t.getMessage());
            }
        });
    }

    private void syncCategories() {
        apiService.getAllCategories(getToken()).enqueue(new Callback<List<Categories>>() {
            @Override
            public void onResponse(Call<List<Categories>> call, Response<List<Categories>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Categories category : response.body()) {
                        dbHelper.insertCategory(category.getNom(), category.getDescription());
                    }
                    prefs.edit().putString("last_category_sync", getCurrentDate()).apply();
                }
            }

            @Override
            public void onFailure(Call<List<Categories>> call, Throwable t) {
                Log.e(TAG, "Échec de la synchronisation des catégories : " + t.getMessage());
            }
        });
    }

    private void syncProducts() {
        String lastSyncTime = prefs.getString("last_product_sync", "1970-01-01 00:00:00");
        apiService.getAllProducts(getToken()).enqueue(new Callback<List<Produits>>() {
            @Override
            public void onResponse(Call<List<Produits>> call, Response<List<Produits>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Produits product : response.body()) {
                        dbHelper.insertProduct(
                                product.getNom(),
                                product.getDescription(),
                                product.getPrix(),
                                product.getImage1(),
                                product.getImage2(),
                                product.getImage3(),
                                product.getImage4(),
                                product.getQuantite(),
                                product.getCategorie().getId(),
                                product.getUtilisateur().getId(),
                                product.isActive()
                        );
                    }
                    prefs.edit().putString("last_product_sync", getCurrentDate()).apply();
                }
            }

            @Override
            public void onFailure(Call<List<Produits>> call, Throwable t) {
                Log.e(TAG, "Échec de la synchronisation des produits : " + t.getMessage());
            }
        });
    }

    private void syncPaniers() {
        apiService.getAllPaniers(getToken()).enqueue(new Callback<List<Panier>>() {
            @Override
            public void onResponse(Call<List<Panier>> call, Response<List<Panier>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Panier panier : response.body()) {
                        dbHelper.ajouterPanier(panier);
                    }
                    prefs.edit().putString("last_panier_sync", getCurrentDate()).apply();
                }
            }

            @Override
            public void onFailure(Call<List<Panier>> call, Throwable t) {
                Log.e(TAG, "Échec de la synchronisation des paniers : " + t.getMessage());
            }
        });
    }

    private void syncArticlePaniers() {
        apiService.getAllArticlesPanier(getToken()).enqueue(new Callback<List<ArticlePanier>>() {
            @Override
            public void onResponse(Call<List<ArticlePanier>> call, Response<List<ArticlePanier>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (ArticlePanier article : response.body()) {
                        dbHelper.ajouterArticlePanier(article);
                    }
                    prefs.edit().putString("last_article_panier_sync", getCurrentDate()).apply();
                }
            }

            @Override
            public void onFailure(Call<List<ArticlePanier>> call, Throwable t) {
                Log.e(TAG, "Échec de la synchronisation des articles de panier : " + t.getMessage());
            }
        });
    }

    private void syncFavorites() {
        // Assuming user ID is stored in SharedPreferences after login
        int userId = prefs.getInt("user_id", -1);
        if (userId != -1) {
            apiService.getFavoriteProducts(getToken(), userId).enqueue(new Callback<List<Produits>>() {
                @Override
                public void onResponse(Call<List<Produits>> call, Response<List<Produits>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        for (Produits product : response.body()) {
                            dbHelper.addFavorite(userId, product.getId());
                        }
                        prefs.edit().putString("last_favorite_sync", getCurrentDate()).apply();
                    }
                }

                @Override
                public void onFailure(Call<List<Produits>> call, Throwable t) {
                    Log.e(TAG, "Échec de la synchronisation des favoris : " + t.getMessage());
                }
            });
        }
    }

    private void cleanOrphanedCartItems() {
        apiService.cleanOrphanedCartItems(getToken()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    dbHelper.cleanOrphanedCartItems();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Échec du nettoyage des articles orphelins : " + t.getMessage());
            }
        });
    }

    private String getToken() {
        return prefs.getString("jwt_token", "");
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}