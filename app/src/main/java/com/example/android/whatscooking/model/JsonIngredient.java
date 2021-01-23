package com.example.android.whatscooking.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class JsonIngredient {

    @SerializedName("meals")
    @Expose
    private List<Ingredient> fullIngredientList = null;

    public List<String> getFullIngredientList() {
        List<String> strIngredients = new ArrayList<>();
        for (int i=0; i < fullIngredientList.size(); i++) {
            strIngredients.add(i, fullIngredientList.get(i).getName());
        }
        return strIngredients;
    }

    public void setFullIngredientList(List<Ingredient> fullIngredientList) {
        this.fullIngredientList = fullIngredientList;
    }
}
