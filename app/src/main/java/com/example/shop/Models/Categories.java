package com.example.shop.Models;

import java.util.Date;
import java.util.List;

public class Categories {
    private int id;
    private String nom;
    private String description;
    private String image;
    private Date dateAjout;
    private Date dateModification;
    private boolean isActive;
    private Users utilisateur;
    private List<Produits> produits;

    Categories(){}

    public Categories(String nom, String description, String image, Date dateAjout, Date dateModification, boolean isActive, Users utilisateur, List<Produits> produits) {
        this.nom = nom;
        this.description = description;
        this.image = image;
        this.dateAjout = dateAjout;
        this.dateModification = dateModification;
        this.isActive = isActive;
        this.utilisateur = utilisateur;
        this.produits = produits;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Date dateAjout) {
        this.dateAjout = dateAjout;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public void setDateModification(Date dateModification) {
        this.dateModification = dateModification;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Users getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Users utilisateur) {
        this.utilisateur = utilisateur;
    }

    public List<Produits> getProduits() {
        return produits;
    }

    public void setProduits(List<Produits> produits) {
        this.produits = produits;
    }
}
