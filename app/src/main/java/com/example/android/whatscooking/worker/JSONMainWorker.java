package com.example.android.whatscooking.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.android.whatscooking.Config;
import com.example.android.whatscooking.R;
import com.example.android.whatscooking.model.JsonMeal;
import com.example.android.whatscooking.network.MealInterface;
import com.example.android.whatscooking.network.RetrofitClient;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Response;

public class JSONMainWorker extends Worker {

    private MealInterface endpointService;

    JSONMainWorker (@NonNull Context context, @NonNull WorkerParameters parameters) {
        super(context, parameters);
        endpointService = RetrofitClient.getInstance(context).getRetrofit().create(MealInterface.class);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        String mealList = "";
        try {
            String ingredient = getInputData().getString(String.valueOf(R.string.KEY_INGREDIENT));
            String category = getInputData().getString(String.valueOf(R.string.KEY_CATEGORY));
            String area = getInputData().getString(String.valueOf(R.string.KEY_AREA));
            Response<JsonMeal> response = null;
            if (ingredient != null) {
                response = endpointService.getMealsIngredient(ingredient).execute();
            }
            else if (category != null) {
                response = endpointService.getMealsCategory(category).execute();
            }
            else if (area != null) {
                response = endpointService.getMealsArea(area).execute();
            }

            assert response != null;
            JsonMeal resultList = response.body();
            if (resultList != null) {
                mealList = new Gson().toJson(resultList);
            }
        }
        catch (IOException e) {
            Log.e(Config.LOG_TAG, "Exception occurred: " + e.getLocalizedMessage());
            e.printStackTrace();
            return ListenableWorker.Result.failure();
        }

        Data output = new Data.Builder()
                .putString(String.valueOf(R.string.KEY_MEAL_RESULTS), mealList)
                .build();
        return ListenableWorker.Result.success(output);
    }
}
