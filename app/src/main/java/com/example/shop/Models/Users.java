package com.example.shop.Models;

import java.util.Date;

public class Users {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role;// admin, client, user
    private String adresse;
    private String telephone;
    private String ville;
    private String codePostal;
    private String pays;
    private String image;
    private String dateInscription;


   public  Users(){}
    public Users(String nom, String prenom, String email, String password, String role, String adresse, String telephone, String ville, String codePostal, String pays, String image, String dateInscription) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.adresse = adresse;
        this.telephone = telephone;
        this.ville = ville;
        this.codePostal = codePostal;
        this.pays = pays;
        this.image = image;
        this.dateInscription = dateInscription;
    }

    public Users(int utilisateurId) {
       this.id=utilisateurId;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(String dateInscription) {
        this.dateInscription = dateInscription;
    }
}
