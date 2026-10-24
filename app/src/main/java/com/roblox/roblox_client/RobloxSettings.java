package com.roblox.roblox_client;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import java.io.File;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class RobloxSettings {
    private static final boolean IS_INTERNAL_BUILD = false;
    private static final String TAG = "roblox.config";
    private static Context mContext = null;
    private static String mVersion = "version missing";
    private static boolean mFakeUserAgent = true;
    private static String mFakeUserAgentString = null;
    private static String mActualUserAgentString = null;
    private static String mUserAgentSuffix = null;
    private static String mBaseUrl = null;
    private static String mBaseMobileUrl = null;
    private static String mCacheDirectory = null;

    private static native void nativeInitBreakpad();

    private static native void nativeInitFastLog();

    private static native void nativeSetBaseUrl(String str);

    private static native void nativeSetCacheDirectory(String str);

    private static native void nativeSetHttpCacheDirectory(String str);

    private static native void nativeSetHttpRobloxCookieFile(String str);

    private static native void nativeSetPlatformUserAgent(String str);

    private static native void nativeSetRobloxVersion(String str);

    static String version() {
        return mVersion;
    }

    static boolean isInternalBuild() {
        return IS_INTERNAL_BUILD;
    }

    static boolean isTablet() {
        return true;
    }

    static String baseUrl() {
        return "http://" + (isTablet() ? mBaseUrl : mBaseMobileUrl);
    }

    static String baseUrlSecure() {
        return "https://" + (isTablet() ? mBaseUrl : mBaseMobileUrl);
    }

    static String userAgentSuffix() {
        return mUserAgentSuffix;
    }

    static String userAgentNotFaked() {
        if (mActualUserAgentString != null) {
            return mActualUserAgentString;
        }
        long memory;
        try {
            memory = Utils.getDeviceTotalMegabytes();
        } catch (Throwable t) {
            memory = 0;
        }
        Point screenSize = Utils.getScreenSize(mContext);
        Point dpi = Utils.getScreenDpi(mContext);
        String deviceName = Utils.getDeviceName();
        String androidVersion = Build.VERSION.RELEASE;
        String webKitVersion = Utils.getWebkitVersion(mContext);
        mActualUserAgentString = Utils.format("Mozilla/5.0 (%dMB; %dx%d; %dx%d; %s; %s) %s (KHTML, like Gecko)  ROBLOX Android App %s", Long.valueOf(memory), Integer.valueOf(screenSize.x), Integer.valueOf(screenSize.y), Integer.valueOf(dpi.x), Integer.valueOf(dpi.y), deviceName, androidVersion, webKitVersion, mVersion);
        return mActualUserAgentString;
    }

    static String userAgent() {
        return mFakeUserAgent ? mFakeUserAgentString : userAgentNotFaked();
    }

    static String loginUrl() {
        return String.valueOf(baseUrlSecure()) + "mobileapi/login";
    }

    static String logoutUrl() {
        return String.valueOf(baseUrlSecure()) + "mobileapi/logout";
    }

    static String searchGamesUrl(String term) {
        return Utils.format("%sBrowse.aspx?name=%s", baseUrl(), term);
    }

    static String searchPeopleUrl(String term) {
        return Utils.format("%speople?search=%s", baseUrl(), term);
    }

    static String userInfoUrl() {
        return Utils.format("%smobileapi/userinfo", baseUrlSecure());
    }

    static String signUpUrl() {
        return String.valueOf(baseUrl()) + "mobileapi/signup";
    }

    static String signUpUrlArgs(String userName, String password, String gender, String dateOfBirth) {
        return Utils.format("userName=%s&password=%s&gender=%s&dateOfBirth=%s", userName, password, gender, dateOfBirth);
    }

    static String usernameCheckUrl(String userName) {
        return Utils.format("%s/UserCheck/checkifinvalidusernameforsignup?username=%s", baseUrl(), userName);
    }

    static String recommendUsernameUrl(String userName) {
        return Utils.format("%sUserCheck/getrecommendedusername?usernameToTry=%s", baseUrl(), userName);
    }

    static String passwordCheckUrl() {
        return String.valueOf(baseUrl()) + "UserCheck/validatepasswordforsignup";
    }

    static String passwordCheckUrlArgs(String userName, String password) {
        return Utils.format("password=%s&username=%s", password, userName);
    }

    static String gamesUrl() {
        return String.valueOf(baseUrl()) + "games/list";
    }

    static String catalogUrl() {
        return String.valueOf(baseUrl()) + (isTablet() ? "catalog/" : "catalog/");
    }

    static String inventoryUrl() {
        return String.valueOf(baseUrl()) + (isTablet() ? "My/Character.aspx" : "inventory");
    }

    static String buildersClubUrl() {
        return String.valueOf(baseUrl()) + "mobile-app-upgrades/";
    }

    static String profileUrl() {
        return String.valueOf(baseUrl()) + (isTablet() ? "User.aspx" : "");
    }

    static String messagesUrl() {
        return String.valueOf(baseUrl()) + (isTablet() ? "My/Messages.aspx#Inbox" : "inbox");
    }

    static String upgradeCheckUrl() {
        return String.valueOf(baseUrl()) + "mobileapi/check-app-version?appVersion=AppAndroidV" + version();
    }

    static String httpCacheDir() {
        return "httplib";
    }

    static String httpRbxCookieFile() {
        return "robloxcookies.txt";
    }

    static void loadConfigFile(Context c) throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
        mContext = c;
        try {
            JSONObject j = Utils.loadJson(c, R.raw.roblox_settings);
            mFakeUserAgent = j.getBoolean("FakeUserAgent");
            mFakeUserAgentString = j.getString("FakeUserAgentString");
            mUserAgentSuffix = j.getString("UserAgentSuffix");
            mBaseUrl = j.getString("BaseUrl");
            mBaseMobileUrl = j.getString("BaseMobileUrl");
            try {
                PackageInfo pinfo = ((Application) mContext).getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                mVersion = pinfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException("Cannot Read Package Info for Version String: " + e.getMessage());
            }
        } catch (JSONException e2) {
            throw new IOException("Cannot parse JSON resource: res/raw/roblox_settings: " + e2.getMessage());
        }
    }

    public static void setCacheDirectory(String directory) {
        mCacheDirectory = directory;
    }

    public static void setBaseUrl(String url) {
        mBaseUrl = url;
    }

    public static void updateNativeSettings() {
        if (baseUrl() == null) {
            throw new NullPointerException("Missing baseUrl()");
        }
        nativeSetBaseUrl(baseUrl());
        if (mCacheDirectory == null) {
            throw new NullPointerException("Missing cacheDirectory");
        }
        File cacheDir = new File(mCacheDirectory);
        String cacheDirName = cacheDir.getAbsolutePath();
        nativeSetCacheDirectory(cacheDirName);
        nativeSetPlatformUserAgent(userAgent());
        nativeSetRobloxVersion(version());
        nativeInitBreakpad();
        nativeSetHttpCacheDirectory(httpCacheDir());
        nativeSetHttpRobloxCookieFile(httpRbxCookieFile());
        nativeInitFastLog();
    }
}
