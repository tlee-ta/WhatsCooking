package com.example.android.whatscooking.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class JsonMeal {

    @SerializedName("meals")
    @Expose
    private List<Meal> mealList = null;

    public List<Meal> getMealList() {
        return mealList;
    }

    public void setMealList(List<Meal> mealList) {
        this.mealList = mealList;
    }
}
