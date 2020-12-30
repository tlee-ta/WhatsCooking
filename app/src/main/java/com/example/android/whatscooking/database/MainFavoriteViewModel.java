package com.example.android.whatscooking.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.whatscooking.model.Meal;

import java.util.List;

public class MainFavoriteViewModel extends AndroidViewModel {

    private LiveData<List<Meal>> favorites;

    public MainFavoriteViewModel(Application application) {
        super(application);
        FavoriteDatabase database = FavoriteDatabase.getInstance(this.getApplication());
        favorites = database.favoriteDao().loadAllFavorites();
    }

    public LiveData<List<Meal>> getFavorites() {
        return favorites;
    }
}
