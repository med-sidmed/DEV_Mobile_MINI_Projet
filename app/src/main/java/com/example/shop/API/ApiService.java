package com.example.shop.API;

import com.example.shop.Models.ArticlePanier;
import com.example.shop.Models.Categories;
import com.example.shop.Models.Panier;
import com.example.shop.Models.Produits;
import com.example.shop.Models.Users;
import com.example.shop.dto.ArticlePanierDTO;
import com.example.shop.dto.CategoryDTO;
import com.example.shop.dto.PanierDTO;
import com.example.shop.dto.ProductDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // User Endpoints
    @GET("api/users")
    Call<List<Users>> getAllUsers();

    @GET("api/users/{id}")
    Call<Users> getUserById( @Path("id") int id);

    @GET("api/users/email")
    Call<Users> getUserByEmail( @Query("email") String email);

    @POST("api/users/register")
    Call<Users> registerUser(@Body Users user);

    @PUT("api/users/{id}")
    Call<Users> updateUser(@Path("id") int id, @Body Users user);

    @DELETE("api/users/{id}")
    Call<Void> deleteUser( @Path("id") int id);

    // Favorite Endpoints
    @GET("api/favorites/user/{userId}/products")
    Call<List<Produits>> getFavoriteProducts(@Path("userId") int userId);

    @POST("api/favorites/user/{userId}/product/{productId}")
    Call<Void> addToFavorites(@Path("userId") int userId, @Path("productId") int productId);

    @DELETE("api/favorites/user/{userId}/product/{productId}")
    Call<Void> removeFromFavorites( @Path("userId") int userId, @Path("productId") int productId);

    @GET("api/favorites/user/{userId}/product/{productId}")
    Call<Boolean> isProductInFavorites( @Path("userId") int userId, @Path("productId") int productId);

    // Cart Endpoints
    @GET("api/cart/user/{userId}")
    Call<Panier> getCartByUserId( @Path("userId") int userId);

    @POST("api/cart")
    Call<Panier> createCart( @Body PanierDTO panierDTO);

    @POST("api/cart/add")
    Call<ArticlePanier> addToCart( @Body ArticlePanierDTO cartItemDTO);

    @PUT("api/cart/update/{cartItemId}")
    Call<ArticlePanier> updateCartItem( @Path("cartItemId") int cartItemId, @Body ArticlePanier cartItem);

    @DELETE("api/cart/remove/{cartItemId}")
    Call<Void> removeFromCart( @Path("cartItemId") int cartItemId);

    @DELETE("api/cart/clear/{userId}")
    Call<Void> clearCart( @Path("userId") int userId);

    @GET("api/cart/allArticles")
    Call<List<ArticlePanier>> getAllArticlesPanier();

    @GET("api/cart/all")
    Call<List<Panier>> getAllPaniers();

    @GET("api/cart/articlepanier/{id}")
    Call<ArticlePanier> getArticlePanierById( @Path("id") int id);

    @GET("api/cart/users/{userId}/articles")
    Call<List<ArticlePanier>> getUserArticles( @Path("userId") int userId);

    @DELETE("api/cart/clean-orphaned")
    Call<Void> cleanOrphanedCartItems();

    // Product Endpoints
    @GET("api/products")
    Call<List<Produits>> getAllProducts();

    @GET("api/products/{id}")
    Call<Produits> getProductById( @Path("id") int id);

    @GET("api/products/category/{categoryId}")
    Call<List<Produits>> getProductsByCategory( @Path("categoryId") int categoryId);

    @GET("api/products/search")
    Call<List<Produits>> searchProducts( @Query("query") String query);

    @POST("api/products")
    Call<Boolean> createProduct( @Body ProductDTO productDTO);

    @PUT("api/products/{id}")
    Call<Boolean> updateProduct( @Path("id") int id, @Body ProductDTO productDTO);

    @DELETE("api/products/{id}")
    Call<Boolean> deleteProduct( @Path("id") int id);

    // Category Endpoints
    @GET("api/categories")
    Call<List<Categories>> getAllCategories();

    @GET("api/categories/{id}")
    Call<Categories> getCategoryById( @Path("id") int id);

    @GET("api/categories/search")
    Call<List<Categories>> getCategoriesByName( @Query("name") String name);

    @POST("api/categories")
    Call<Boolean> createCategory( @Body CategoryDTO categoryDTO);

    @PUT("api/categories/{id}")
    Call<Boolean> updateCategory( @Path("id") int id, @Body CategoryDTO categoryDTO);

    @DELETE("api/categories/{id}")
    Call<Boolean> deleteCategory( @Path("id") int id);
}