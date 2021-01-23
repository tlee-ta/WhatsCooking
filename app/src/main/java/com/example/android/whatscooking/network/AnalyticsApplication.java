package com.example.android.whatscooking.network;

import android.app.Application;

import com.example.android.whatscooking.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsApplication extends Application {

    private static GoogleAnalytics googleAnalytics;
    private static Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        googleAnalytics = GoogleAnalytics.getInstance(this);
    }

    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            tracker = googleAnalytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }
}
