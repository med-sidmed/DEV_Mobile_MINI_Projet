package com.example.shop.API;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.ArticlePanier;
import com.example.shop.Models.Categories;
import com.example.shop.Models.Panier;
import com.example.shop.Models.Produits;
import com.example.shop.Models.Users;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class ApiSyncManager {
    private static final String TAG = "ApiSyncManager";
    private DBHelper dbHelper;
    private final ApiService apiService;
    private final SharedPreferences prefs;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Classes d'exceptions personnalisées
    public static class SyncException extends Exception {
        public SyncException(String message) {
            super(message);
        }

        public SyncException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class NetworkException extends SyncException {
        public NetworkException(String message) {
            super(message);
        }

        public NetworkException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class DatabaseException extends SyncException {
        public DatabaseException(String message) {
            super(message);
        }

        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ValidationException extends SyncException {
        public ValidationException(String message) {
            super(message);
        }
    }

    public interface SyncCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public ApiSyncManager(Context context) throws SyncException {
        try {
            // CORRECTION 1: Initialiser DBHelper avec gestion d'exception
            dbHelper = DBHelper.getInstance(context);
            if (dbHelper == null) {
                throw new DatabaseException("Impossible d'initialiser la base de données");
            }

            apiService = ApiClient.getApiService();
            if (apiService == null) {
                throw new NetworkException("Impossible d'initialiser le service API");
            }

            prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (prefs == null) {
                throw new SyncException("Impossible d'accéder aux préférences partagées");
            }
        } catch (Exception e) {
            throw new SyncException("Erreur lors de l'initialisation d'ApiSyncManager", e);
        }
    }

    public void synchronizeData(SyncCallback callback) {
        if (callback == null) {
            Log.e(TAG, "Callback ne peut pas être null");
            return;
        }

        executor.execute(() -> {
            try {
                Log.d(TAG, "Début de la synchronisation");
//                syncUsers();
                syncCategories();
                syncProducts();
                syncPaniers();
                syncArticlePaniers();
                syncFavorites();
                cleanOrphanedCartItems();
                Log.d(TAG, "Synchronisation terminée avec succès");
                mainHandler.post(() -> callback.onSuccess("Synchronisation terminée avec succès"));
            } catch (NetworkException e) {
                String errorMsg = "Erreur réseau lors de la synchronisation : " + e.getMessage();
                Log.e(TAG, errorMsg, e);
                mainHandler.post(() -> callback.onFailure(errorMsg));
            } catch (DatabaseException e) {
                String errorMsg = "Erreur de base de données : " + e.getMessage();
                Log.e(TAG, errorMsg, e);
                mainHandler.post(() -> callback.onFailure(errorMsg));
            } catch (ValidationException e) {
                String errorMsg = "Erreur de validation des données : " + e.getMessage();
                Log.e(TAG, errorMsg, e);
                mainHandler.post(() -> callback.onFailure(errorMsg));
            } catch (SyncException e) {
                String errorMsg = "Erreur de synchronisation : " + e.getMessage();
                Log.e(TAG, errorMsg, e);
                mainHandler.post(() -> callback.onFailure(errorMsg));
            } catch (Exception e) {
                String errorMsg = "Erreur inattendue : " + e.getMessage();
                Log.e(TAG, errorMsg, e);
                mainHandler.post(() -> callback.onFailure(errorMsg));
            }
        });
    }

    private void syncUsers() throws NetworkException, DatabaseException, ValidationException {
        Log.d(TAG, "Synchronisation des utilisateurs...");

        try {
            Call<List<Users>> call = apiService.getAllUsers();
            Response<List<Users>> response = executeWithRetry(call, 3);

            if (response.isSuccessful() && response.body() != null) {
                List<Users> users = response.body();
                Log.d(TAG, "Reçu " + users.size() + " utilisateurs");

                for (Users user : users) {
                    // CORRECTION 2: Vérifier les données avant insertion avec exceptions
                    if (user == null) {
                        throw new ValidationException("Utilisateur reçu est null");
                    }

                    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                        throw new ValidationException("Email utilisateur manquant ou vide pour l'utilisateur ID: " + user.getId());
                    }

                    try {
                        dbHelper.ajouterUtilisateur(user);
                        Log.d(TAG, "Utilisateur ajouté: " + user.getEmail());
                    } catch (Exception e) {
                        throw new DatabaseException("Erreur lors de l'ajout de l'utilisateur: " + user.getEmail(), e);
                    }
                }

                try {
                    prefs.edit().putString("last_user_sync", getCurrentDate()).apply();
                } catch (Exception e) {
                    Log.w(TAG, "Impossible de sauvegarder la date de synchronisation des utilisateurs", e);
                }
            } else {
                throw new NetworkException("Échec de la synchronisation des utilisateurs: " + response.code() + " - " + response.message());
            }
        } catch (NetworkException | DatabaseException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            Log.d("Erreur inattendue lors de la synchronisation des utilisateurs", e.getMessage());
        }
    }

    private void syncCategories() throws NetworkException, DatabaseException, ValidationException {
        Log.d(TAG, "Synchronisation des catégories...");

        try {
            Call<List<Categories>> call = apiService.getAllCategories();
            Response<List<Categories>> response = executeWithRetry(call, 3);

            if (response.isSuccessful() && response.body() != null) {
                List<Categories> categories = response.body();
                Log.d(TAG, "Reçu " + categories.size() + " catégories");

                for (Categories category : categories) {
                    // CORRECTION 3: Vérifier les données et utiliser l'ID avec exceptions
                    if (category == null) {
                        throw new ValidationException("Catégorie reçue est null");
                    }

                    if (category.getNom() == null || category.getNom().trim().isEmpty()) {
                        throw new ValidationException("Nom de catégorie manquant pour la catégorie ID: " + category.getId());
                    }

                    try {
                        // Vérifier si la catégorie existe déjà
                        if (!dbHelper.categoryExists(category.getId())) {
                            dbHelper.insertCategory(
                                    category.getNom(),
                                    category.getDescription() != null ? category.getDescription() : ""
                            );
                            Log.d(TAG, "Catégorie ajoutée: " + category.getNom());
                        } else {
                            // Mettre à jour si elle existe
                            dbHelper.updateCategoryById(category.getId(), category.getNom(), category.getDescription());
                            Log.d(TAG, "Catégorie mise à jour: " + category.getNom());
                        }
                    } catch (Exception e) {
                        throw new DatabaseException("Erreur lors de l'ajout/mise à jour de la catégorie: " + category.getNom(), e);
                    }
                }

                try {
                    prefs.edit().putString("last_category_sync", getCurrentDate()).apply();
                } catch (Exception e) {
                    Log.w(TAG, "Impossible de sauvegarder la date de synchronisation des catégories", e);
                }
            } else {
                throw new NetworkException("Échec de la synchronisation des catégories: " + response.code() + " - " + response.message());
            }
        } catch (NetworkException | DatabaseException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            Log.d("Erreur inattendue lors de la synchronisation des catégories", e.getMessage());
        }
    }

    private void syncProducts() throws NetworkException, DatabaseException, ValidationException {
        Log.d(TAG, "Synchronisation des produits...");

        try {
            Call<List<Produits>> call = apiService.getAllProducts();
            Response<List<Produits>> response = executeWithRetry(call, 3);

            if (response.isSuccessful() && response.body() != null) {
                List<Produits> products = response.body();
                Log.d(TAG, "Reçu " + products.size() + " produits");

                for (Produits product : products) {
                    if (product == null) {
                        throw new ValidationException("Produit reçu est null");
                    }

                    if (product.getNom() == null || product.getNom().trim().isEmpty()) {
                        throw new ValidationException("Nom de produit manquant pour le produit ID: " + product.getId());
                    }

                    if (product.getPrix() < 0) {
                        throw new ValidationException("Prix invalide pour le produit: " + product.getNom());
                    }

                    try {
                        int categoryId = 0;
                        int userId = 0;

                        if (product.getCategorie() != null) {
                            categoryId = product.getCategorie().getId();
                        }
                        if (product.getUtilisateur() != null) {
                            userId = product.getUtilisateur().getId();
                        }

                        // Vérifier si le produit existe déjà
                        if (!dbHelper.productExists(product.getId())) {
                            dbHelper.insertProduct(
                                    product.getNom(),
                                    product.getDescription() != null ? product.getDescription() : "",
                                    product.getPrix(),
                                    product.getImage1() != null ? product.getImage1() : "",
                                    product.getImage2() != null ? product.getImage2() : "",
                                    product.getImage3() != null ? product.getImage3() : "",
                                    product.getImage4() != null ? product.getImage4() : "",
                                    product.getQuantite(),
                                    categoryId,
                                    userId,
                                    product.isActive()
                            );
                            Log.d(TAG, "Produit ajouté: " + product.getNom());
                        } else {
                            dbHelper.updateProduct(
                                    product.getId(),
                                    product.getNom(),
                                    product.getDescription() != null ? product.getDescription() : "",
                                    product.getPrix(),
                                    product.getImage1() != null ? product.getImage1() : "",
                                    product.getImage2() != null ? product.getImage2() : "",
                                    product.getImage3() != null ? product.getImage3() : "",
                                    product.getImage4() != null ? product.getImage4() : "",
                                    product.getQuantite(),
                                    categoryId,
                                    userId,
                                    product.isActive()
                            );
                            Log.d(TAG, "Produit mis à jour: " + product.getNom());
                        }
                    } catch (Exception e) {
                        throw new DatabaseException("Erreur lors de l'ajout/mise à jour du produit: " + product.getNom(), e);
                    }
                }

                try {
                    prefs.edit().putString("last_product_sync", getCurrentDate()).apply();
                } catch (Exception e) {
                    Log.w(TAG, "Impossible de sauvegarder la date de synchronisation des produits", e);
                }
            } else {
                throw new NetworkException("Échec de la synchronisation des produits: " + response.code() + " - " + response.message());
            }
        } catch (NetworkException | DatabaseException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "Erreur inattendue lors de la synchronisation des produits", e);
            try {
                throw new SyncException("Erreur inattendue lors de la synchronisation des produits", e);
            } catch (SyncException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    private void syncPaniers() throws NetworkException, DatabaseException, ValidationException {
        Log.d(TAG, "Synchronisation des paniers...");

        try {
            Call<List<Panier>> call = apiService.getAllPaniers();
            Response<List<Panier>> response = executeWithRetry(call, 3);

            if (response.isSuccessful() && response.body() != null) {
                List<Panier> paniers = response.body();
                Log.d(TAG, "Reçu " + paniers.size() + " paniers");

                for (Panier panier : paniers) {
                    if (panier == null) {
                        throw new ValidationException("Panier reçu est null");
                    }

                    if (panier.getUtilisateur() == null || panier.getUtilisateur().getId() <= 0) {
                        throw new ValidationException("Utilisateur manquant ou invalide pour le panier ID: " + panier.getId());
                    }

                    try {
                        // Vérifier si le panier existe déjà
                        if (!dbHelper.panierExists(panier.getId())) {
                            dbHelper.ajouterPanier(panier);
                            Log.d(TAG, "Panier ajouté pour utilisateur: " + panier.getUtilisateur().getId());
                        } else {
                            dbHelper.updatePanierById(panier);
                            Log.d(TAG, "Panier mis à jour pour utilisateur: " + panier.getUtilisateur().getId());
                        }
                    } catch (Exception e) {
                        throw new DatabaseException("Erreur lors de l'ajout/mise à jour du panier ID: " + panier.getId(), e);
                    }
                }

                try {
                    prefs.edit().putString("last_panier_sync", getCurrentDate()).apply();
                } catch (Exception e) {
                    Log.w(TAG, "Impossible de sauvegarder la date de synchronisation des paniers", e);
                }
            } else {
                throw new NetworkException("Échec de la synchronisation des paniers: " + response.code() + " - " + response.message());
            }
        } catch (NetworkException | DatabaseException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "Erreur inattendue lors de la synchronisation des paniers", e);
            try {
                throw new SyncException("Erreur inattendue lors de la synchronisation des paniers", e);
            } catch (SyncException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    private void syncArticlePaniers() throws NetworkException, DatabaseException, ValidationException {
        Log.d(TAG, "Synchronisation des articles panier...");

        try {
            Call<List<ArticlePanier>> call = apiService.getAllArticlesPanier();
            Response<List<ArticlePanier>> response = executeWithRetry(call, 3);

            if (response.isSuccessful() && response.body() != null) {
                List<ArticlePanier> articles = response.body();
                Log.d(TAG, "Reçu " + articles.size() + " articles panier");

                for (ArticlePanier article : articles) {
                    if (article == null) {
                        throw new ValidationException("Article panier reçu est null");
                    }

                    if (article.getPanier() == null || article.getPanier().getId() <= 0) {
                        throw new ValidationException("Panier manquant ou invalide pour l'article ID: " + article.getId());
                    }

                    if (article.getProduit() == null || article.getProduit().getId() <= 0) {
                        throw new ValidationException("Produit manquant ou invalide pour l'article ID: " + article.getId());
                    }

                    if (article.getQuantite() <= 0) {
                        throw new ValidationException("Quantité invalide pour l'article ID: " + article.getId());
                    }

                    try {
                        // Vérifier si l'article existe déjà
                        if (!dbHelper.articlePanierExists(article.getId())) {
                            dbHelper.ajouterArticlePanier(article);
                            Log.d(TAG, "Article panier ajouté: " + article.getId());
                        } else {
                            dbHelper.updateArticlePanierById(article);
                            Log.d(TAG, "Article panier mis à jour: " + article.getId());
                        }
                    } catch (Exception e) {
                        throw new DatabaseException("Erreur lors de l'ajout/mise à jour de l'article panier ID: " + article.getId(), e);
                    }
                }

                try {
                    prefs.edit().putString("last_article_panier_sync", getCurrentDate()).apply();
                } catch (Exception e) {
                    Log.w(TAG, "Impossible de sauvegarder la date de synchronisation des articles panier", e);
                }
            } else {
                throw new NetworkException("Échec de la synchronisation des articles panier: " + response.code() + " - " + response.message());
            }
        } catch (NetworkException | DatabaseException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "Erreur inattendue lors de la synchronisation des articles panier", e);
            try {
                throw new SyncException("Erreur inattendue lors de la synchronisation des articles panier", e);
            } catch (SyncException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    private void syncFavorites() throws NetworkException, DatabaseException, ValidationException {
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Log.w(TAG, "Aucun ID utilisateur disponible pour la synchronisation des favoris");
            return; // Ne pas lancer d'exception, juste ignorer
        }

        Log.d(TAG, "Synchronisation des favoris pour l'utilisateur: " + userId);

        try {
            Call<List<Produits>> call = apiService.getFavoriteProducts(userId);
            Response<List<Produits>> response = executeWithRetry(call, 3);

            if (response.isSuccessful() && response.body() != null) {
                List<Produits> favoriteProducts = response.body();
                Log.d(TAG, "Reçu " + favoriteProducts.size() + " produits favoris");

                try {
                    // CORRECTION 7: Nettoyer les anciens favoris avant d'ajouter les nouveaux
                    dbHelper.removeFavorite(userId, 1);

                    for (Produits product : favoriteProducts) {
                        if (product == null) {
                            throw new ValidationException("Produit favori reçu est null");
                        }

                        if (product.getId() <= 0) {
                            throw new ValidationException("ID produit favori invalide: " + product.getId());
                        }

                        try {
                            dbHelper.addFavorite(userId, product.getId());
                            Log.d(TAG, "Favori ajouté: " + product.getNom());
                        } catch (Exception e) {
                            throw new DatabaseException("Erreur lors de l'ajout du favori produit ID: " + product.getId(), e);
                        }
                    }
                } catch (Exception e) {
                    throw new DatabaseException("Erreur lors de la gestion des favoris", e);
                }

                try {
                    prefs.edit().putString("last_favorite_sync", getCurrentDate()).apply();
                } catch (Exception e) {
                    Log.w(TAG, "Impossible de sauvegarder la date de synchronisation des favoris", e);
                }
            } else {
                throw new NetworkException("Échec de la synchronisation des favoris: " + response.code() + " - " + response.message());
            }
        } catch (NetworkException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            Log.d("Erreur inattendue lors de la synchronisation des favoris", e.getMessage());
        }
    }

    private void cleanOrphanedCartItems() throws NetworkException, DatabaseException {
        Log.d(TAG, "Nettoyage des articles panier orphelins...");

        try {
            Call<Void> call = apiService.cleanOrphanedCartItems();
            Response<Void> response = executeWithRetry(call, 3);

            if (response.isSuccessful()) {
                try {
                    dbHelper.cleanOrphanedCartItems();
                    Log.d(TAG, "Nettoyage des articles orphelins terminé");
                } catch (Exception e) {
                    throw new DatabaseException("Erreur lors du nettoyage local des articles orphelins", e);
                }
            } else {
                throw new NetworkException("Échec du nettoyage des articles orphelins: " + response.code() + " - " + response.message());
            }
        } catch (NetworkException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            Log.d("Erreur inattendue lors du nettoyage des articles orphelins", e.getMessage());
        }
    }

    private <T> Response<T> executeWithRetry(Call<T> call, int maxRetries) throws NetworkException {
        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxRetries) {
            try {
                Log.d(TAG, "Tentative " + (attempt + 1) + "/" + maxRetries);
                Response<T> response = call.clone().execute();

                if (response.isSuccessful()) {
                    return response;
                } else {
                    Log.w(TAG, "Réponse non réussie: " + response.code() + " - " + response.message());
                    if (response.code() >= 400 && response.code() < 500) {
                        // Erreur client, ne pas réessayer
                        throw new NetworkException("Erreur client: " + response.code() + " - " + response.message());
                    }
                }
            } catch (Exception e) {
                lastException = e;
                Log.e(TAG, "Erreur lors de la tentative " + (attempt + 1), e);

                if (++attempt == maxRetries) {
                    throw new NetworkException("Échec après " + maxRetries + " tentatives", e);
                }

                try {
                    Thread.sleep(1000 * attempt); // Délai progressif
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new NetworkException("Thread interrompu pendant la requête", ie);
                }
            }
        }

        throw new NetworkException("Nombre maximum de tentatives atteint", lastException);
    }

    private String getCurrentDate() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la génération de la date", e);
            return "Date inconnue";
        }
    }
}