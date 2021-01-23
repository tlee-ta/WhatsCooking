package com.example.android.whatscooking.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.android.whatscooking.Config;
import com.example.android.whatscooking.R;
import com.example.android.whatscooking.model.JsonArea;
import com.example.android.whatscooking.model.JsonCategory;
import com.example.android.whatscooking.model.JsonIngredient;
import com.example.android.whatscooking.network.MealInterface;
import com.example.android.whatscooking.network.RetrofitClient;

import java.io.IOException;

import retrofit2.Response;

public class JSONFilterWorker extends Worker {

    private MealInterface endpointService;

    JSONFilterWorker (@NonNull Context context, @NonNull WorkerParameters parameters) {
        super(context, parameters);
        endpointService = RetrofitClient.getInstance(context).getRetrofit().create(MealInterface.class);
    }

    @NonNull
    @Override
    public Result doWork() {
        String ingredientList = "";
        String categoryList = "";
        String areaList = "";
        try {
            Response<JsonIngredient> responseIngredient = endpointService.getAllIngredients().execute();
            JsonIngredient resultIngredientList = responseIngredient.body();
            if (resultIngredientList != null) {
                ingredientList = resultIngredientList.getFullIngredientList().toString();
                ingredientList = ingredientList.substring(1, ingredientList.length() - 1);
            }

            Response<JsonCategory> responseCategory = endpointService.getAllCategories().execute();
            JsonCategory resultCategoryList = responseCategory.body();
            if (resultCategoryList != null) {
                categoryList = resultCategoryList.getFullCategoryList().toString();
                categoryList = categoryList.substring(1, categoryList.length() - 1);
            }

            Response<JsonArea> responseArea = endpointService.getAllAreas().execute();
            JsonArea resultAreaList = responseArea.body();
            if (resultAreaList != null) {
                areaList = resultAreaList.getFullAreaList().toString();
                areaList = areaList.substring(1, areaList.length() - 1);
            }
        }
        catch (IOException e) {
            Log.e(Config.LOG_TAG, "Exception occurred: " + e.getLocalizedMessage());
            e.printStackTrace();
            return Result.failure();
        }

        Data output = new Data.Builder()
                .putString(String.valueOf(R.string.KEY_ING_RESULTS), ingredientList)
                .putString(String.valueOf(R.string.KEY_CAT_RESULTS), categoryList)
                .putString(String.valueOf(R.string.KEY_AREA_RESULTS), areaList)
                .build();
        return Result.success(output);
    }
}
