package com.roblox.roblox_client;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.vending.billing.IInAppBillingService;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class RobloxActivity extends RobloxServiceActivity {
    protected static final String BUILDERS_CLUB_KEY = "builders_club";
    protected static final String LOGGED_IN_KEY = "logged_in";
    protected static final String PASSWORD_CHECKBOX_KEY = "password_checkbox";
    protected static final String PASSWORD_KEY = "password";
    protected static final String ROBUX_KEY = "robux";
    protected static final String TAG = "RobloxActivity";
    protected static final String THUMBNAIL_KEY = "thumbnail";
    protected static final String TICKETS_KEY = "tickets";
    protected static final String USERNAME_KEY = "username";
    protected static final String WEBVIEW_URL_KEY = "webview_url";
    IInAppBillingService mBillingService = null;
    ServiceConnection mBillingServiceConn = null;
    SharedPreferences mKeyValues = null;
    String mUsername = null;
    String mPassword = null;
    boolean mRememberPassword = false;
    boolean mLoggedIn = false;
    String mThumbnailUrl = null;
    int mRobuxBalance = 0;
    int mTicketsBalance = 0;
    boolean mIsAnyBuildersClubMember = false;

    @Override // com.roblox.roblox_client.RobloxServiceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mBillingServiceConn = new ServiceConnection() { // from class: com.roblox.roblox_client.RobloxActivity.1
            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName name) {
                RobloxActivity.this.mBillingService = null;
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName name, IBinder service) {
                RobloxActivity.this.mBillingService = IInAppBillingService.Stub.asInterface(service);
            }
        };
        Intent billingIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        bindService(billingIntent, this.mBillingServiceConn, 1);
        HttpAgent.onCreate(this);
        this.mKeyValues = getSharedPreferences("prefs", 4);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        String url = this.mKeyValues.getString(WEBVIEW_URL_KEY, "");
        HttpAgent.onPause(getCacheDir(), url);
        setIntent(null);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        HttpAgent.onResume();
        readLoginKeyValues();
        onUserInfoChanged();
        startUserInfoUpdate();
    }

    @Override // com.roblox.roblox_client.RobloxServiceActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (this.mBillingService != null) {
            unbindService(this.mBillingServiceConn);
        }
    }

    @Override // android.app.Activity
    public void onNewIntent(Intent n) {
        setIntent(n);
        super.onNewIntent(n);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        RobloxApplication.logTrimMemory(TAG, level);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "onLowMemory()");
    }

    protected void readLoginKeyValues() {
        this.mUsername = this.mKeyValues.getString(USERNAME_KEY, "");
        this.mPassword = this.mKeyValues.getString(PASSWORD_KEY, "");
        this.mRememberPassword = this.mKeyValues.getBoolean(PASSWORD_CHECKBOX_KEY, false);
        this.mLoggedIn = this.mKeyValues.getBoolean(LOGGED_IN_KEY, false);
        this.mThumbnailUrl = this.mKeyValues.getString(THUMBNAIL_KEY, "");
        this.mRobuxBalance = this.mKeyValues.getInt(ROBUX_KEY, -1);
        this.mTicketsBalance = this.mKeyValues.getInt(TICKETS_KEY, -1);
        this.mIsAnyBuildersClubMember = this.mKeyValues.getBoolean(BUILDERS_CLUB_KEY, false);
    }

    protected void writeLoginKeyValues() {
        SharedPreferences.Editor editor = this.mKeyValues.edit();
        editor.putString(USERNAME_KEY, this.mUsername);
        if (this.mRememberPassword) {
            editor.putString(PASSWORD_KEY, this.mPassword);
        } else {
            editor.remove(PASSWORD_KEY);
        }
        editor.putBoolean(PASSWORD_CHECKBOX_KEY, this.mRememberPassword);
        editor.putBoolean(LOGGED_IN_KEY, this.mLoggedIn);
        editor.putString(THUMBNAIL_KEY, this.mThumbnailUrl);
        editor.putInt(ROBUX_KEY, this.mRobuxBalance);
        editor.putInt(TICKETS_KEY, this.mTicketsBalance);
        editor.putBoolean(BUILDERS_CLUB_KEY, this.mIsAnyBuildersClubMember);
        editor.apply();
    }

    protected void onUserInfoChanged() {
    }

    protected boolean onLogin(JSONObject json) {
        try {
            JSONObject userInfo = json.optJSONObject("UserInfo");
            if (userInfo == null) {
                userInfo = json;
            }
            this.mUsername = userInfo.getString("UserName");
            this.mRobuxBalance = userInfo.getInt("RobuxBalance");
            this.mTicketsBalance = userInfo.getInt("TicketsBalance");
            this.mIsAnyBuildersClubMember = userInfo.getBoolean("IsAnyBuildersClubMember");
            this.mThumbnailUrl = userInfo.getString("ThumbnailUrl");
            this.mLoggedIn = true;
            writeLoginKeyValues();
            onUserInfoChanged();
            return true;
        } catch (JSONException e) {
            Utils.alertUnexpectedError(this, "Missing User Info");
            onLogout();
            return false;
        }
    }

    protected void onLogout() {
        this.mRobuxBalance = 0;
        this.mTicketsBalance = 0;
        this.mThumbnailUrl = null;
        this.mIsAnyBuildersClubMember = false;
        this.mPassword = "";
        this.mLoggedIn = false;
        writeLoginKeyValues();
        onUserInfoChanged();
        new LogoutAsyncTask(this, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    protected void startUserInfoUpdate() {
        if (this.mLoggedIn) {
            new UserInfoAsyncTask(this, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    private class UserInfoAsyncTask extends AsyncTask<Void, Void, Void> {
        JSONObject mJson;
        String mResponse;

        private UserInfoAsyncTask() {
            this.mResponse = null;
            this.mJson = null;
        }

        /* synthetic */ UserInfoAsyncTask(RobloxActivity robloxActivity, UserInfoAsyncTask userInfoAsyncTask) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... arg0) {
            this.mResponse = HttpAgent.readUrlToString(RobloxSettings.userInfoUrl(), null);
            if (this.mResponse != null) {
                try {
                    this.mJson = new JSONObject(this.mResponse);
                } catch (JSONException e) {
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (this.mResponse == null) {
                if (!Utils.alertIfNetworkNotConnected(RobloxActivity.this)) {
                    Utils.alertUnexpectedError(RobloxActivity.this, "UserInfoAsyncTask failed");
                }
            } else if (this.mJson == null) {
                RobloxActivity.this.onLogout();
                Utils.alertExclusively(RobloxActivity.this, R.string.LoggedOut);
            } else if (RobloxActivity.this.onLogin(this.mJson)) {
                Utils.sendAnalytics(RobloxActivity.this, "UserInfoAsyncTask", "OK");
                RobloxActivity.this.onUserInfoChanged();
            } else {
                Utils.alertUnexpectedError(RobloxActivity.this, this.mResponse);
            }
        }
    }

    private class LogoutAsyncTask extends AsyncTask<Void, Void, Void> {
        String mResponse;

        private LogoutAsyncTask() {
            this.mResponse = null;
        }

        /* synthetic */ LogoutAsyncTask(RobloxActivity robloxActivity, LogoutAsyncTask logoutAsyncTask) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... arg0) {
            this.mResponse = HttpAgent.readUrlToString(RobloxSettings.logoutUrl(), "");
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.i(RobloxActivity.TAG, "Logout: " + this.mResponse);
            if (this.mResponse == null && !Utils.alertIfNetworkNotConnected(RobloxActivity.this)) {
                Utils.alertUnexpectedError(RobloxActivity.this, "LogoutAsyncTask failed");
            }
        }
    }
}
