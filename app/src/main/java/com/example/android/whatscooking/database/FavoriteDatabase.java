package com.example.android.whatscooking.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.android.whatscooking.model.Meal;

@Database(entities = {Meal.class}, version = 1, exportSchema = false)
public abstract class FavoriteDatabase extends RoomDatabase {

    private static FavoriteDatabase favoriteDatabase;
    private static final Object LOCK = new Object();
    private static final String DB_NAME = "favorite_database";

    public static FavoriteDatabase getInstance(Context context) {
        if (favoriteDatabase == null) {
            synchronized (LOCK) {
                favoriteDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        FavoriteDatabase.class, DB_NAME)
                        .build();
            }
        }
        return favoriteDatabase;
    }

    public abstract FavoriteDao favoriteDao();
}
