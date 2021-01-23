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

public class JSONDetailWorker extends Worker {

    private MealInterface endpointService;

    JSONDetailWorker (@NonNull Context context, @NonNull WorkerParameters parameters) {
        super(context, parameters);
        endpointService = RetrofitClient.getInstance(context).getRetrofit().create(MealInterface.class);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        String mealDetails = "";
        try {
            int id = getInputData().getInt(String.valueOf(R.string.KEY_ID), 0);
            if (id != 0) {
                Response<JsonMeal> response = endpointService.getMealDetails(String.valueOf(id)).execute();
                JsonMeal result = response.body();
                if (result != null) {
                    mealDetails = new Gson().toJson(result);
                }
            }
        }
        catch (IOException e) {
            Log.e(Config.LOG_TAG, "Exception occurred: " + e.getLocalizedMessage());
            e.printStackTrace();
            return ListenableWorker.Result.failure();
        }

        Data output = new Data.Builder()
                .putString(String.valueOf(R.string.KEY_MEAL_RESULTS), mealDetails)
                .build();
        return ListenableWorker.Result.success(output);
    }
}
