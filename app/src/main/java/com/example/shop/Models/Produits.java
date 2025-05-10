package com.example.shop.Models;

import java.util.Date;

public class Produits {
    private int id;
    private String nom;
    private String description;
    private Double prix;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
    private int quantite;
    private Date dateAjout;
    private Date dateModification;
    private boolean isActive;
    private Categories categorie;

    private Users utilisateur;


    public Produits(){}

    public Produits(String nom, String description, double prix, String image1, String image2, String image3, String image4, int quantite, Date dateAjout, Date dateModification, boolean isActive, Categories categorie, Users utilisateur) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.quantite = quantite;
        this.dateAjout = dateAjout;
        this.dateModification = dateModification;
        this.isActive = isActive;
        this.categorie = categorie;
        this.utilisateur = utilisateur;
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

    public Double getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = (double) prix;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
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

    public Categories getCategorie() {
        return categorie;
    }

    public void setCategorie(Categories categorie) {
        this.categorie = categorie;
    }

    public Users getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Users utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void setCategoryId(int categoryId) {
        this.categorie=null;
    }

    public void setUserId(int userId) {
        this.utilisateur=null;
    }
}