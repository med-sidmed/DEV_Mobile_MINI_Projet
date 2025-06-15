package com.example.shop.dto;

 
import com.google.gson.annotations.SerializedName;

public class ArticlePanierDTO {
    @SerializedName("produitId")
    private Integer produitId;

    @SerializedName("panierId")
    private Integer panierId;

    @SerializedName("quantite")
    private Integer quantite;

    @SerializedName("prixUnitaire")
    private Double prixUnitaire;

    @SerializedName("prixTotal")
    private Double prixTotal;

    @SerializedName("dateAjout")
    private String dateAjout;

    @SerializedName("dateModification")
    private String dateModification;

    @SerializedName("isActive")
    private Boolean isActive;

    // Getters and setters
    public Integer getProduitId() {
        return produitId;
    }

    public void setProduitId(Integer produitId) {
        this.produitId = produitId;
    }

    public Integer getPanierId() {
        return panierId;
    }

    public void setPanierId(Integer panierId) {
        this.panierId = panierId;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(Double prixTotal) {
        this.prixTotal = prixTotal;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
