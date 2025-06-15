package com.example.shop.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Panier {
     private int id;
     private Users utilisateur;

    private String dateAjout;
    private String dateModification;
    private boolean isActive;
     private List<ArticlePanier> articles;


    public Panier(){}
    public Panier(Users utilisateur, String dateAjout, String dateModification, boolean isActive, List<ArticlePanier> articles) {
        this.utilisateur = utilisateur;
        this.dateAjout = dateAjout;
        this.dateModification = dateModification;
        this.isActive = isActive;
        this.articles = articles;
    }

    public Panier(int panierId) {
        this.id=panierId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Users getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Users utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(String dateAjout) {
        this.dateAjout = dateAjout;
    }

    public String getDateModification() {
        return dateModification;
    }

    public void setDateModification(String dateModification) {
        this.dateModification = dateModification;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<ArticlePanier> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticlePanier> articles) {
        this.articles = articles;
    }
}
