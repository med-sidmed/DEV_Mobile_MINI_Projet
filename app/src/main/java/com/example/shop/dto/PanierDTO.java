package com.example.shop.dto;


import com.google.gson.annotations.SerializedName;

public class PanierDTO {
    @SerializedName("utilisateurId")
    private Integer utilisateurId;

    @SerializedName("dateAjout")
    private String dateAjout;

    @SerializedName("dateModification")
    private String dateModification;

    @SerializedName("isActive")
    private Boolean isActive;

    // Getters and setters
    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        this.utilisateurId = utilisateurId;
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
