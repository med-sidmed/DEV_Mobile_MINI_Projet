package com.example.shop.Models;

import java.util.Date;

public class Commandes {
    private int id;
    private Date dateCommande;
    private String etat;//Complété ou en cours ou annulé
    private Date dateAjout;
    private Date dateModification;
    private boolean isActive;
    private String adresseLivraison;
    private String modePaiement;
    private double montantTotal;
    private Date dateAnnulation;
    private Date dateLivraison;
    private Users utilisateur;
    private Panier panier;


    Commandes(){}

    public Commandes(Date dateCommande, String etat, Date dateAjout, Date dateModification, boolean isActive, String adresseLivraison, String modePaiement, double montantTotal, Date dateAnnulation, Date dateLivraison, Users utilisateur, Panier panier) {
        this.dateCommande = dateCommande;
        this.etat = etat;
        this.dateAjout = dateAjout;
        this.dateModification = dateModification;
        this.isActive = isActive;
        this.adresseLivraison = adresseLivraison;
        this.modePaiement = modePaiement;
        this.montantTotal = montantTotal;
        this.dateAnnulation = dateAnnulation;
        this.dateLivraison = dateLivraison;
        this.utilisateur = utilisateur;
        this.panier = panier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
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

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Date getDateAnnulation() {
        return dateAnnulation;
    }

    public void setDateAnnulation(Date dateAnnulation) {
        this.dateAnnulation = dateAnnulation;
    }

    public Date getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(Date dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public Users getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Users utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Panier getPanier() {
        return panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }
}
