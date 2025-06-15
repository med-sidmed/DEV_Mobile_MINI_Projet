package com.example.shop.Models;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Favorites {
     private Long id;
     private Users user;
     private Produits product;
     private LocalDateTime dateAdded;
     private Boolean isActive = true;


    public Favorites(){}
    public Favorites(Long id, Users user, Produits product, LocalDateTime dateAdded, Boolean isActive) {
        this.id = id;
        this.user = user;
        this.product = product;
        this.dateAdded = dateAdded;
        this.isActive = isActive;
    }

    public Favorites(Users user, Produits product, LocalDateTime dateAdded, Boolean isActive) {
        this.user = user;
        this.product = product;
        this.dateAdded = dateAdded;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Produits getProduct() {
        return product;
    }

    public void setProduct(Produits product) {
        this.product = product;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
