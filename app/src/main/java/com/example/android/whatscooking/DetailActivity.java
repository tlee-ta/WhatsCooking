package com.example.android.whatscooking;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.android.whatscooking.database.FavoriteDatabase;
import com.example.android.whatscooking.database.FavoriteViewModel;
import com.example.android.whatscooking.databinding.ActivityDetailBinding;
import com.example.android.whatscooking.model.JsonMeal;
import com.example.android.whatscooking.model.Meal;
import com.example.android.whatscooking.network.AppExecutors;
import com.example.android.whatscooking.network.MealInterface;
import com.google.gson.GsonBuilder;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";

    ActivityDetailBinding detailBinding;
    private MealInterface mealInterface;

    private FavoriteDatabase favoriteDatabase;
    private boolean isFavorite = false;
    private Meal meal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent == null) {
                closeOnError();
                return;
            }
            if (intent.hasExtra(String.valueOf(R.string.KEY_ID))) {
                final int id = intent.getIntExtra(String.valueOf(R.string.KEY_ID), 0);

                if (id == 0) {
                    closeOnError();
                    return;
                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                        .build();
                mealInterface = retrofit.create(MealInterface.class);
                loadMealDetailsData(id);

                favoriteDatabase = FavoriteDatabase.getInstance(getApplicationContext());
                FavoriteViewModel favoriteViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(FavoriteViewModel.class);
                final LiveData<Meal> favorite = favoriteViewModel.getFavMeal(id);
                favorite.observe(this, new Observer<Meal>() {
                    @Override
                    public void onChanged(Meal favMeal) {
                        favorite.removeObserver(this);
                        isFavorite = favMeal != null;
                        updateFavoriteUI();
                    }
                });

                detailBinding.btnFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateFavorite(meal);
                    }
                });
            }
        }
    }

    private void loadMealDetailsData(int id) {
        Call<JsonMeal> callMealDetails = mealInterface.getMealDetails(String.valueOf(id));

        detailBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
        detailBinding.loadingBar.setVisibility(View.VISIBLE);
        detailBinding.clDetails.setVisibility(View.INVISIBLE);

        callMealDetails.enqueue(new Callback<JsonMeal>() {
            @Override
            public void onResponse(Call<JsonMeal> call, Response<JsonMeal> response) {
                if (!response.isSuccessful()) {
                    Log.i("Main", String.valueOf(response.code()));
                    detailBinding.tvErrorMessage.setVisibility(View.VISIBLE);
                    detailBinding.loadingBar.setVisibility(View.INVISIBLE);
                    detailBinding.clDetails.setVisibility(View.INVISIBLE);
                    return;
                }
                detailBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
                detailBinding.loadingBar.setVisibility(View.INVISIBLE);
                detailBinding.clDetails.setVisibility(View.VISIBLE);

                assert response.body() != null;
                meal = response.body().getMealList().get(0);
                populateUI(meal);
            }

            @Override
            public void onFailure(Call<JsonMeal> call, Throwable t) {
                Log.i("Main", Objects.requireNonNull(t.getMessage()));
                detailBinding.tvErrorMessage.setVisibility(View.VISIBLE);
                detailBinding.loadingBar.setVisibility(View.INVISIBLE);
                detailBinding.clDetails.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void populateUI(Meal mealData) {
        setTitle(mealData.getName());
        Glide.with(getBaseContext())
                .load(mealData.getThumbnail())
                .into(detailBinding.ivMeal);

        detailBinding.tvIngredients.setText(mealData.getIngredients());
        detailBinding.tvInstructions.setText(mealData.getInstructions());

        detailBinding.ivPlayVideo.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mealData.getYoutube()));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        final SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.MEAL_PREFS), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.MEAL_NAME_PREFS), mealData.getName());
        editor.putString(getString(R.string.MEAL_INGREDIENT_PREFS), mealData.getIngredients());
        editor.apply();

        sendRecipeToWidget(this);

    }

    public void sendRecipeToWidget(Context context) {
        Intent intent = new Intent(context, IngredientsWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, IngredientsWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    public void updateFavorite(final Meal meal) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (isFavorite) {
                    favoriteDatabase.favoriteDao().deleteFavMeal(meal);
                    isFavorite = false;
                } else {
                    favoriteDatabase.favoriteDao().insertFavMeal(meal);
                    isFavorite = true;
                }
                updateFavoriteUI();
            }
        });
    }

    private void updateFavoriteUI() {
        if (isFavorite) {
            detailBinding.btnFavorite.setImageResource(R.drawable.heart_icon_full);
        }
        else {
            detailBinding.btnFavorite.setImageResource(R.drawable.heart_icon_empty);
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
    }
}