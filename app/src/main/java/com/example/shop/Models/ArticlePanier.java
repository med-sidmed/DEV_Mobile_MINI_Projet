package com.example.shop.Models;

public class ArticlePanier {

    private Long id;

    private Produits produit;


    private Panier panier;

    private int quantite;




    private String dateModification;

    private boolean isActive;

    private double prixUnitaire;

    private double prixTotal;   private String dateAjout;




     public ArticlePanier(){}

    public ArticlePanier(Produits produit, Panier panier, int quantite, String dateAjout, String dateModification, boolean isActive, double prixUnitaire, double prixTotal) {
        this.produit = produit;
        this.panier = panier;
        this.quantite = quantite;
        this.dateAjout = dateAjout;
        this.dateModification = dateModification;
        this.isActive = isActive;
        this.prixUnitaire = prixUnitaire;
        this.prixTotal = prixTotal;
    }

    public ArticlePanier(Produits produit, Panier panier, int quantite,
                         Double prix, Double prix1, String format,
                         Object o, boolean b) {

         this.produit=produit;
         this.panier=panier;
         this.quantite=quantite;
         this.prixTotal= prix+prix1;
         this.dateAjout=format;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produits getProduit() {
        return produit;
    }

    public void setProduit(Produits produit) {
        this.produit = produit;
    }

    public Panier getPanier() {
        return panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getDateAjout() {
        return String.valueOf(dateAjout);
    }

    public void setDateAjout(String dateAjout) {
        this.dateAjout = dateAjout;
    }

    public String getDateModification() {
        return String.valueOf(dateModification);
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

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }


}
