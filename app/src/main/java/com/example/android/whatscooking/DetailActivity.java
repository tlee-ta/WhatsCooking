package com.example.android.whatscooking;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.example.android.whatscooking.database.FavoriteDatabase;
import com.example.android.whatscooking.database.FavoriteViewModel;
import com.example.android.whatscooking.databinding.ActivityDetailBinding;
import com.example.android.whatscooking.model.JsonMeal;
import com.example.android.whatscooking.model.Meal;
import com.example.android.whatscooking.network.AnalyticsApplication;
import com.example.android.whatscooking.network.AppExecutors;
import com.example.android.whatscooking.worker.JSONDetailWorker;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.GsonBuilder;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding detailBinding;

    private FavoriteDatabase favoriteDatabase;
    private boolean isFavorite = false;
    private Meal meal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Toolbar toolbar = detailBinding.toolbarDetail;
        setSupportActionBar(toolbar);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();

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

                mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Meal")
                    .setValue(id)
                    .build());

                detailBinding.loadingBar.setVisibility(View.VISIBLE);
                detailBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
                detailBinding.clDetails.setVisibility(View.INVISIBLE);

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

                Data data = new Data.Builder()
                        .putInt(String.valueOf(R.string.KEY_ID), id)
                        .build();

                OneTimeWorkRequest oneTimeWorkRequestDetails = new OneTimeWorkRequest.Builder(
                        JSONDetailWorker.class)
                        .setInputData(data)
                        .build();
                WorkManager.getInstance(this).enqueue(oneTimeWorkRequestDetails);
                WorkManager.getInstance(this).getWorkInfoByIdLiveData(oneTimeWorkRequestDetails.getId()).observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null) {
                            WorkInfo.State state = workInfo.getState();
                            if (state == WorkInfo.State.RUNNING) {
                                detailBinding.loadingBar.setVisibility(View.VISIBLE);
                                detailBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
                                detailBinding.clDetails.setVisibility(View.INVISIBLE);
                            } else if (state == WorkInfo.State.SUCCEEDED) {
                                detailBinding.loadingBar.setVisibility(View.INVISIBLE);

                                String output = workInfo.getOutputData().getString(String.valueOf(R.string.KEY_MEAL_RESULTS));
                                if (output == null) {
                                    detailBinding.clDetails.setVisibility(View.INVISIBLE);
                                    detailBinding.tvErrorMessage.setVisibility(View.VISIBLE);
                                } else {
                                    detailBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
                                    detailBinding.loadingBar.setVisibility(View.INVISIBLE);
                                    detailBinding.clDetails.setVisibility(View.VISIBLE);

                                    meal = new GsonBuilder().create().fromJson(output, JsonMeal.class).getMealList().get(0);
                                    populateUI(meal);
                                }
                            } else if (state == WorkInfo.State.FAILED) {
                                detailBinding.tvErrorMessage.setVisibility(View.VISIBLE);
                                detailBinding.loadingBar.setVisibility(View.INVISIBLE);
                                detailBinding.clDetails.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
            }
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.maps) {
            Uri mapIntentUri = Uri.parse("geo:0,0?q=grocery store");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
    }
}