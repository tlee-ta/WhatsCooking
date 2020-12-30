package com.example.android.whatscooking.network;

import com.example.android.whatscooking.model.JsonIngredient;
import com.example.android.whatscooking.model.JsonMeal;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealInterface {
    @GET("filter.php")
    Call<JsonMeal> getMeals(@Query("i") String itemName);

    @GET("lookup.php")
    Call<JsonMeal> getMealDetails(@Query("i") String itemId);

    @GET("list.php?i=list")
    Call<JsonIngredient> getAllIngredients();
}
