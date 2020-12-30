package com.example.android.whatscooking.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class JsonIngredient {

    @SerializedName("meals")
    @Expose
    private List<Ingredient> fullIngredientList = null;

    public List<Ingredient> getFullIngredientList() {
        return fullIngredientList;
    }

    public void setFullIngredientList(List<Ingredient> fullIngredientList) {
        this.fullIngredientList = fullIngredientList;
    }
}
