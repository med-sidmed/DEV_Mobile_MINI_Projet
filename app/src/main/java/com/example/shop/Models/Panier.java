package com.example.shop.Models;

import java.util.Date;

public class Panier {
    private int id;

    private Users utilisateur;

    private Date dateAjout;
    private Date dateModification;
    private boolean isActive;

    private List<ArticlePanier> articles;
}
