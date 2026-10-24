package com.roblox.roblox_client;

import android.app.Application;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import java.io.IOException;

/* loaded from: classes.dex */
public class RobloxApplication extends Application {
    private static String TAG = "roblox.app";
    static String mCriticalError = null;
    private Tracker mAndroidTracker = null;

    public void setCriticalErrorOccured(String msg) {
        mCriticalError = msg;
        Log.e(TAG, "************************************************************");
        Log.e(TAG, "***  CriticalError: " + mCriticalError);
        Log.e(TAG, "************************************************************");
        throw new RuntimeException(msg);
    }

    public boolean checkShowCriticalError() {
        if (mCriticalError != null) {
            Log.e(TAG, "***  CriticalError: " + mCriticalError);
        }
        return mCriticalError != null;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        try {
            RobloxSettings.loadConfigFile(getApplicationContext());
        } catch (IOException | PackageManager.NameNotFoundException e) {
            setCriticalErrorOccured(e.getMessage());
        }
        String ua;
        try {
            ua = RobloxSettings.userAgent();
        } catch (Exception e) {
            ua = "";
        }
        Log.i(TAG, ua);
    }

    synchronized Tracker getAndroidTracker() {
        if (this.mAndroidTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            this.mAndroidTracker = analytics.newTracker(R.xml.android_tracker);
        }
        return this.mAndroidTracker;
    }

    static void logTrimMemory(String TAG2, int level) {
        switch (level) {
            case 20:
                Log.w(TAG2, "TRIM_MEMORY_UI_HIDDEN");
                break;
            case 40:
                Log.w(TAG2, "TRIM_MEMORY_BACKGROUND");
                break;
            case 60:
                Log.w(TAG2, "TRIM_MEMORY_MODERATE");
                break;
            case 80 /* ServerConfig.DEFAULT_PLAYBACK_PORT_WEB */:
                Log.w(TAG2, "TRIM_MEMORY_COMPLETE");
                break;
        }
    }
}
