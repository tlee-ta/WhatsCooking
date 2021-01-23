package com.example.android.whatscooking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.android.whatscooking.adapters.MealAdapter;
import com.example.android.whatscooking.database.MainFavoriteViewModel;
import com.example.android.whatscooking.databinding.ActivityMainBinding;
import com.example.android.whatscooking.model.JsonMeal;
import com.example.android.whatscooking.model.Meal;
import com.example.android.whatscooking.worker.JSONFilterWorker;
import com.example.android.whatscooking.worker.JSONMainWorker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MealAdapter.MealAdapterOnClickHandler {

    private String searchIngredient;
    private String searchCategory;
    private String searchArea;
    private String deviceLoc;

    ActivityMainBinding mainBinding;

    private MealAdapter mealAdapter;
    private List<Meal> mealsList;

    private String fullIngredientsList;
    private String fullCategoriesList;
    private String fullAreasList;

    private MenuItem favMenuItem;
    private MenuItem homeMenuItem;

    private Boolean searching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Toolbar toolbar = mainBinding.toolbarMain;
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent == null) {
                return;
            }
            if (intent.hasExtra(String.valueOf(R.string.KEY_INGREDIENT))) {
                searchIngredient = intent.getStringExtra(String.valueOf(R.string.KEY_INGREDIENT));
            } else if (intent.hasExtra(String.valueOf(R.string.KEY_CATEGORY))) {
                searchCategory = intent.getStringExtra(String.valueOf(R.string.KEY_CATEGORY));
            } else if (intent.hasExtra(String.valueOf(R.string.KEY_AREA))) {
                searchArea = intent.getStringExtra(String.valueOf(R.string.KEY_AREA));
            }
            if (intent.hasExtra(String.valueOf(R.string.KEY_SEARCH))) {
                searching = intent.getBooleanExtra(String.valueOf(R.string.KEY_SEARCH), false);
            }

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int gridCount = (int) displayMetrics.widthPixels / 500;

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, gridCount);

            mainBinding.rvMeals.setLayoutManager(gridLayoutManager);
            mainBinding.rvMeals.setHasFixedSize(true);

            mealAdapter = new MealAdapter(this, this);
            mainBinding.rvMeals.setAdapter(mealAdapter);

            OneTimeWorkRequest oneTimeWorkRequestFilter = new OneTimeWorkRequest.Builder(
                    JSONFilterWorker.class)
                    .build();
            WorkManager.getInstance(this).enqueue(oneTimeWorkRequestFilter);
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(oneTimeWorkRequestFilter.getId()).observe(this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(WorkInfo workInfo) {
                    if (workInfo != null) {
                        WorkInfo.State state = workInfo.getState();
                        if (state == WorkInfo.State.SUCCEEDED) {
                            fullIngredientsList = workInfo.getOutputData().getString(String.valueOf(R.string.KEY_ING_RESULTS));
                            fullCategoriesList = workInfo.getOutputData().getString(String.valueOf(R.string.KEY_CAT_RESULTS));
                            fullAreasList = workInfo.getOutputData().getString(String.valueOf(R.string.KEY_AREA_RESULTS));
                        }
                    }
                }
            });

            if (searching) {
                loadMealData(searchIngredient, searchCategory, searchArea);
            }
            else {
                getLastLocation();
            }
        }
    }

    private void loadMealData(String ingredient, String category, String area) {
        Data myData;
        if (ingredient != null) {
            myData = new Data.Builder()
                    .putString(String.valueOf(R.string.KEY_INGREDIENT), ingredient)
                    .build();
        } else if (category != null) {
            myData = new Data.Builder()
                    .putString(String.valueOf(R.string.KEY_CATEGORY), category)
                    .build();
        } else if (area != null) {
            myData = new Data.Builder()
                    .putString(String.valueOf(R.string.KEY_AREA), area)
                    .build();
        } else {
            mainBinding.tvErrorMessage.setVisibility(View.VISIBLE);
            mainBinding.loadingBar.setVisibility(View.INVISIBLE);
            mainBinding.rvMeals.setVisibility(View.INVISIBLE);
            return;
        }

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(
                JSONMainWorker.class)
                .setInputData(myData)
                .build();
        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null) {
                    WorkInfo.State state = workInfo.getState();
                    if (state == WorkInfo.State.RUNNING) {
                        mainBinding.loadingBar.setVisibility(View.VISIBLE);
                        mainBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
                        mainBinding.rvMeals.setVisibility(View.INVISIBLE);
                        mainBinding.tvNoFavorites.setVisibility(View.INVISIBLE);
                    } else if (state == WorkInfo.State.SUCCEEDED) {
                        mainBinding.loadingBar.setVisibility(View.INVISIBLE);

                        String output = workInfo.getOutputData().getString(String.valueOf(R.string.KEY_MEAL_RESULTS));
                        if (output == null) {
                            mainBinding.tvNoResults.setVisibility(View.VISIBLE);
                            mainBinding.rvMeals.setVisibility(View.INVISIBLE);
                        } else {
                            mealsList = new GsonBuilder().create().fromJson(output, JsonMeal.class).getMealList();
                            mealAdapter.setMealData(mealsList);
                            mainBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
                            mainBinding.rvMeals.setVisibility(View.VISIBLE);
                        }
                    } else if (state == WorkInfo.State.FAILED) {
                        mainBinding.tvErrorMessage.setVisibility(View.VISIBLE);
                        mainBinding.loadingBar.setVisibility(View.INVISIBLE);
                        mainBinding.rvMeals.setVisibility(View.INVISIBLE);
                    }
                }
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
            intent.putExtra(String.valueOf(R.string.KEY_INGREDIENT), fullIngredientsList);
            intent.putExtra(String.valueOf(R.string.KEY_CATEGORY), fullCategoriesList);
            intent.putExtra(String.valueOf(R.string.KEY_AREA), fullAreasList);
            startActivity(intent);
            return true;
        } else if (id == R.id.show_main) {
            favMenuItem.setVisible(true);
            homeMenuItem.setVisible(false);
            setTitle(getString(R.string.app_name));
            mealAdapter.setMealData(mealsList);
            return true;
        } else if (id == R.id.show_favorites) {
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
                } else {
                    mainBinding.tvNoFavorites.setVisibility(View.INVISIBLE);
                    mainBinding.rvMeals.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void getArea(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            String countryName = "";
            if (addresses.size() > 0) {
                countryName = addresses.get(0).getCountryName();
            }

            switch (countryName) {
                case "United States":
                    deviceLoc = "American";
                    break;
                case "United Kingdom":
                    deviceLoc = "British";
                    break;
                case "Canada":
                    deviceLoc = "Canadian";
                    break;
                case "China":
                    deviceLoc = "Chinese";
                    break;
                case "Netherlands":
                    deviceLoc = "Dutch";
                    break;
                case "Egypt":
                    deviceLoc = "Egyptian";
                    break;
                case "France":
                    deviceLoc = "French";
                    break;
                case "Greece":
                    deviceLoc = "Greek";
                    break;
                case "India":
                    deviceLoc = "Indian";
                    break;
                case "Ireland":
                    deviceLoc = "Irish";
                    break;
                case "Italy":
                    deviceLoc = "Italian";
                    break;
                case "Jamaica":
                    deviceLoc = "Jamaican";
                    break;
                case "Japan":
                    deviceLoc = "Japanese";
                    break;
                case "Kenya":
                    deviceLoc = "Kenyan";
                    break;
                case "Malaysia":
                    deviceLoc = "Malaysian";
                    break;
                case "Mexico":
                    deviceLoc = "Mexican";
                    break;
                case "Morocco":
                    deviceLoc = "Moroccan";
                    break;
                case "Poland":
                    deviceLoc = "Polish";
                    break;
                case "Russia":
                    deviceLoc = "Russian";
                    break;
                case "Spain":
                    deviceLoc = "Spanish";
                    break;
                case "Thailand":
                    deviceLoc = "Thai";
                    break;
                case "Tunisia":
                    deviceLoc = "Tunisian";
                    break;
                case "Turkey":
                    deviceLoc = "Turkish";
                    break;
                case "Vietnam":
                    deviceLoc = "Vietnamese";
                    break;
                default:
                    deviceLoc = "Unknown";
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getLastLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }
        else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                getArea(location.getLatitude(), location.getLongitude());
                                loadMealData(searchIngredient, searchCategory, deviceLoc);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(Config.LOG_TAG, "Error trying to get last location, setting to unknown");
                            e.printStackTrace();
                            loadMealData(searchIngredient, searchCategory, deviceLoc);
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
            else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    };
}