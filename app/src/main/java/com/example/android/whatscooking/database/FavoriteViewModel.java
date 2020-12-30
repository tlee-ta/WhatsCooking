package com.example.android.whatscooking.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.whatscooking.model.Meal;

public class FavoriteViewModel extends AndroidViewModel {

    private FavoriteDatabase database;

    public FavoriteViewModel(Application application) {
        super(application);
        database = FavoriteDatabase.getInstance(this.getApplication());
    }

    public LiveData<Meal> getFavMeal(int id) {
        return database.favoriteDao().loadFavoriteById(id);
    }
}
