package com.example.android.whatscooking;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        String mealName;
        String mealIngredients;

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);

        Intent intentMain = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentMain, 0);
        views.setOnClickPendingIntent(R.id.tv_widget_ingredients, pendingIntent);

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.MEAL_PREFS), Context.MODE_PRIVATE);
        mealName = sharedPreferences.getString(context.getString(R.string.MEAL_NAME_PREFS), "");
        if (mealName == null) {
            mealName = "Selected recipe will show here";
        }
        mealIngredients = sharedPreferences.getString(context.getString(R.string.MEAL_INGREDIENT_PREFS), "");
        if (mealIngredients == null) {
            mealIngredients = " ";
        }

        views.setTextViewText(R.id.tv_widget_title, mealName);
        views.setTextViewText(R.id.tv_widget_ingredients, mealIngredients);

        Uri mapIntentUri = Uri.parse("geo:0,0?q=grocery store");
        Intent widgetClickIntent = new Intent(Intent.ACTION_VIEW, mapIntentUri);
        widgetClickIntent.setPackage("com.google.android.apps.maps");
        PendingIntent pendingIntentViewClick = PendingIntent.getActivity(context, 0, widgetClickIntent, 0);
        if (widgetClickIntent.resolveActivity(context.getPackageManager()) != null) {
            views.setOnClickPendingIntent(R.id.iv_widget_maps, pendingIntentViewClick);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

