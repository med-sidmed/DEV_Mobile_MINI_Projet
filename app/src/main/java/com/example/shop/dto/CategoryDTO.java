package com.example.shop.dto;


import com.google.gson.annotations.SerializedName;

public class CategoryDTO {
    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}