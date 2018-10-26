package com.example.mahima.yummly;

import com.google.gson.annotations.SerializedName;

public class RecipeIngredient {

    @SerializedName("quantity")
    private Integer quantity;
    @SerializedName("measure")
    private String measure;
    @SerializedName("ingredient")
    private String ingredient;

    public Integer getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }
}
