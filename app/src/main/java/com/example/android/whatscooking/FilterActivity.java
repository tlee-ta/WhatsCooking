package com.example.android.whatscooking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android.whatscooking.adapters.FilterAdapter;
import com.example.android.whatscooking.databinding.ActivityFilterBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    ActivityFilterBinding filterBinding;

    private FilterAdapter filterAdapter;

    private List<String> fullIngredientList;
    private List<String> fullCategoryList;
    private List<String> fullAreaList;

    private MenuItem ingMenuItem;
    private MenuItem catMenuItem;
    private MenuItem areaMenuItem;

    private int searchType = 0;

    private List<String> currentSelectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterBinding = DataBindingUtil.setContentView(this, R.layout.activity_filter);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent == null) {
                closeOnError();
                return;
            }
            if (intent.hasExtra(String.valueOf(R.string.KEY_INGREDIENT))) {
                final String outputIng = intent.getStringExtra(String.valueOf(R.string.KEY_INGREDIENT));
                if (outputIng == null || outputIng.isEmpty()) {
                    closeOnError();
                    return;
                }

                filterBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
                filterBinding.clSearch.setVisibility(View.VISIBLE);

                fullIngredientList = Arrays.asList(outputIng.split(", "));

                if (intent.hasExtra(String.valueOf(R.string.KEY_CATEGORY))) {
                    final String outputCat = intent.getStringExtra(String.valueOf(R.string.KEY_CATEGORY));
                    if (outputCat == null || outputCat.isEmpty()) {
                        closeOnError();
                        return;
                    }
                    fullCategoryList = Arrays.asList(outputCat.split(", "));
                }

                if (intent.hasExtra(String.valueOf(R.string.KEY_AREA))) {
                    final String outputArea = intent.getStringExtra(String.valueOf(R.string.KEY_AREA));
                    if (outputArea == null || outputArea.isEmpty()) {
                        closeOnError();
                        return;
                    }
                    fullAreaList = Arrays.asList(outputArea.split(", "));
                }
            }
            else {
                filterBinding.tvErrorMessage.setVisibility(View.VISIBLE);
                filterBinding.clSearch.setVisibility(View.INVISIBLE);
            }

            Toolbar toolbar = filterBinding.toolbarFilter;
            setSupportActionBar(toolbar);

            Context context = this;

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            filterBinding.rvIngredientsList.setLayoutManager(linearLayoutManager);

            setTitle(getString(R.string.ingredient_search_title));
            filterBinding.btnSearch.setEnabled(false);

            currentSelectedItems = new ArrayList<>();

            filterAdapter = new FilterAdapter(new FilterAdapter.OnItemCheckListener() {
                @Override
                public void onItemCheck(String item) {
                    currentSelectedItems.add(item);
                    filterBinding.btnSearch.setEnabled(true);
                }

                @Override
                public void onItemUncheck(String item) {
                    currentSelectedItems.remove(item);
                    filterBinding.btnSearch.setEnabled(false);
                }
            });
            filterBinding.rvIngredientsList.setAdapter(filterAdapter);
            filterAdapter.setDataList(fullIngredientList);

            filterBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MainActivity.class);
                    if (currentSelectedItems.size() != 0) {
                        if (searchType == 0) {
                            intent.putExtra(String.valueOf(R.string.KEY_INGREDIENT), currentSelectedItems.get(0));
                        }
                        else if (searchType == 1){
                            intent.putExtra(String.valueOf(R.string.KEY_CATEGORY), currentSelectedItems.get(0));
                        }
                        else if (searchType == 2){
                            intent.putExtra(String.valueOf(R.string.KEY_AREA), currentSelectedItems.get(0));
                        }
                    }
                    intent.putExtra(String.valueOf(R.string.KEY_SEARCH), true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter_menu, menu);
        ingMenuItem = menu.findItem(R.id.ingredient_search);
        catMenuItem = menu.findItem(R.id.category_search);
        areaMenuItem = menu.findItem(R.id.area_search);
        ingMenuItem.setVisible(false);
        catMenuItem.setVisible(true);
        areaMenuItem.setVisible(true);

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

        if (id == R.id.category_search) {
            ingMenuItem.setVisible(true);
            catMenuItem.setVisible(false);
            areaMenuItem.setVisible(true);
            setTitle(getString(R.string.category_search_title));
            filterAdapter.setDataList(fullCategoryList);
            searchType = 1;
            currentSelectedItems = new ArrayList<>();
            filterBinding.btnSearch.setEnabled(false);
            return true;
        }
        if (id == R.id.ingredient_search) {
            ingMenuItem.setVisible(false);
            catMenuItem.setVisible(true);
            areaMenuItem.setVisible(true);
            setTitle(getString(R.string.ingredient_search_title));
            filterAdapter.setDataList(fullIngredientList);
            searchType = 0;
            currentSelectedItems = new ArrayList<>();
            filterBinding.btnSearch.setEnabled(false);
            return true;
        }
        if (id == R.id.area_search) {
            ingMenuItem.setVisible(true);
            catMenuItem.setVisible(true);
            areaMenuItem.setVisible(false);
            setTitle(getString(R.string.area_search_title));
            filterAdapter.setDataList(fullAreaList);
            searchType = 2;
            currentSelectedItems = new ArrayList<>();
            filterBinding.btnSearch.setEnabled(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
    }
}