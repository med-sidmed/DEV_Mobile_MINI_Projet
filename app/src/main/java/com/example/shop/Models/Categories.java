package com.example.shop.Models;

import java.util.Date;
import java.util.List;

public class Categories {
    private int id;
    private String nom;
    private String description;


    Categories(){}

    public Categories(String nom, String description) {
        this.nom = nom;
        this.description = description;

    }

    public Categories(int id,String nom, String description) {
        this.nom = nom;
        this.description = description;
        this.id=id;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
