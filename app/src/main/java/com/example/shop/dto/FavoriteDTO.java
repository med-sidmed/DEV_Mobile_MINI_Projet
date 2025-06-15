package com.example.shop.dto;


import com.google.gson.annotations.SerializedName;

public class FavoriteDTO {
    @SerializedName("userId")
    private Integer userId;

    @SerializedName("productId")
    private Integer productId;

    // Getters and setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}