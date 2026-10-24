package com.roblox.roblox_client;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.File;

/* loaded from: classes.dex */
public class ActivityCurlTest extends RobloxActivity {
    private static String TAG = "roblox.activitycurltest";
    private TextView textView;

    public static native String nativeGetURL(String str);

    public static native void nativeOnPause();

    public static native void nativeOnResume();

    public static native void nativeOnStart();

    public static native void nativeOnStop();

    public static native String nativePostAnalytics(String str, String str2, int i, String str3);

    static {
        System.loadLibrary("curl");
        System.loadLibrary("roblox");
    }

    @Override // com.roblox.roblox_client.RobloxActivity, com.roblox.roblox_client.RobloxServiceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        setContentView(R.layout.activity_curltest);
        this.textView = (TextView) findViewById(R.id.textView);
        this.textView.setMovementMethod(new ScrollingMovementMethod());
        this.textView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        this.textView.setBackgroundColor(-1);
    }

    public void onURLGo(View view) {
        EditText urlEdit = (EditText) findViewById(R.id.url);
        String url = urlEdit.getText().toString();
        this.textView.setText("Loading " + url + "....\n");
        String html = nativeGetURL(url);
        this.textView.append("HTML: " + html + "\n");
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        nativeOnStart();
        File cacheDir = getCacheDir();
        String cacheDirName = cacheDir.getAbsolutePath();
        Log.i(TAG, "cacheDirName = " + cacheDirName);
        RobloxSettings.setCacheDirectory(cacheDirName);
    }

    @Override // com.roblox.roblox_client.RobloxActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        nativeOnResume();
    }

    @Override // com.roblox.roblox_client.RobloxActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        nativeOnPause();
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        nativeOnStop();
    }

    @Override // com.roblox.roblox_client.RobloxActivity, android.app.Activity, android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        RobloxApplication.logTrimMemory(TAG, level);
    }
}
