package com.example.android.whatscooking;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android.whatscooking.adapters.FilterAdapter;
import com.example.android.whatscooking.databinding.ActivityFilterBinding;
import com.example.android.whatscooking.model.Ingredient;
import com.example.android.whatscooking.model.JsonIngredient;
import com.example.android.whatscooking.network.MealInterface;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilterActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";

    ActivityFilterBinding filterBinding;
    private FilterAdapter filterAdapter;
    private MealInterface mealInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterBinding = DataBindingUtil.setContentView(this, R.layout.activity_filter);

        Context context = this;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        filterBinding.rvIngredientsList.setLayoutManager(linearLayoutManager);

        setTitle(getString(R.string.filter_search));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                .build();
        mealInterface = retrofit.create(MealInterface.class);
        loadFilterData();

        List<Ingredient> currentSelectedItems = new ArrayList<>();

        filterAdapter = new FilterAdapter(new FilterAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(Ingredient item) {
                currentSelectedItems.add(item);
            }

            @Override
            public void onItemUncheck(Ingredient item) {
                currentSelectedItems.remove(item);
            }
        });
        filterBinding.rvIngredientsList.setAdapter(filterAdapter);

        filterBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                if (currentSelectedItems.size() != 0) {
                    intent.putExtra(String.valueOf(R.string.KEY_INGREDIENT), currentSelectedItems.get(0).getName());
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadFilterData(){
        Call<JsonIngredient> callIngredients = mealInterface.getAllIngredients();

        filterBinding.loadingBar.setVisibility(View.VISIBLE);
        filterBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
        filterBinding.clSearch.setVisibility(View.INVISIBLE);

        callIngredients.enqueue(new Callback<JsonIngredient>() {
            @Override
            public void onResponse(Call<JsonIngredient> call, Response<JsonIngredient> response) {
                if (!response.isSuccessful()) {
                    Log.i("Main", String.valueOf(response.code()));
                    filterBinding.tvErrorMessage.setVisibility(View.VISIBLE);
                    filterBinding.loadingBar.setVisibility(View.INVISIBLE);
                    filterBinding.clSearch.setVisibility(View.INVISIBLE);
                    return;
                }
                filterBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
                filterBinding.loadingBar.setVisibility(View.INVISIBLE);
                filterBinding.clSearch.setVisibility(View.VISIBLE);

                assert response.body() != null;
                List<Ingredient> ingredientList = response.body().getFullIngredientList();
                filterAdapter.setIngredientDataList(ingredientList);
            }

            @Override
            public void onFailure(Call<JsonIngredient> call, Throwable t) {
                Log.i("Main", Objects.requireNonNull(t.getMessage()));
                filterBinding.tvErrorMessage.setVisibility(View.VISIBLE);
                filterBinding.loadingBar.setVisibility(View.INVISIBLE);
                filterBinding.clSearch.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAdapter.filter(newText);
                return true;
            }
        });

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
}