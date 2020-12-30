package com.example.android.whatscooking.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.android.whatscooking.model.Meal;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Query("SELECT * FROM favMeals")
    LiveData<List<Meal>> loadAllFavorites();

    @Query("SELECT * FROM favMeals WHERE id = :id")
    LiveData<Meal> loadFavoriteById(int id);

    @Insert
    void insertFavMeal(Meal meal);

    @Delete
    void deleteFavMeal(Meal meal);
}
