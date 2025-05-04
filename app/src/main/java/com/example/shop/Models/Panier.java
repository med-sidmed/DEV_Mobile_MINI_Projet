package com.example.shop.Models;

import java.util.Date;
import java.util.List;

public class Panier {
    private int id;

    private Users utilisateur;

    private Date dateAjout;
    private Date dateModification;
    private boolean isActive;

    private List<ArticlePanier> articles;

    public Panier(Users utilisateur, Date dateAjout, Date dateModification, boolean isActive, List<ArticlePanier> articles) {
        this.utilisateur = utilisateur;
        this.dateAjout = dateAjout;
        this.dateModification = dateModification;
        this.isActive = isActive;
        this.articles = articles;
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

    public List<ArticlePanier> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticlePanier> articles) {
        this.articles = articles;
    }
}
