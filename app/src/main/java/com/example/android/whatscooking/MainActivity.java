package com.example.android.whatscooking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.android.whatscooking.adapters.MealAdapter;
import com.example.android.whatscooking.database.MainFavoriteViewModel;
import com.example.android.whatscooking.databinding.ActivityMainBinding;
import com.example.android.whatscooking.model.Ingredient;
import com.example.android.whatscooking.model.JsonMeal;
import com.example.android.whatscooking.model.Meal;
import com.example.android.whatscooking.network.MealInterface;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MealAdapter.MealAdapterOnClickHandler {

    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private String searchIngredient = "";

    ActivityMainBinding mainBinding;

    private MealAdapter mealAdapter;
    private MealInterface mealInterface;

    private MenuItem favMenuItem;
    private MenuItem homeMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent == null) {
                searchIngredient = "";
                return;
            }
            if (intent.hasExtra(String.valueOf(R.string.KEY_INGREDIENT))) {
                final String ingredient = intent.getStringExtra(String.valueOf(R.string.KEY_INGREDIENT));

                if (ingredient == null) {
                    searchIngredient = "";
                    return;
                }
                searchIngredient = ingredient;
            }
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int gridCount = (int)displayMetrics.widthPixels/500;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, gridCount);

        mainBinding.rvMeals.setLayoutManager(gridLayoutManager);
        mainBinding.rvMeals.setHasFixedSize(true);

        mealAdapter = new MealAdapter(this, this);
        mainBinding.rvMeals.setAdapter(mealAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                .build();
        mealInterface = retrofit.create(MealInterface.class);

        loadMealData(searchIngredient);
    }

    private void loadMealData(String searchItem) {
        mainBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
        mainBinding.loadingBar.setVisibility(View.VISIBLE);
        mainBinding.rvMeals.setVisibility(View.INVISIBLE);
        mainBinding.tvNoFavorites.setVisibility(View.INVISIBLE);

        Call<JsonMeal> callMeals = mealInterface.getMeals(searchItem);
        callMeals.enqueue(new Callback<JsonMeal>() {
            @Override
            public void onResponse(Call<JsonMeal> call, Response<JsonMeal> response) {
                if (!response.isSuccessful()) {
                    Log.i("Main", String.valueOf(response.code()));
                    mainBinding.tvErrorMessage.setVisibility(View.VISIBLE);
                    mainBinding.loadingBar.setVisibility(View.INVISIBLE);
                    mainBinding.rvMeals.setVisibility(View.INVISIBLE);
                    return;
                }
                mainBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
                mainBinding.loadingBar.setVisibility(View.INVISIBLE);
                mainBinding.rvMeals.setVisibility(View.VISIBLE);

                assert response.body() != null;
                List<Meal> mealsList = response.body().getMealList();
                if (mealsList == null) {
                    mainBinding.tvNoResults.setVisibility(View.VISIBLE);
                    mainBinding.rvMeals.setVisibility(View.INVISIBLE);
                }

                mealAdapter.setMealData(mealsList);
            }

            @Override
            public void onFailure(Call<JsonMeal> call, Throwable t) {
                Log.i("Main", Objects.requireNonNull(t.getMessage()));
                mainBinding.tvErrorMessage.setVisibility(View.VISIBLE);
                mainBinding.loadingBar.setVisibility(View.INVISIBLE);
                mainBinding.rvMeals.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onClick(Meal selectedMeal) {
        Context context = this;
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(String.valueOf(R.string.KEY_ID), selectedMeal.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        favMenuItem = menu.findItem(R.id.show_favorites);
        homeMenuItem = menu.findItem(R.id.show_main);
        favMenuItem.setVisible(true);
        homeMenuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter) {
            Context context = this;
            Intent intent = new Intent(context, FilterActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.show_main) {
            favMenuItem.setVisible(true);
            homeMenuItem.setVisible(false);
            setTitle(getString(R.string.app_name));
            loadMealData(searchIngredient);
            return true;
        }
        else if (id == R.id.show_favorites) {
            favMenuItem.setVisible(false);
            homeMenuItem.setVisible(true);
            setTitle(getString(R.string.favorites_title));
            getAllFavorites();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getAllFavorites() {
        MainFavoriteViewModel mainFavoriteViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(MainFavoriteViewModel.class);
        final LiveData<List<Meal>> allFavorites = mainFavoriteViewModel.getFavorites();
        allFavorites.observe(this, new Observer<List<Meal>>() {
            @Override
            public void onChanged(@Nullable List<Meal> favorites) {
                allFavorites.removeObserver(this);
                mealAdapter.setMealData(favorites);
                if (favorites == null || favorites.size() == 0) {
                    mainBinding.tvNoFavorites.setVisibility(View.VISIBLE);
                    mainBinding.rvMeals.setVisibility(View.INVISIBLE);
                }
                else {
                    mainBinding.tvNoFavorites.setVisibility(View.INVISIBLE);
                    mainBinding.rvMeals.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}