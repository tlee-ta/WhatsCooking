package com.example.android.whatscooking.network;

import com.example.android.whatscooking.model.JsonArea;
import com.example.android.whatscooking.model.JsonCategory;
import com.example.android.whatscooking.model.JsonIngredient;
import com.example.android.whatscooking.model.JsonMeal;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealInterface {
    @GET("filter.php")
    Call<JsonMeal> getMealsIngredient(@Query("i") String itemName);

    @GET("filter.php")
    Call<JsonMeal> getMealsCategory(@Query("c") String itemName);

    @GET("filter.php")
    Call<JsonMeal> getMealsArea(@Query("a") String itemName);

    @GET("lookup.php")
    Call<JsonMeal> getMealDetails(@Query("i") String itemId);

    @GET("list.php?i=list")
    Call<JsonIngredient> getAllIngredients();

    @GET("list.php?c=list")
    Call<JsonCategory> getAllCategories();

    @GET("list.php?a=list")
    Call<JsonArea> getAllAreas();
}
